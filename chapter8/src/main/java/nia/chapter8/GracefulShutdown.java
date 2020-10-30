package nia.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

/**
 * Listing 8.9 Graceful shutdown
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
     * Listing 8.9 Graceful shutdown
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
        // syncUninterruptibly()方法：等待这个future，直到它完成为止，如果这个future失败，则重新抛出失败的原因。（不确定该方法是否是阻塞方法）
        bootstrap.connect(new InetSocketAddress("www.manning.com", 80)).syncUninterruptibly();
        Future<?> future = group.shutdownGracefully(); // shutdownGracefully()方法将释放所有的资源，并且关闭所有的当前正在使用中的Channel
        // block until the group has shutdown
        future.syncUninterruptibly();
    }
}
