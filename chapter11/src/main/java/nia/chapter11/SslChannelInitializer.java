package nia.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * Listing 11.1 Adding SSL/TLS support
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean startTls;

    /**
     * @param context : 传入要使用的SslContext
     * @param startTls: 如果设置为true，第一个写入的消息将不会被加密（客户端应该设置为true）
     */
    public SslChannelInitializer(SslContext context, boolean startTls) {
        this.context = context;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = context.newEngine(ch.alloc()); // 对于每个SslHandler 实例，都使用Channel 的ByteBufAllocator 从SslContext 获取一个新的SSLEngine
        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls)); // 将SslHandler 作为第一个ChannelHandler 添加到ChannelPipeline 中
    }
}
