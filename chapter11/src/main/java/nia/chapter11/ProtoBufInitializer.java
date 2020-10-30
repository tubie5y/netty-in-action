package nia.chapter11;

import com.google.protobuf.MessageLite;
import io.netty.channel.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

/**
 * Listing 11.14 Using protobuf
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ProtoBufInitializer extends ChannelInitializer<Channel> {
    private final MessageLite lite;

    public ProtoBufInitializer(MessageLite lite) {
        this.lite = lite;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtobufVarint32FrameDecoder()); // 添加ProtobufVarint32FrameDecoder以分隔帧
        pipeline.addLast(new ProtobufEncoder()); // 添加ProtobufEncoder以处理消息的编码
        pipeline.addLast(new ProtobufDecoder(lite)); // 添加ProtobufDecoder以解码消息
        pipeline.addLast(new ObjectHandler()); // 添加ObjectHandler 以处理解码消息
    }

    public static final class ObjectHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            // Do something with the object
        }
    }
}
