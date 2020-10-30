package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * Listing 6.4 Discarding and releasing outbound data
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
public class DiscardOutboundHandler extends ChannelOutboundHandlerAdapter { // 扩展了ChannelOutboundHandlerAdapter
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ReferenceCountUtil.release(msg); // 通过使用ReferenceCountUtil.realse(...)方法释放资源
        promise.setSuccess(); // 通知ChannelPromise数据已经被处理了
    }
}

