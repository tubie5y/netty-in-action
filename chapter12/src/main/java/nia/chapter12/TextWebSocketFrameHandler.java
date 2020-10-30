package nia.chapter12;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Listing 12.2 Handling text frames
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> { // 扩展SimpleChannelInboundHandler，并处理TextWebSocketFrame 消息
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception { // 重写userEventTriggered()方法以处理自定义事件
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HttpRequestHandler.class); // 如果该事件表示握手成功，则从该Channelipeline中移除HttpRequestHandler，因为将不会接收到任何HTTP 消息了
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));// 通知所有已经连接的WebSocket 客户端新的客户端已经连接上了
            group.add(ctx.channel()); // 将新的WebSocket Channel添加到ChannelGroup 中，以便它可以接收到所有的消息
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        group.writeAndFlush(msg.retain()); // 增加消息的引用计数，并将它写到ChannelGroup 中所有已经连接的客户端
    }
}
