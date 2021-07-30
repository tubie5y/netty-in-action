package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Listing 10.8 Class ByteToCharDecoder
 *
 * 这里的decode()方法一次将从ByteBuf中提取2字节，并将它们作为char写入到List中，其将会被自动装箱为 Character 对象。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ByteToCharDecoder extends ByteToMessageDecoder { //  ←--  扩展了ByteToMessageDecoder
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 2) { //  ←--  将一个或者多个Character对象添加到传出的List 中
            out.add(in.readChar());
        }
    }
}

