package nia.chapter6;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Listing 6.12 Basic inbound exception handling (基本的入站异常处理)
 *      代码清单6-12展示了一个简单的示例，其关闭了Channel并打印了异常的栈跟踪信息。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class InboundExceptionHandler extends ChannelInboundHandlerAdapter {
    /**
     * - 如果在处理入站事件的过程中有异常被抛出，那么它将从它在ChannelInboundHandler里被触发的那一点开始流经ChannelPipeline。
     *   要想处理这种类型的入站异常，你需要在你的ChannelInboundHandler实现中重写下面的方法。
     *
     * - 因为异常将会继续按照入站方向流动（就像所有的入站事件一样），所以实现了前面所示逻辑的ChannelInboundHandler通常位于ChannelPipeline的最后。
     *   这确保了所有的入站异常都总是会被处理，无论它们可能会发生在ChannelPipeline中的什么位置。
     *
     * - 你应该如何响应异常，可能很大程度上取决于你的应用程序。你可能想要关闭Channel（和连接），也可能会尝试进行恢复。
     *   如果你不实现任何处理入站异常的逻辑（或者没有消费该异常），那么Netty将会记录该异常没有被处理的事实。
     *
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
