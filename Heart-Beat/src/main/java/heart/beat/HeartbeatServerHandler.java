package heart.beat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

public class HeartbeatServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final ByteBuf HEARTBEAT_SEQYENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object obj) throws Exception {

        if(obj instanceof IdleStateEvent){

            IdleStateEvent event = (IdleStateEvent)obj;

            String type = "";
            if(event.state() == IdleState.READER_IDLE){
                type = "read idle";
            }else if(event.state() == IdleState.WRITER_IDLE){
                type = "write idle";
            }else if(event.state() == IdleState.ALL_IDLE){
                type = "all idle";
            }

            context.writeAndFlush(HEARTBEAT_SEQYENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

            System.out.println(context.channel().remoteAddress() + "超时类型" + type);

        }else {
            super.userEventTriggered(context, obj);
        }

    }

    @Override
        protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
