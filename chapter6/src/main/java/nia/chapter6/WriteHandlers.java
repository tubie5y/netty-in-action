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
 * 代码清单 6-6 从 ChannelHandlerContext 访问 Channel
 *
 * 代码清单 6-7 通过 ChannelHandlerContext 访问 ChannelPipeline
 *
 * Listing 6.8 Calling ChannelHandlerContext write()
 */
public class WriteHandlers {
    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = DUMMY_INSTANCE;
    private static final ChannelPipeline CHANNEL_PIPELINE_FROM_SOMEWHERE = DummyChannelPipeline.DUMMY_INSTANCE;

    /**
     * 代码清单 6-6 从 ChannelHandlerContext 访问 Channel
     *      在代码清单6-6中，将通过ChannelHandlerContext获取到Channel的引用。调用Channel上的write()方法将会导致写入事件从尾端到头部地流经ChannelPipeline。
     *
     * - ChannelHandlerContext有很多的方法，其中一些方法也存在于Channel和ChannelPipeline本身上，但是有一点重要的不同。
     *   如果调用Channel或者ChannelPipeline上的这些方法，它们将沿着整个ChannelPipeline进行传播。
     *   而调用位于ChannelHandlerContext上的相同方法，则将从当前所关联的ChannelHandler开始，并且只会传播给位于该ChannelPipeline中的下一个能够处理该事件的ChannelHandler。
     *   相对于其他类的同名方法，ChannelHandlerContext的方法将产生更短的事件流，应该尽可能地利用这个特性来获得最大的性能。
     * */
    public static void writeViaChannel() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere
        //获取到与 ChannelHandlerContext相关联的 Channel 的引用
        Channel channel = ctx.channel();
        //通过 Channel 写入缓冲区
        channel.write(Unpooled.copiedBuffer("Netty in Action",
                CharsetUtil.UTF_8));

    }

    /**
     * 代码清单 6-7 通过 ChannelHandlerContext 访问 ChannelPipeline
     *      代码清单6-7展示了一个类似的例子，但是这一次是写入ChannelPipeline。我们再次看到，（到ChannelPipline的）引用是通过ChannelHandlerContext获取的。
     *
     * */
    public static void writeViaChannelPipeline() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere
        //获取到与 ChannelHandlerContext相关联的 ChannelPipeline 的引用
        ChannelPipeline pipeline = ctx.pipeline(); //get reference form somewhere
        //通过 ChannelPipeline写入缓冲区
        pipeline.write(Unpooled.copiedBuffer("Netty in Action",
                CharsetUtil.UTF_8));

    }

    /**
     * 代码清单 6-8 调用 ChannelHandlerContext 的 write()方法
     * */
    public static void writeViaChannelHandlerContext() {
        //获取到 ChannelHandlerContext 的引用
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere;
        //write()方法将把缓冲区数据发送到下一个 ChannelHandler
        ctx.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8));
    }

}
