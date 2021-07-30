package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Listing 10.1 Class ToIntegerDecoder extends ByteToMessageDecoder (ToIntegerDecoder类扩展了ByteToMessageDecoder)
 *      - 虽然ByteToMessageDecoder使得可以很简单地实现这种模式，但是你可能会发现，在调用readInt()方法前不得不验证所输入的ByteBuf是否具有足够的数据有点繁琐。
 *        在下一节中，我们将讨论ReplayingDecoder，它是一个特殊的解码器，以少量的开销消除了这个步骤。
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

