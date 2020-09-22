package socket.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUrl;

    private static File INDEX = null;

    static {
        //获取正在运行的jar文件的路径
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();

        try {
            String path = location.toURI() + "WebSockerChatClient.html";
            path = path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate WebSocketChatClient.html");
        }
    }

    public HttpRequestHandler(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //如果请求了websocket，服务升级，增加引用计数（调用retain()),并将他传递给下一个ChannelHandler
        //由于调用完 messageRecived之后，资源会被release方法释放。所以需要调用 retain()方法。保留资源
        if(ctx instanceof FullHttpRequest){
            ctx.fireChannelRead(request.retain());
        }else {
            //处理 100 continue 请求以符合 Http  1.1 规范
            if(HttpHeaderUtil.is100ContinueExpected(request)){
               send100Continue(ctx);
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

            HttpHeaders headers = response.headers();
            //在该Http 头信息被设置之后， HttpRequestHandler 将会回写一个HttpResponse 给客户端
            headers.set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);

            if(keepAlive){
                headers.setLong(HttpHeaderNames.CONTENT_LENGTH, randomAccessFile.length());
                headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            ctx.write(response);

            //将对应网页写给客户端
            if(ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length()));
            }else {
                ctx.write(new ChunkedNioFile(randomAccessFile.getChannel()));
            }

            //写并刷新LastHttpContext 到客户端，并标记响应完成
            ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if(!keepAlive){
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }

            randomAccessFile.close();
        }
    }

    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception{
        Channel incoming = context.channel();
        System.out.println("Client" + incoming.remoteAddress() + "异常");
        cause.printStackTrace();
        context.close();
    }

}
