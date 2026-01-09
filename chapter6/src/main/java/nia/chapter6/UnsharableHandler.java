package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 代码清单 6-11 @Sharable 的错误用法
 *      前面的ChannelHandler实现(6.10)符合所有的将其加入到多个ChannelPipeline的需求，即它使用了注解@Sharable标注，并且也不持有任何的状态。相反，代码清单6-11中的实现将会导致问题。
 *
 * - 这段代码的问题在于它拥有状态，即用于跟踪方法调用次数的实例变量count。将这个类的一个实例添加到ChannelPipeline将极有可能在它被多个并发的Channel访问时导致问题。
 *  （当然，这个简单的问题可以通过使channelRead()方法变为同步方法来修正。）
 * - 总之，只应该在确定了你的ChannelHandler是线程安全的时才使用@Sharable注解。
 * - 为何要共享同一个ChannelHandler: 在多个ChannelPipeline中安装同一个ChannelHandler的一个常见的原因是用于收集跨越多个Channel的统计信息。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
//使用注解@Sharable标注
@Sharable
public class UnsharableHandler extends ChannelInboundHandlerAdapter {
    private int count;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //将 count 字段的值加 1
        count++;
        //记录方法调用，并转发给下一个ChannelHandler
        System.out.println("inboundBufferUpdated(...) called the " + count + " time");
        ctx.fireChannelRead(msg);
    }
}

