package nia.chapter2.echoclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 代码清单 2-3 客户端的 ChannelHandler
 *
 * - 通过ChannelHandler实现客户端逻辑
 *     - 如同服务器,客户端将拥有一个用来处理数据的 ChannelInboundHandler。 在这个场景下,
 *       你将扩展 SimpleChannelInboundHandler 类以处理所有必须的任务
 *
 * - SimpleChannelInboundHandler与ChannelInboundHandler
 *     - 你可能会想：为什么我们在客户端使用的是SimpleChannelInboundHandler，
 *       而不是在EchoServerHandler中所使用的ChannelInboundHandlerAdapter呢？
 *       这和两个因素的相互作用有关：业务逻辑如何处理消息以及Netty如何管理资源。
 *
 *      - 在客户端，当channelRead0()方法完成时，你已经有了传入消息，并且已经处理完它了。当该方法返回时，
 *        SimpleChannelInboundHandler负责释放指向保存该消息的ByteBuf的内存引用。
 *
 *      - 在EchoServerHandler中，你仍然需要将传入消息回送给发送者，而write()操作是异步的，
 *        直到channelRead()方法返回后可能仍然没有完成（如代码清单2-1所示）。
 *        为此，EchoServerHandler扩展了ChannelInboundHandlerAdapter，其在这个时间点上不会释放消息。
 *        
 *      - 消息在EchoServerHandler的channelReadComplete()方法中，当writeAndFlush()方法被调用时被释放（见代码清单2-1）。
 *
 * 第5章和第6章将对消息的资源管理进行详细的介绍。
 */
@Sharable //标记该类的实例可以被多个Channel共享
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * - channelActive()——在到服务器的连接已经建立之后将被调用；
     *
     * - 首先，你重写了channelActive()方法，其将在一个连接建立时被调用。这确保了数据将会被尽可能快地写入服务器，
     *   其在这个场景下是一个编码了字符串"Netty rocks!"的字节缓冲区。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 当被通知Channel是活跃的时候，发送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    /**
     * - channelRead0()——当从服务器接收到一条消息时被调用；
     *
     * - 你重写了channelRead0()方法。每当接收数据时，都会调用这个方法。需要注意的是，由服务器发送的消息可能会被分块接收。
     *   也就是说，如果服务器发送了5字节，那么不能保证这5字节会被一次性接收。即使是对于这么少量的数据，channelRead0()方法也可能会被调用两次，
     *   第一次使用一个持有3字节的ByteBuf（Netty的字节容器），第二次使用一个持有2字节的ByteBuf。
     *   作为一个面向流的协议，TCP保证了字节数组将会按照服务器发送它们的顺序被接收。
     *
     * - 当该方法返回时，SimpleChannelInboundHandler 负责释放指向保存该消息的 ByteBuf 的内存引用。
     * - 当该方法返回时，SimpleChannelInboundHandler 负责关闭Channel？
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        // 记录已接收消息的转储
        System.out.println("[Client] received: " + in.toString(CharsetUtil.UTF_8));
    }

    /**
     * - exceptionCaught()——在处理过程中引发异常时被调用。
     *
     * - 重写的第三个方法是exceptionCaught()。如同在EchoServerHandler（见代码清单2-2）中所示，
     *   记录Throwable，关闭Channel，在这个场景下，终止到服务器的连接。
     *
     * - 客户端连接一个已经关闭的服务端会发生什么?
     *   - 客户端试图连接服务器, 其预期运行在localhost:9999上。但是连接失败了,因为服务器在这之前就已经停止了,
     *     所以在客户端导致了一个 java.net.ConnectException。这个异常触发了 EchoClientHandler 的 exceptionCaught() 方法,
     *     打印出了栈跟踪并关闭了Channel。
     */

    @Override
    // 在发生异常时，记录错误并关闭Channel
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
