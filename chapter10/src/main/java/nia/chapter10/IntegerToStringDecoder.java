package nia.chapter10;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Listing 10.3 Class IntegerToStringDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> { // 扩展了MessageToMessageDecoder<Integer>
    @Override
    public void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg)); // 将Integer 消息转换为它的String 表示，并将其添加到输出的List 中
    }
}

