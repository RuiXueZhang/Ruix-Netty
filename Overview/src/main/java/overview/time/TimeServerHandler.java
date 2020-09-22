package overview.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * channelActive 方法在 连接建立后 准备产生流量 时 被执行，
     * @param context
     */
    @Override
    public void channelActive(final ChannelHandlerContext context){


        // 发送一个新消息，我们需要 分配一个新的缓冲期来包含消息，当前程序返回一个 32 位的整数，需要 4个字节的 缓冲区
        final ByteBuf time = context.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis()/100L + 2208988800L));

        //ChannelFuture 表示将要发生 还没有发生的io操作
        final ChannelFuture future = context.writeAndFlush(time);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                 assert future == channelFuture;
                 context.close();
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause){
        cause.printStackTrace();
        context.close();
    }


}
