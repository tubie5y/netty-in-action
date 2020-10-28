package nia.chapter6;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Listing 6.9 Caching a ChannelHandlerContext
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
