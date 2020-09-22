package chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClient {

    private String host;

    private int port;

    public ChatClient(String localhsot, int port) {
        this.host= localhsot;
        this.port = port;
    }

    public void run(){

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInit())
                    .option(ChannelOption.SO_BACKLOG, 128);

            Channel channel = bootstrap.connect(host, port).channel();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                channel.writeAndFlush(in.readLine()+"\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args){

        new ChatClient("localhost", 8080).run();


    }
}
