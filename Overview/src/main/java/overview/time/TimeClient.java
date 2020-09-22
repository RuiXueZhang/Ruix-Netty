package overview.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import overview.bufferResolution.ByteToMessage;

public class TimeClient {

    private int port;

    public TimeClient(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new TimeClientHandler(), new ByteToMessage());
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);

        //start client
        ChannelFuture future = b.connect("localhost", 8080).sync();
        //等待 连接关闭
        future.channel().closeFuture().sync();

    }

    public static void main(String[] args) throws InterruptedException {
        new TimeClient(8080).run();
    }
}
