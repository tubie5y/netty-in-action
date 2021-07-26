package nia.chapter2.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Listing 2.1 EchoServerHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable // 标示一个ChannelHandler可以被多个Channel安全地共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * channelRead()——对于每个传入的消息都要调用；
     *
     * ChannelInboundHandlerAdapter有一个直观的API，并且它的每个方法都可以被重写以挂钩到事件生命周期的恰当点上。
     * 因为需要处理所有接收到的数据，所以你重写了channelRead()方法。在这个服务器应用程序中，你将数据简单地回送给了远程节点。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("[Server] received: " + in.toString(CharsetUtil.UTF_8)); //  ← --    将消息记录到控制台
        ctx.write(in); //  ← --  将接收到的消息写给发送者，而不冲刷出站消息
    }

    /**
     * channelReadComplete()——通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息；
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE); // 　←--　将未决消息冲刷到远程节点，并且关闭该Channel
    }

    /**
     * exceptionCaught()——在读取操作期间，有异常抛出时会调用。
     *
     * 重写exceptionCaught()方法允许你对Throwable的任何子类型做出反应，在这里你记录了异常并关闭了连接。
     * 虽然一个更加完善的应用程序也许会尝试从异常中恢复，但在这个场景下，只是通过简单地关闭连接来通知远程节点发生了错误。
     *
     * 如果不捕获异常，会发生什么呢
     *      每个Channel都拥有一个与之相关联的ChannelPipeline，其持有一个ChannelHandler的实例链。在默认的情况下，ChannelHandler会把对它的方法的调用转发给链中的下一个ChannelHandler。
     *      因此，如果exceptionCaught()方法没有被该链中的某处实现，那么所接收的异常将会被传递到ChannelPipeline的尾端并被记录。
     *      为此，你的应用程序应该提供至少有一个实现了exceptionCaught()方法的ChannelHandler。（6.4节详细地讨论了异常处理）。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();// 打印异常栈跟踪
        ctx.close(); // 关闭该Channel
    }
}
