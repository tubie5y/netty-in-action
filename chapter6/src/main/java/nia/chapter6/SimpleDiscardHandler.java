package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 代码清单 6-2 使用 SimpleChannelInboundHandler
 *
 * - Netty将使用WARN级别的日志消息记录未释放的资源，使得可以非常简单地在代码中发现违规的实例。但是以这种方式管理资源可能很繁琐。
 *   一个更加简单的方式是使用SimpleChannelInboundHandler。代码清单6-2是代码清单6-1的一个变体，说明了这一点。
 *
 * - 由于SimpleChannelInboundHandler会自动释放资源，所以你不应该存储指向任何消息的引用供将来使用，因为这些引用都将会失效。
 *   6.1.6节为引用处理提供了更加详细的讨论。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
//扩展了SimpleChannelInboundHandler
public class SimpleDiscardHandler extends SimpleChannelInboundHandler<Object> { // 扩展了SimpleChannelInboundHandler
    /**
     * - 消费入站消息的简单方式:
     *     - 由于消费入站数据是一项常规任务，所以Netty提供了一个特殊的被称为 SimpleChannelInboundHandler 的ChannelInboundHandler实现。
     *       这个实现会在消息被channelRead0()方法消费之后自动释放消息。
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        //不需要任何显式的资源释放
        // No need to do anything special
    }
}
