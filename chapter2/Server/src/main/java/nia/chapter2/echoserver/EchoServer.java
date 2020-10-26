package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * 2.3.2　引导服务器
 *      在讨论过由EchoServerHandler实现的核心业务逻辑之后，我们现在可以探讨引导服务器本身的过程了，具体涉及以下内容：
 *          绑定到服务器将在其上监听并接受传入连接请求的端口；
 *          配置Channel，以将有关的入站消息通知给EchoServerHandler实例。
 *
 *      在➋处，你创建了一个ServerBootstrap实例。因为你正在使用的是NIO传输，所以你指定了NioEventLoopGroup➊来接受和处理新的连接，
 * 并且将Channel的类型指定为NioServerSocketChannel➌。在此之后，你将本地地址设置为一个具有选定端口的InetSocketAddress➍。服务器将绑定到这个地址以监听新的连接请求。
 *      在➎处，你使用了一个特殊的类——ChannelInitializer。这是关键。当一个新的连接被接受时，一个新的子Channel将会被创建，
 * 而ChannelInitializer将会把一个你的EchoServerHandler的实例添加到该Channel的ChannelPipeline中。正如我们之前所解释的，
 * 这个ChannelHandler将会收到有关入站消息的通知。
 *      接下来你绑定了服务器➏，并等待绑定完成。（对sync()方法的调用将导致当前Thread阻塞，一直到绑定操作完成为止）。
 * 在➐处，该应用程序将会阻塞等待直到服务器的Channel关闭（因为你在Channel的Close Future上调用了sync()方法）。
 * 然后，你将可以关闭EventLoopGroup，并释放所有的资源，包括所有被创建的线程➑。
 *
 *      这个示例使用了NIO，因为得益于它的可扩展性和彻底的异步性，它是目前使用最广泛的传输。但是也可以使用一个不同的传输实现。如果你想要在自己的服务器中使用OIO传输，
 * 将需要指定OioServerSocketChannel和OioEventLoopGroup。我们将在第4章中对传输进行更加详细的探讨。
 *
 * 与此同时，让我们回顾一下你刚完成的服务器实现中的重要步骤。下面这些是服务器的主要代码组件：
 *      EchoServerHandler实现了业务逻辑；
 *      main()方法引导了服务器；
 * 引导过程中所需要的步骤如下：
 *      创建一个ServerBootstrap的实例以引导和绑定服务器；
 *      创建并分配一个NioEventLoopGroup实例以进行事件的处理，如接受新连接以及读/写数据；
 *      指定服务器绑定的本地的InetSocketAddress；
 *      使用一个EchoServerHandler的实例初始化每一个新的Channel；
 *      调用ServerBootstrap.bind()方法以绑定服务器。
 * 在这个时候，服务器已经初始化，并且已经就绪能被使用了。
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
            return;
        }
        // 设置端口值（如果端口参数的格式不正确，则抛出一个NumberFormatException）
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup(); //①
        try {
            // 创建ServerBootstrap
            ServerBootstrap b = new ServerBootstrap(); // ②
            b.group(group)
                .channel(NioServerSocketChannel.class) // ③指定所使用的NIO传输Channel
                .localAddress(new InetSocketAddress(port)) //④ 使用指定的端口设置套接字地址
                // 添加一个EchoServerHandler到子Channel的ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() { //⑤
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // EchoServerHandler被标注为@Shareable，所以我们可以总是使用同样的实例
                        ch.pipeline().addLast(serverHandler);
                    }
                });

            // 异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成
            ChannelFuture f = b.bind().sync(); //⑥
            System.out.println(EchoServer.class.getName() + " started and listening for connections on " + f.channel().localAddress());
            // 获取Channel的CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();// ⑦
        } finally {
            group.shutdownGracefully().sync(); //⑧ 关闭EventLoopGroup，释放所有的资源
        }
    }
}
