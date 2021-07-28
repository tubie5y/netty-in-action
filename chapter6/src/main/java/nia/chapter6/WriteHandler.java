package nia.chapter6;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Listing 6.9 Caching a ChannelHandlerContext (缓存到ChannelHandlerContext的引用)
 *      另一种高级的用法是缓存到ChannelHandlerContext的引用以供稍后使用，这可能会发生在任何的ChannelHandler方法之外，甚至来自于不同的线程。代码清单6-9展示了用这种模式来触发事件。
 *
 * ChannelHandlerContext和ChannelHandler之间的关联（绑定）是永远不会改变的，所以缓存对它的引用是安全的；
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class WriteHandler extends ChannelHandlerAdapter {
    private ChannelHandlerContext ctx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx; // 存储到ChannelHandlerContext的引用以供稍后使用
    }

    public void send(String msg) { // 使用之前存储的到ChannelHandlerContext的引用来发送消息
        ctx.writeAndFlush(msg);
    }
}
