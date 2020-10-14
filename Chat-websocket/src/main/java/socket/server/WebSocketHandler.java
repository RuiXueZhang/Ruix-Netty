package socket.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public WebSocketHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        // 增加消息引用计数，并将他写到channelGroup 中所有已连接的客户端
        Channel channel  = ctx.channel();
        //自己写的消息不用发送给自己，所以删除自己的channel
        group.remove(channel);
        group.writeAndFlush(msg.retain());
        group.add(channel);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{

        //是否握手成功,升级为Websocket协议
        if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
           ctx.pipeline().remove(HttpRequestHandler.class);
           group.writeAndFlush(new TextWebSocketFrame("Client" + ctx.channel() + "joined"));
           group.add(ctx.channel());
        }else if(evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
            if(idleStateEvent.state() == IdleState.READER_IDLE){
                group.remove(ctx.channel());
                ctx.writeAndFlush(new TextWebSocketFrame("由于您长时间不在线，系统已经将您自动踢下线"));
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
