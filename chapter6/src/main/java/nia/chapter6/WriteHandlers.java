package nia.chapter6;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DummyChannelPipeline;
import io.netty.util.CharsetUtil;

import static io.netty.channel.DummyChannelHandlerContext.DUMMY_INSTANCE;

/**
 * Created by kerr.
 *
 * Listing 6.6 Accessing the Channel from a ChannelHandlerContext (从ChannelHandlerContext访问Channel)
 *
 * Listing 6.7 Accessing the ChannelPipeline from a ChannelHandlerContext (通过ChannelHandlerContext访问ChannelPipeline)
 *
 * Listing 6.8 Calling ChannelHandlerContext write()
 */
public class WriteHandlers {
    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = DUMMY_INSTANCE;
    private static final ChannelPipeline CHANNEL_PIPELINE_FROM_SOMEWHERE = DummyChannelPipeline.DUMMY_INSTANCE;

    /**
     * Listing 6.6 Accessing the Channel from a ChannelHandlerContext (从ChannelHandlerContext访问Channel)
     *      在代码清单6-6中，将通过ChannelHandlerContext获取到Channel的引用。调用Channel上的write()方法将会导致写入事件从尾端到头部地流经ChannelPipeline。
     *
     * - ChannelHandlerContext有很多的方法，其中一些方法也存在于Channel和ChannelPipeline本身上，但是有一点重要的不同。
     *   如果调用Channel或者ChannelPipeline上的这些方法，它们将沿着整个ChannelPipeline进行传播。
     *   而调用位于ChannelHandlerContext上的相同方法，则将从当前所关联的ChannelHandler开始，并且只会传播给位于该ChannelPipeline中的下一个能够处理该事件的ChannelHandler。
     *   相对于其他类的同名方法，ChannelHandlerContext的方法将产生更短的事件流，应该尽可能地利用这个特性来获得最大的性能。
     * */
    public static void writeViaChannel() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere
        Channel channel = ctx.channel(); // 获取到与ChannelHandlerContext相关联的Channel 的引用
        channel.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8)); // 通过Channel 写入缓冲区
    }

    /**
     * Listing 6.7 Accessing the ChannelPipeline from a ChannelHandlerContext (通过ChannelHandlerContext访问ChannelPipeline)
     *      代码清单6-7展示了一个类似的例子，但是这一次是写入ChannelPipeline。我们再次看到，（到ChannelPipline的）引用是通过ChannelHandlerContext获取的。
     *
     * */
    public static void writeViaChannelPipeline() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere
        ChannelPipeline pipeline = ctx.pipeline(); //  ←--  获取到与ChannelHandlerContext相关联的ChannelPipeline 的引用
        pipeline.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8)); //  ←--  通过ChannelPipeline写入缓冲区
    }

    /**
     * Listing 6.8 Calling ChannelHandlerContext write() (调用ChannelHandlerContext的write()方法)
     * */
    public static void writeViaChannelHandlerContext() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere; 获取到ChannelHandlerContext的引用
        ctx.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8)); // write()方法将把缓冲区数据发送到下一个ChannelHandler
    }

}
