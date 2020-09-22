package overview.pojo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg){
        UnixTime time = (UnixTime)msg;
        System.out.println(time);
        context.close();
    }
}
