package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 代码清单 6-1 释放消息资源
 *
 * - 当某个ChannelInboundHandler的实现重写channelRead()方法时，它将负责显式地释放与池化的ByteBuf实例相关的内存。
 *   Netty为此提供了一个实用方法 ReferenceCountUtil.release()，如代码清单6-1所示。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
//扩展了 ChannelInboundHandlerAdapter
public class DiscardHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //丢弃已接收的消息
        ReferenceCountUtil.release(msg);
    }

}

