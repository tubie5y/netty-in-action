package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 代码清单 9 CharToByteEncoder 类
 *      代码清单10-9包含了CharToByteEncoder，它能将Character转换回字节。这个类扩展了MessageToByteEncoder，因为它需要将char消息编码到ByteBuf中。这是通过直接写入ByteBuf做到的。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
//扩展了MessageToByteEncoder
public class CharToByteEncoder extends MessageToByteEncoder<Character> {
    @Override
    public void encode(ChannelHandlerContext ctx, Character msg, ByteBuf out) throws Exception {
        //将 Character 解码为 char，并将其写入到出站 ByteBuf 中
        out.writeChar(msg);
    }
}

