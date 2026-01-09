package nia.chapter6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * Created by kerr.
 *
 * 代码清单 6-13 添加 ChannelFutureListener 到 ChannelFuture
 *      - 添加ChannelFutureListener只需要调用ChannelFuture实例上的addListener(ChannelFutureListener)方法，并且有两种不同的方式可以做到这一点。
 *        其中最常用的方式是，调用出站操作（如write()方法）所返回的ChannelFuture上的addListener()方法。
 *      - 代码清单6-13使用了这种方式来添加ChannelFutureListener，它将打印栈跟踪信息并且随后关闭Channel。
 *
 */
public class ChannelFutures {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final ByteBuf SOME_MSG_FROM_SOMEWHERE = Unpooled.buffer(1024);

    /**
     * 代码清单 6-13 添加 ChannelFutureListener 到 ChannelFuture
     * */
    public static void addingChannelFutureListener(){
        Channel channel = CHANNEL_FROM_SOMEWHERE; // get reference to pipeline;
        ByteBuf someMessage = SOME_MSG_FROM_SOMEWHERE; // get reference to pipeline;
        //...
        ChannelFuture future = channel.write(someMessage);
        future.addListener(new ChannelFutureListener() {
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
