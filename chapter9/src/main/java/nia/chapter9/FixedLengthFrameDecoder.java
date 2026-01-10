package nia.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 代码清单9-1 FixedLengthFrameDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * ============== EmbeddedChannel的相关方法
 * 1. writeInbound(Object... msgs)
 *   - 将入站消息写到EmbeddedChannel中。如果可以通过readinbound()方法从EmbeddedChannel中读取数据, 则返回true
 * 2. readInbound()
 *   - 从 EmbeddedChannel 中读取一个入站消息。任何返回的东西都穿越了整个ChannelPipeline。如果没有任何可供读取的,则返回null
 *
 * 3. writeOutbound(Object... msgs)
 *   - 将出站消息写到EmbeddedChannel中。如果现在可以通过readOutbound()方法从EmbeddedChannel中读取到什么东西,则返回true
 * 4. readOutbound()
 *   - 从EmbeddedChannel中读取一个出站消息。任何返回的东西都穿越了整个ChannelPipeline。如果没有任何可供读取的,则返回null
 *
 * 5. finish ()
 *   - 将EmbeddedChannel标记为完成, 并且如果有可被读取的入站数据或者出站数据, 则返回true。这个方法还将会调用EmbeddedChannel上的close()方法
 *
 */
//扩展 ByteToMessageDecoder 以处理入站字节，并将它们解码为消息
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private final int frameLength;

    //指定要生成的帧的长度
    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //检查是否有足够的字节可以被读取，以生成下一个帧
        while (in.readableBytes() >= frameLength) {
            //从 ByteBuf 中读取一个新帧
            ByteBuf buf = in.readBytes(frameLength);
            //将该帧添加到已被解码的消息列表中
            out.add(buf);
        }
    }
}
