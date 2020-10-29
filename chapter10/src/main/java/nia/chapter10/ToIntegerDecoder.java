package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Listing 10.1 Class ToIntegerDecoder extends ByteToMessageDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ToIntegerDecoder extends ByteToMessageDecoder { // 扩展ByteToMessageDecoder 类，以将字节解码为特定的格式
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) { // 检查是否至少有4字节可读（一个int的字节长度）
            out.add(in.readInt()); // 从入站ByteBuf 中读取一个int，并将其添加到解码消息的List 中
        }
    }
}

