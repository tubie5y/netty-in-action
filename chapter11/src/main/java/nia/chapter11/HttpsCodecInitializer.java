package nia.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * Listing 11.5 Using HTTPS (使用HTTPS)
 *      代码清单11-5显示，启用HTTPS只需要将SslHandler添加到ChannelPipeline的ChannelHandler组合中。
 *
 * - 前面的代码是一个很好的例子，说明了Netty的架构方式是如何将代码重用变为杠杆作用的。
 *   只需要简单地将一个ChannelHandler添加到ChannelPipeline中，便可以提供一项新功能，甚至像加密这样重要的功能都能提供。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class HttpsCodecInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean isClient;

    public HttpsCodecInitializer(SslContext context, boolean isClient) {
        this.context = context;
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine = context.newEngine(ch.alloc());
        pipeline.addFirst("ssl", new SslHandler(engine)); // 将SslHandler 添加到ChannelPipeline 中以使用HTTPS

        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec()); // 如果是客户端，则添加HttpClientCodec
        } else {
            pipeline.addLast("codec", new HttpServerCodec()); // 如果是服务器，则添加HttpServerCodec
        }
    }
}
