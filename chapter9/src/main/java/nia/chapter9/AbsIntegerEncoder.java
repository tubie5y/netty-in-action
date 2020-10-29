package nia.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Listing 9.3 AbsIntegerEncoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> { // 扩展MessageToMessageEncoder 以将一个消息编码为另外一种格式
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) { // 检查是否有足够的字节用来编码
            int value = Math.abs(in.readInt()); // 从输入的ByteBuf中读取下一个整数，并且计算其绝对值
            out.add(value); // 将该整数写入到编码消息的List 中
        }
    }
}
