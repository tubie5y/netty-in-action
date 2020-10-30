package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * Listing 10.4 TooLongFrameException
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */

public class SafeByteToMessageDecoder extends ByteToMessageDecoder { // 扩展ByteToMessageDecoder以将字节解码为消息
    private static final int MAX_FRAME_SIZE = 1024;

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readable = in.readableBytes();
        if (readable > MAX_FRAME_SIZE) { // 检查缓冲区中是否有超过MAX_FRAME_SIZE个字节
            in.skipBytes(readable); // 跳过所有的可读字节，抛出TooLongFrameException 并通知ChannelHandler
            throw new TooLongFrameException("Frame too big!");
        }
        // do something
        // ...
    }
}
