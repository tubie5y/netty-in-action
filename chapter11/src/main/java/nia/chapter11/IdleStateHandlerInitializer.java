package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * Listing 11.7 Sending heartbeats
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * 代码清单11-7展示了当使用通常的发送心跳消息到远程节点的方法时，如果在60秒之内没有接收或者发送任何的数据，我们将如何得到通知；如果没有响应，则连接会被关闭。
 *
 * 这个示例演示了如何使用IdleStateHandler来测试远程节点是否仍然还活着，并且在它失活时通过关闭连接来释放资源。
 *
 * 如果连接超过60秒没有接收或者发送任何的数据，那么IdleStateHandler❶将会使用一个IdleStateEvent事件来调用fireUserEventTriggered()方法。HeartbeatHandler实
 * 现了userEventTriggered()方法，如果这个方法检测到IdleStateEvent事件，它将会发送心跳消息，并且添加一个将在发送操作失败时关闭该连接的ChannelFutureListener❷。
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS)); // IdleStateHandler 将在被触发时发送一个IdleStateEvent 事件
        pipeline.addLast(new HeartbeatHandler()); // 将一个HeartbeatHandler添加到ChannelPipeline中
    }

    public static final class HeartbeatHandler extends ChannelInboundHandlerAdapter { // 实现userEventTriggered()方法以发送心跳消息
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1));

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE); // 发送心跳消息，并在发送失败时关闭该连接
            } else {
                super.userEventTriggered(ctx, evt); // 不是IdleStateEvent事件，所以将它传递给下一个ChannelInboundHandler
            }
        }
    }
}
