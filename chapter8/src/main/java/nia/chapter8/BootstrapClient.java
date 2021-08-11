package nia.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 8.1 Bootstrapping a client (引导一个客户端)
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 * @author <a href="mailto:mawolfthal@gmail.com">Marvin Wolfthal</a>
 */
public class BootstrapClient {
    public static void main(String args[]) {
        BootstrapClient client = new BootstrapClient();
        client.bootstrap();
    }

    /**
     * Listing 8.1 Bootstrapping a client (引导一个客户端)
     *      代码清单8-1中的代码引导了一个使用NIO TCP传输的客户端。
     *
     * 这个示例使用了前面提到的流式语法；这些方法（除了connect()方法以外）将通过每次方法调用所返回的对Bootstrap实例的引用链接在一起。
     */
    public void bootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap(); //  ←--  创建一个Bootstrap类的实例以创建和连接新的客户端Channel
        // 设置用于处理Channel所有事件的EventLoopGroup
        bootstrap.group(group) //  ← -- 设置EventLoopGroup，提供用于处理Channel事件的EventLoop
                .channel(NioSocketChannel.class) //  ← --   指定要使用的Channel 实现
                // 设置将被添加到ChannelPipeline以接收事件通知的ChannelHandler
                .handler(new SimpleChannelInboundHandler<ByteBuf>() { //  ←--  设置一个用于处理Channel的I/O 事件和数据的ChannelInboundHandler
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        System.out.println("Received data");
                    }
                });
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.manning.com", 80)); //  ←--   连接到远程主机
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Connection established");
                } else {
                    System.err.println("Connection attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
