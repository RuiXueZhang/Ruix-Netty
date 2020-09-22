package overview.pojo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext context){
        ChannelFuture future = context.writeAndFlush(new UnixTime());
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
