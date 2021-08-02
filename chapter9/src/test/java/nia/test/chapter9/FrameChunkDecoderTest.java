package nia.test.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import nia.chapter9.FrameChunkDecoder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Listing 9.6 Testing FrameChunkDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * - 乍一看，这看起来非常类似于代码清单9-2中的测试，但是它有一个有趣的转折点，即对TooLongFrameException的处理。这里使用的try/catch块是EmbeddedChannel的一个特殊功能。
 *   如果其中一个write*方法产生了一个受检查的Exception，那么它将会被包装在一个RuntimeException中并抛出。这使得可以容易地测试出一个Exception是否在处理数据的过程中已经被处理了。
 *
 * - 这里介绍的测试方法可以用于任何能抛出Exception的ChannelHandler实现。
 */

public class FrameChunkDecoderTest {
    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer(); // 创建一个ByteBuf，并向它写入9 字节
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3)); // 创建一个EmbeddedChannel，并向其安装一个帧大小为3 字节的FixedLengthFrameDecoder

        assertTrue(channel.writeInbound(input.readBytes(2))); // 向它写入2 字节，并断言它们将会产生一个新帧
        try {
            channel.writeInbound(input.readBytes(4)); // 写入一个4 字节大小的帧，并捕获预期的TooLongFrameException
            Assert.fail(); // 如果上面没有抛出异常，那么就会到达这个断言，并且测试失败
        } catch (TooLongFrameException e) {
            // expected exception
        }
        assertTrue(channel.writeInbound(input.readBytes(3))); // 写入剩余的2 字节，并断言将会产生一个有效帧
        assertTrue(channel.finish()); // 将该Channel 标记为已完成状态

        // Read frames : 读取产 生的消息，并且验证值
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(2), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.skipBytes(4).readSlice(3), read);
        read.release();
        buf.release();
    }
}
