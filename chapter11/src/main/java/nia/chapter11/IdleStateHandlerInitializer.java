package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 代码清单 11-7 发送心跳
 *     - 让我们仔细看看在实践中使用得最多的IdleStateHandler吧。代码清单11-7展示了当使用通常的发送心跳消息到远程节点的方法时，
 *       如果在60秒之内没有接收或者发送任何的数据，我们将如何得到通知；如果没有响应，则连接会被关闭。
 *
 *     - 这个示例演示了如何使用 IdleStateHandler 来测试远程节点是否仍然还活着，并且在它失活时通过关闭连接来释放资源。
 *
 *     - 如果连接超过60秒没有接收或者发送任何的数据，那么 IdleStateHandler❶将会使用一个 IdleStateEvent 事件来调用 fireUserEventTriggered()方法。
 *        HeartbeatHandler实现了userEventTriggered()方法，如果这个方法检测到IdleStateEvent事件，它将会发送心跳消息，并且添加一个将在发送操作失败时关闭该连接的 ChannelFutureListener❷。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * ============= 用于空闲连接以及超时的ChannelHandler
 * 1. IdleStateHandler
 *   - 当连接空闲时间太长时, 将会触发一个 IdleStateEvent事件。然后, 你可以通过在你的 ChannelInboundHandler中重写 userEventTriggered() 方法来处理该IdleStateEvent事件
 *
 * 2. ReadTimeoutHandler
 *   - 如果在指定的时间间隔内没有收到任何的入站数据, 则抛出一个 ReadTimeoutException并关闭对应的Channel。可以通过重写你的ChannelHandler中的exceptionCaught()方法来检测该ReadTimeoutException
 *
 * 3. WriteTimeoutHandler
 *   - 如果在指定的时间间隔内没有任何出站数据写入, 则抛出一个 WriteTimeoutException并关闭对应的Channel。可以通过重写你的ChannelHandler的exceptionCaught()方法检测该 WriteTimeoutException
 *
 *
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //(1) IdleStateHandler 将在被触发时发送一个IdleStateEvent 事件
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        //将一个 HeartbeatHandler 添加到ChannelPipeline中
        pipeline.addLast(new HeartbeatHandler());
    }

    //实现 userEventTriggered() 方法以发送心跳消息
    public static final class HeartbeatHandler extends ChannelInboundHandlerAdapter {
        //发送到远程节点的心跳消息
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1));
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            //(2) 发送心跳消息，并在发送失败时关闭该连接
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                //不是 IdleStateEvent 事件，所以将它传递给下一个 ChannelInboundHandler
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
