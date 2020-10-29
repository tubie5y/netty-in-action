package nia.chapter10;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Listing 10.6 Class IntegerToStringEncoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> { // 扩展了MessageToMessageEncoder
    @Override
    public void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg)); // 将Integer 转换为String，并将其添加到List 中
    }
}

