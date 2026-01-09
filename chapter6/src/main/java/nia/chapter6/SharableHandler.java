package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 代码清单 6-10 可共享的 ChannelHandler
 *      因为一个ChannelHandler可以从属于多个ChannelPipeline，所以它也可以绑定到多个ChannelHandlerContext实例。
 *      对于这种用法指在多个ChannelPipeline中共享同一个ChannelHandler，对应的ChannelHandler必须要使用@Sharable注解标注；否则，试图将它添加到多个ChannelPipeline时将会触发异常。
 *      显而易见，为了安全地被用于多个并发的Channel（即连接），这样的ChannelHandler必须是线程安全的。
 *      代码清单6-10展示了这种模式的一个正确实现。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
//使用注解@Sharable标注
@Sharable
public class SharableHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("channel read message " + msg);
        //记录方法调用，并转发给下一个 ChannelHandler
        ctx.fireChannelRead(msg);
    }
}

