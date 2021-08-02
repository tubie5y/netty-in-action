package nia.test.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import nia.chapter9.FixedLengthFrameDecoder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Listing 9.2 Testing the FixedLengthFrameDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * - 该testFramesDecoded()方法验证了：一个包含9个可读字节的ByteBuf被解码为3个ByteBuf，每个都包含了3字节。
 *   需要注意的是，仅通过一次对writeInbound()方法的调用，ByteBuf是如何被填充了9个可读字节的。在此之后，通过执行finish()方法，
 *   将EmbeddedChannel标记为了已完成状态。最后，通过调用readInbound()方法，从Embedded-Channel中正好读取了3个帧和一个null。
 *
 * - testFramesDecoded2()方法也是类似的，只有一处不同：入站ByteBuf是通过两个步骤写入的。当writeInbound(input.readBytes(2))被调用时，返回了false。为什么呢？
 *   正如同表9-1中所描述的，如果对readInbound()的后续调用将会返回数据，那么writeInbound()方法将会返回true。
 *   但是只有当有3个或者更多的字节可供读取时，FixedLength-FrameDecoder才会产生输出。该测试剩下的部分和testFramesDecoded()是相同的。
 */
public class FixedLengthFrameDecoderTest {
    @Test // 使用了注解@Test 标注，因此JUnit 将会执行该方法
    public void testFramesDecoded() { // 第一个测试方法：testFramesDecoded()
        ByteBuf buf = Unpooled.buffer(); // 创建一个ByteBuf，并存储9 字节
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        // 创建一个EmbeddedChannel，并添加一个FixedLengthFrameDecoder，其将以3 字节的帧长度被测试
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        // write bytes
        assertTrue(channel.writeInbound(input.retain())); // 将数据写入EmbeddedChannel
        assertTrue(channel.finish()); // 标记Channel为已完成状态　

        // read messages
        ByteBuf read = (ByteBuf) channel.readInbound(); // 读取所生成的消息，并且验证是否有3 帧（切片），其中每帧（切片）都为3 字节
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void testFramesDecoded2() { // 第二个测试方法：testFramesDecoded2()　
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        assertFalse(channel.writeInbound(input.readBytes(2))); // 返回false，因为没有一个完整的可供读取的帧
        assertTrue(channel.writeInbound(input.readBytes(7)));

        assertTrue(channel.finish());
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }
}
