package nia.chapter13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Listing 13.2 LogEventEncoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress) { // LogEventEncoder 创建了即将被发送到指定的InetSocketAddress 的DatagramPacket 消息
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LogEvent logEvent, List<Object> out) throws Exception {
        byte[] file = logEvent.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = channelHandlerContext.alloc().buffer(file.length + msg.length + 1);
        buf.writeBytes(file); // 将文件名写入到ByteBuf 中
        buf.writeByte(LogEvent.SEPARATOR); // 添加一个SEPARATOR
        buf.writeBytes(msg); // 将日志消息写入ByteBuf 中　
        out.add(new DatagramPacket(buf, remoteAddress)); // 将一个拥有数据和目的地地址的新DatagramPacket添加到出站的消息列表中
    }
}
