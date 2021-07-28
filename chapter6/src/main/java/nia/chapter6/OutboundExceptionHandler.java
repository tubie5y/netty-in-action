package nia.chapter6;

import io.netty.channel.*;

/**
 * Listing 6.14 Adding a ChannelFutureListener to a ChannelPromise (添加ChannelFutureListener到ChannelPromise)
 *      第二种方式是将ChannelFutureListener添加到即将作为参数传递给ChannelOutboundHandler的方法的ChannelPromise。代码清单6-14中所展示的代码和代码清单6-13中所展示的具有相同的效果。
 *
 * ChannelPromise的可写方法
 *      - 通过调用ChannelPromise上的setSuccess()和setFailure()方法，可以使一个操作的状态在ChannelHandler的方法返回给其调用者时便即刻被感知到。
 *      - 为何选择一种方式而不是另一种呢？对于细致的异常处理，你可能会发现，在调用出站操作时添加ChannelFutureListener更合适，如代码清单6-13所示。
 *        而对于一般的异常处理，你可能会发现，代码清单6-14所示的自定义的ChannelOutboundHandler实现的方式更加的简单。
 *      - 如果你的ChannelOutboundHandler本身抛出了异常会发生什么呢？在这种情况下，Netty本身会通知任何已经注册到对应ChannelPromise的监听器。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) {
                if (!f.isSuccess()) {
                    f.cause().printStackTrace();
                    f.channel().close();
                }
            }
        });
    }
}
