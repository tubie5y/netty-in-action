package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Listing 10.2 Class ToIntegerDecoder2 extends ReplayingDecoder (ToIntegerDecoder2类扩展了ReplayingDecoder)
 *
 * - ReplayingDecoder扩展了ByteToMessageDecoder类（如代码清单10-1所示），使得我们不必调用readableBytes()方法。
 *   它通过使用一个自定义的ByteBuf实现，ReplayingDecoderByteBuf，包装传入的ByteBuf实现了这一点，其将在内部执行该调用。
 *
 * - 类型参数S指定了用于状态管理的类型，其中Void代表不需要状态管理。代码清单10-2展示了基于ReplayingDecoder重新实现的ToIntegerDecoder。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> { // 扩展ReplayingDecoder<Void>以将字节解码为消息

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception { // 传入的ByteBuf 是ReplayingDecoderByteBuf
        out.add(in.readInt()); // 从入站ByteBuf 中读取一个int，并将其添加到解码消息的List 中
    }
}

