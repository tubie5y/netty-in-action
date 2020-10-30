package nia.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.oio.OioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * Listing 8.8 Using Bootstrap with DatagramChannel
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 * @author <a href="mailto:mawolfthal@gmail.com">Marvin Wolfthal</a>
 */
public class BootstrapDatagramChannel {

    /**
     * Listing 8.8 Using Bootstrap with DatagramChannel
     */
    public void bootstrap() {
        Bootstrap bootstrap = new Bootstrap(); // 创建一个Bootstrap 的实例以创建和绑定新的数据报Channel
        bootstrap.group(new OioEventLoopGroup()) // 设置EventLoopGroup，其提供了用以处理Channel 事件的EventLoop
                .channel(OioDatagramChannel.class) // 指定Channel的实现
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() { // 设置用以处理Channel 的I/O 以及数据的ChannelInboundHandler
                             @Override
                             public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                 // Do something with the packet
                             }
                         }
                );
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(0)); // 调用bind()方法，因为该协议是无连接的
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Channel bound");
                } else {
                    System.err.println("Bind attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
