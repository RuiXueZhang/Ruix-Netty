package overview.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg){
        // discard the received data silently
        ByteBuf in = (ByteBuf)msg;
        try {
            while (in.isReadable()) {
                System.out.println(in.readByte());
                System.out.flush();
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause){
        //Close the connection when an exception rasied
        cause.printStackTrace();
        context.close();
    }


}
