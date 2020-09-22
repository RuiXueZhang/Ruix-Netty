package overview.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Builder;

import java.util.Date;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {

        ByteBuf buf = (ByteBuf) msg;
        try {
            long currentTime = (buf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTime));

            context.close();
        } finally {
            buf.release();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause){
        cause.printStackTrace();
        context.close();
    }
}
