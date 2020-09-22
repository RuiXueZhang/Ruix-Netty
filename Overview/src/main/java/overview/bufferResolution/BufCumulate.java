package overview.bufferResolution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
/**
 * 关于 socket buffer  的警告
 */

/**
 * 粘包解决方法1：
 * 创建一个内部积累的缓冲区，等到4个字节被接收到内部缓冲区
 */
public class BufCumulate extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext context){
        buf = context.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context){
        buf.release();
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg){

        ByteBuf m = (ByteBuf)msg;
        buf.writeBytes(m);
        m.release();
        if(buf.readableBytes() >= 4){
            long curr = (buf.readUnsignedInt() - 2208988800L)* 1000L;
            System.out.println(new Date(curr));
            context.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause){
        cause.printStackTrace();
        context.close();
    }
}
