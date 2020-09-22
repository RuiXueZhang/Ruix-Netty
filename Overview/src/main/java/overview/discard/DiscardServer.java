package overview.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {

    private  int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception{

        /**
         *   bossGroup  接收进入的连接
         *   workerGroup  一旦boss接受了连接并注册到了workerGroup，就开始处理连接的流量
         *   EventLoopGroup 的实现  可以决定：使用多少线程及他们如何将他们映射到channel， via construct
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        /**
         *  ServerBootstrap 帮助我们创建 服务器
         */
        ServerBootstrap b = new ServerBootstrap();

        try {
            b.group(bossGroup, workerGroup)
                    //我们使用 NioServerSocketChannel 实例化新接入的连接
                    .channel(NioServerSocketChannel.class)
                    //childhandler 处理程序， ChannelInitializer帮助我们配置新通道， 添加 一些处理程序（DiscardServerHandler）
                    // 配置新通道的 ChannelPipeline，实现网络应用程序
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    //设置参数实现不同类型的通道
                    //option 用于接受传入连接的 NioServerSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //childOption 用于 父通道接受的通道
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //绑定并开始接受连接
            ChannelFuture future = b.bind(port).sync();

            future.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        new DiscardServer(port).run();
    }
}
