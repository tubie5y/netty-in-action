package nia.chapter1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by kerr.
 *
 * 代码清单 1-3 异步地建立连接
 *
 * 代码清单 1-4 回调实战
 */
public class ConnectExample {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /**
     * 代码清单 1-3 异步地建立连接
     *      代码清单1-3展示了一个ChannelFuture作为一个I/O操作的一部分返回的例子。这里，connect()方法将会直接返回，而不会阻塞，该调用将会在后台完成。
     *      这究竟什么时候会发生则取决于若干的因素，但这个关注点已经从代码中抽象出来了。因为线程不用阻塞以等待对应的操作完成，所以它可以同时做其他的工作，从而更加有效地利用资源。
     *
     * 代码清单 1-4 回调实战
     *      代码清单1-4显示了如何利用ChannelFutureListener。首先，要连接到远程节点上。然后，要注册一个新的ChannelFutureListener到对connect()方法的调用所返回的ChannelFuture上。
     *      当该监听器被通知连接已经建立的时候，要检查对应的状态❶。如果该操作是成功的，那么将数据写到该Channel。否则，要从ChannelFuture中检索对应的Throwable。
     * */
    public static void connect() {
        Channel channel = CHANNEL_FROM_SOMEWHERE; //reference form somewhere
        // Does not block
        //  异步地连接到远程节点
        ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1", 25));
        //  注册一个 ChannelFutureListener，以便在操作完成时获得通知
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                // ① 检查操作的状态
                if (future.isSuccess()) {
                    //如果操作是成功的，则创建一个 ByteBuf 以持有数据
                    ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
                    //  将数据异步地发送到远程节点。返回一个 ChannelFuture
                    ChannelFuture wf = future.channel().writeAndFlush(buffer);
                    // ...
                } else {
                    // 需要注意的是,对错误的处理完全取决于你、目标,当然也包括目前任何对于特定类型的错误加以的限制。例如,如果连接失败,你可以尝试重新连接或者建立一个到另一个远程节点的连接。
                    Throwable cause = future.cause(); // 　如果发生错误，则访问描述原因的 Throwable
                    cause.printStackTrace();
                }
            }
        });

    }
}