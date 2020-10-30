package nia.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 8.5 Bootstrapping a server
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 * @author <a href="mailto:mawolfthal@gmail.com">Marvin Wolfthal</a>
 */
public class BootstrapSharingEventLoopGroup {

    /**
     * Listing 8.5 Bootstrapping a server
     */
    public void bootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap(); // 创建ServerBootstrap 以创建ServerSocketChannel，并绑定它
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup()) // 设置EventLoopGroup，其将提供用以处理Channel 事件的EventLoop
                .channel(NioServerSocketChannel.class) // 指定要使用的Channel 实现
                .childHandler( // 设置用于处理已被接受的子Channel 的I/O 和数据的ChannelInboundHandler　
                        new SimpleChannelInboundHandler<ByteBuf>() {
                            ChannelFuture connectFuture;

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                Bootstrap bootstrap = new Bootstrap(); // 创建一个Bootstrap类的实例以连接到远程主机
                                bootstrap.channel(NioSocketChannel.class) // 指定Channel的实现
                                        .handler(new SimpleChannelInboundHandler<ByteBuf>() { // 为入站I/O 设置ChannelInboundHandler
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
                                                System.out.println("Received data");
                                            }
                                        });
                                bootstrap.group(ctx.channel().eventLoop()); // 使用与分配给已被接受的子Channel 相同的EventLoop
                                connectFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80)); // 连接到远程节点
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                if (connectFuture.isDone()) { // 当连接完成时，执行一些数据操作（如代理）
                                    // do something with the data
                                }
                            }
                        });
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080)); // 通过配置好的ServerBootstrap绑定该ServerSocketChannel
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Server bound");
                } else {
                    System.err.println("Bind attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
