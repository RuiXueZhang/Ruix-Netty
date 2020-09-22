package overview.echo;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg){
        // ChannelHandlerContext 为我们提供多种可以触发 io的事件 和操作
        //write 操作不会将消息写入网络，而是在内部缓冲 通过 flush 刷新到网络上
        context.write(msg);
        context.flush();
    }

}
