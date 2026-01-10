package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 代码清单 11-10 使用 LengthFieldBasedFrameDecoder 解码器基于长度的协议
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * ================== 用于基于长度的协议的解码器
 * 1. FixedLengthFrameDecoder
 *   - 提取在调用构造函数时指定的定长帧
 *
 * 2. LengthFieldBasedFrameDecoder
 *   - 根据编码进帧头部中的长度值提取帧; 该字段的偏移量以及长度在构造函数中指定
 *   - 你将经常会遇到被编码到消息头部的帧大小不是固定值的协议。为了处理这种变长帧，你可以使用 LengthFieldBasedFrameDecoder，
 *     它将从头部字段确定帧长，然后从数据流中提取指定的字节数。
 */
public class LengthBasedInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //使用 LengthFieldBasedFrameDecoder 解码将帧长度编码到帧起始的前 8 个字节中的消息
        pipeline.addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 8));
        //添加 FrameHandler 以处理每个帧
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // Do something with the frame
            //处理帧的数据
        }
    }
}
