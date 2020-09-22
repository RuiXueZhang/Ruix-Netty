package chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) {

        Channel incoming = channelHandlerContext.channel();
        channels.writeAndFlush("[Service]-" + incoming.remoteAddress() + "加入\n");
        channels.add(incoming);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context){
        Channel incoming = context.channel();
        channels.writeAndFlush("[Service]-" + incoming.remoteAddress() + "离开\n");

        // 一个关闭的channel会自动remove，所以不需要我们手动关闭
    }


    @Override
    protected void messageReceived(ChannelHandlerContext context, String s) throws Exception {

        Channel incoming = context.channel();
        for(Channel channel : channels){
            if(channel != incoming) {
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + s + "\n");
            }else {
                channel.writeAndFlush("you"  + s + "\n");
            }
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext context){
        Channel incoming = context.channel();
        System.out.println("chatClient" + incoming.remoteAddress() + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext context){
        Channel incoming = context.channel();
        System.out.println("chatClient" + incoming.remoteAddress() + "离线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause){
        Channel incoming = context.channel();
        System.out.println("chatClient" + incoming.remoteAddress() + "异常");
        cause.printStackTrace();
        context.close();
    }

}
