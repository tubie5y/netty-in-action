package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Listing 10.2 Class ToIntegerDecoder2 extends ReplayingDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> { // 扩展ReplayingDecoder<Void>以将字节解码为消息

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception { // 传入的ByteBuf 是ReplayingDecoderByteBuf
        out.add(in.readInt()); // 从入站ByteBuf 中读取一个int，并将其添加到解码消息的List 中
    }
}

