package chat.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler  extends SimpleChannelInboundHandler {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
      System.out.println((String)msg);
    }
}
