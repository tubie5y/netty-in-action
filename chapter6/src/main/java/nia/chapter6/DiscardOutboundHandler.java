package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * - 代码清单 6-4 丢弃并释放出站消息
 *     - 在出站方向这边，如果你处理了write()操作并丢弃了一个消息，那么你也应该负责释放它。代码清单6-4展示了一个丢弃所有的写入数据的实现。
 *
 * - 重要的是，不仅要释放资源，还要通知ChannelPromise。否则可能会出现ChannelFutureListener收不到某个消息已经被处理了的通知的情况。
 * - 总之，如果一个消息被消费或者丢弃了，并且没有传递给ChannelPipeline中的下一个ChannelOutboundHandler，那么用户就有责任调用ReferenceCountUtil.release()。
 *   如果消息到达了实际的传输层，那么当它被写入时或者Channel关闭时，都将被自动释放。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
//扩展了ChannelOutboundHandlerAdapter
public class DiscardOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        //通过使用 ReferenceCountUtil.realse(...)方法释放资源
        ReferenceCountUtil.release(msg);
        //通知 ChannelPromise数据已经被处理了
        promise.setSuccess();
    }
}

