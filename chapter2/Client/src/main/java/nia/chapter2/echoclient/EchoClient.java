package nia.chapter2.echoclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 代码清单 2-4 客户端的主类
 *
 * - Echo客户端将会：
 * （1）连接到服务器；
 * （2）发送一个或者多个消息；
 * （3）对于每个消息，等待并接收从服务器发回的相同的消息；
 * （4）关闭连接。
 * 编写客户端所涉及的两个主要代码部分也是业务逻辑和引导，和你在服务器中看到的一样。
 *
 * - 2.4.2　引导客户端
 *     - 引导客户端类似于引导服务器，不同的是，客户端是使用主机和端口参数来连接远程地址，
 *       也就是这里的Echo服务器的地址，而不是绑定到一个一直被监听的端口。
 *
 *     - 和之前一样，使用了NIO传输。注意，你可以在客户端和服务器上分别使用不同的传输。例如，在服务器端使用NIO传输，而在客户端使用OIO传输。
 *        在第4章，我们将探讨影响你选择适用于特定用例的特定传输的各种因素和场景。
 *
 * - 让我们回顾一下这一节中所介绍的要点：
 *     - 为初始化客户端，创建了一个Bootstrap实例；
 *     - 为进行事件处理分配了一个NioEventLoopGroup实例，其中事件处理包括创建新的连接以及处理入站和出站数据；
 *     - 为服务器连接创建了一个InetSocketAddress实例；
 *     - 当连接被建立时，一个EchoClientHandler实例会被安装到（该Channel的）ChannelPipeline中；
 *     - 在一切都设置完成后，调用Bootstrap.connect()方法连接到远程节点；
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建Bootstrap
            Bootstrap b = new Bootstrap();
            // 指定 EventLoopGroup 以处理客户端事件；需要适用于NIO的实现
            // 在内部，将会为每个Channel分配一个EventLoop，用以处理所有事件
            b.group(group)
                // 适用于 NIO 传输的Channel类型
                .channel(NioSocketChannel.class)
                // 设置服务器的 InetSocketAddress
                .remoteAddress(new InetSocketAddress(host, port))
                // 在创建 Channel时，向 ChannelPipeline 中添加一个 EchoClientHandler 实例
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                });
            // 连接到远程节点，阻塞等待直到连接完成
            ChannelFuture f = b.connect().sync();
            // 阻塞，直到 Channel 关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关闭线程池并且释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
            return;
        }

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}

