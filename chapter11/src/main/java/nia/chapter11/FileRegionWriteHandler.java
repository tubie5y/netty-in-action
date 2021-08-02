package nia.chapter11;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by kerr.
 * <p>
 * Listing 11.11 Transferring file contents with FileRegion (使用FileRegion传输文件的内容)
 *      代码清单11-11展示了如何通过从FileInputStream创建一个DefaultFileRegion，并将其写入Channel，从而利用零拷贝特性来传输一个文件的内容。
 */
public class FileRegionWriteHandler extends ChannelInboundHandlerAdapter {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final File FILE_FROM_SOMEWHERE = new File("");

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        File file = FILE_FROM_SOMEWHERE; //get reference from somewhere
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference from somewhere
        //...
        FileInputStream in = new FileInputStream(file); // 创建一个FileInputStream
        FileRegion region = new DefaultFileRegion(in.getChannel(), 0, file.length()); // 以该文件的完整长度创建一个新的DefaultFileRegion
        channel.writeAndFlush(region).addListener(new ChannelFutureListener() { // 发送该DefaultFileRegion，并注册一个ChannelFutureListener
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause(); // 处理失败
                    // Do something
                }
            }
        });
    }
}
