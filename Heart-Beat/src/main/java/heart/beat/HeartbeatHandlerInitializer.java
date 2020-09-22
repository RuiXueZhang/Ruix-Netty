package heart.beat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartbeatHandlerInitializer extends ChannelInitializer<Channel> {
    //定义超时时间

    private static final int READ_IDLE_TIME_OUT = 4;  //读超时

    private static final int WRITE_IDLE_TIME_OUT = 5; //写超时

    private static final int ALL_IDLE_TIME_out = 7; //所有超时

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME_OUT, WRITE_IDLE_TIME_OUT, ALL_IDLE_TIME_out, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatServerHandler());
    }
}
