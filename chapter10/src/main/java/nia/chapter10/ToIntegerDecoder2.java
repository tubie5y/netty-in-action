package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 代码清单 10-2 ToIntegerDecoder2 类扩展了 ReplayingDecoder
 *
 * - ReplayingDecoder扩展了ByteToMessageDecoder类（如代码清单10-1所示），使得我们不必调用readableBytes()方法。
 *   它通过使用一个自定义的ByteBuf实现，ReplayingDecoderByteBuf，包装传入的ByteBuf实现了这一点，其将在内部执行该调用。
 *
 * - 类型参数S指定了用于状态管理的类型，其中Void代表不需要状态管理。代码清单10-2展示了基于ReplayingDecoder重新实现的ToIntegerDecoder。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
//扩展 ReplayingDecoder<Void> 以将字节解码为消息
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, //传入的 ByteBuf 是 ReplayingDecoderByteBuf
        List<Object> out) throws Exception {
        //从入站 ByteBuf 中读取 一个 int，并将其添加到解码消息的 List 中
        out.add(in.readInt());
    }
}

