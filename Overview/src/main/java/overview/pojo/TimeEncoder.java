package overview.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class TimeEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext context, Object msg, ChannelPromise promise){

        UnixTime unixTime = (UnixTime)msg;
        ByteBuf buf = context.alloc().buffer(4);
        buf.writeInt((int)unixTime.getValue());
        context.write(buf, promise);
    }
}
