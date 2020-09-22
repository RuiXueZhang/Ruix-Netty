package overview.bufferResolution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 第一种方法 字节累加 然后读取的方式 处理的并不干净，一旦有非常复杂的协议，ChannelInboundHandler会变得难以维护
 *
 * 我们的channelPipeline 中可以添加不止一种 channelHandler，例如：
 *          serverChannel.pipeline().addLast(new TimeClientHandler()   //逻辑处理
 *                                           new ByteToMessage()    //碎片处理);
 *
 *    这样可以降低我们应用的复杂度
 *    netty 为我们提供了 一个可扩展的类, ByteToMessageDecoder 是实现了 ChannelInboundHandler，更容易处理碎片 class
 */
public class ByteToMessage extends ByteToMessageDecoder {

    //当收到新的数据时，ByteToMessageDecoder 使用 内部维护的数据缓冲区 调用decode方法
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //当累加的缓冲区（bytebuf) 没有足够的数据， add noting to list,当收到数据时再次调用直到 达到足够的数据
        if(byteBuf.readableBytes() <4){
            return;
        }

        list.add(byteBuf.readBytes(4));

    }
}
