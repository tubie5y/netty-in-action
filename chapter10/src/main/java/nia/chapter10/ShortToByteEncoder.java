package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameEncoder;

/**
 * Listing 10.5 Class ShortToByteEncoder (ShortToByteEncoder类)
 *
 * - Netty提供了一些专门化的MessageToByteEncoder，你可以基于它们实现自己的编码器。{@link WebSocket08FrameEncoder} 类提供了一个很好的实例。
 *   你可以在io.netty.handler.codec.http.websocketx包中找到它。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ShortToByteEncoder extends MessageToByteEncoder<Short> { // 扩展了MessageToByteEncoder
    @Override
    public void encode(ChannelHandlerContext ctx, Short msg, ByteBuf out) throws Exception {
        out.writeShort(msg); // 将Short 写入ByteBuf 中
    }
}
