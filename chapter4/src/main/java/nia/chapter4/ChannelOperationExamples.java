package nia.chapter4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Listing 4.5 Writing to a Channel
 *
 * Listing 4.6 Using a Channel from many threads
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ChannelOperationExamples {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    /**
     * Listing 4.5 Writing to a Channel
     */
    public static void writingToChannel() {
        Channel channel = CHANNEL_FROM_SOMEWHERE; // Get the channel reference from somewhere
        ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8); // 创建持有要写数据的ByteBuf
        ChannelFuture cf = channel.writeAndFlush(buf); // 写数据并冲刷它
        cf.addListener(new ChannelFutureListener() { // 添加ChannelFutureListener 以便在写操作完成后接收通知
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) { // 写操作完成，并且没有错误发生　
                    System.out.println("Write successful");
                } else { // 记录错误
                    System.err.println("Write error");
                    future.cause().printStackTrace();
                }
            }
        });
    }

    /**
     * Listing 4.6 Using a Channel from many threads
     *
     * Netty的Channel实现是线程安全的，因此你可以存储一个到Channel的引用，并且每当你需要向远程节点写数据时，都可以使用它，即使当时许多线程都在使用它。
     * 代码清单4-6展示了一个多线程写数据的简单例子。需要注意的是，消息将会被保证按顺序发送。
     */
    public static void writingToChannelFromManyThreads() {
        final Channel channel = CHANNEL_FROM_SOMEWHERE; // Get the channel reference from somewhere
        final ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8); // 创建持有要写数据的ByteBuf
        Runnable writer = new Runnable() { // 创建将数据写到Channel 的Runnable
            @Override
            public void run() {
                channel.write(buf.duplicate());
            }
        };
        Executor executor = Executors.newCachedThreadPool(); // 获取到线程池Executor 的引用

        // write in one thread
        executor.execute(writer); // 递交写任务给线程池以便在某个线程中执行

        // write in another thread
        executor.execute(writer); // 递交另一个写任务以便在另一个线程中执行
        //...
    }
}
