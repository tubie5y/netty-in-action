package nia.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

/**
 * Listing 8.9 Graceful shutdown (优雅关闭)
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 * @author <a href="mailto:mawolfthal@gmail.com">Marvin Wolfthal</a>
 */
public class GracefulShutdown {
    public static void main(String args[]) {
        GracefulShutdown client = new GracefulShutdown();
        client.bootstrap();
    }

    /**
     * Listing 8.9 Graceful shutdown (优雅关闭)
     *      代码清单8-9符合优雅关闭的定义。
     *
     * - 引导使你的应用程序启动并且运行起来，但是迟早你都需要优雅地将它关闭。当然，你也可以让JVM在退出时处理好一切，但是这不符合优雅的定义，优雅是指干净地释放资源。
     *   关闭Netty应用程序并没有太多的魔法，但是还是有些事情需要记在心上。
     * - 最重要的是，你需要关闭EventLoopGroup，它将处理任何挂起的事件和任务，并且随后释放所有活动的线程。这就是调用EventLoopGroup.shutdownGracefully()方法的作用。
     *   这个方法调用将会返回一个Future，这个Future将在关闭完成时接收到通知。需要注意的是，shutdownGracefully()方法也是一个异步的操作，所以你需要阻塞等待直到它完成，
     *   或者向所返回的Future注册一个监听器以在关闭完成时获得通知。
     * - 或者，你也可以在调用EventLoopGroup.shutdownGracefully()方法之前，显式地在所有活动的Channel上调用Channel.close()方法。但是在任何情况下，都请记得关闭EventLoopGroup本身。
     */
    public void bootstrap() {
        EventLoopGroup group = new NioEventLoopGroup(); // 创建处理I/O 的EventLoopGroup
        Bootstrap bootstrap = new Bootstrap(); // 创建一个Bootstrap类的实例并配置它
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                             @Override
                             protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                 System.out.println("Received data");
                             }
                         }
                );
        // syncUninterruptibly()方法：阻塞等待这个future，直到它完成为止，如果这个future失败，则重新抛出失败的原因。
        bootstrap.connect(new InetSocketAddress("www.manning.com", 80)).syncUninterruptibly();
        Future<?> future = group.shutdownGracefully(); // shutdownGracefully()方法将释放所有的资源，并且关闭所有的当前正在使用中的Channel
        // block until the group has shutdown
        future.syncUninterruptibly();
    }
}
