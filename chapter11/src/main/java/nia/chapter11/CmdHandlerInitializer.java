package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * 代码清单 11-9 使用 ChannelInitializer 安装解码器
 *
 * - 如果你正在使用除了行尾符之外的分隔符分隔的帧，那么你可以以类似的方式使用Delimiter-BasedFrameDecoder，只需要将特定的分隔符序列指定到其构造函数即可。
 *
 * - 这些解码器是实现你自己的基于分隔符的协议的工具。作为示例，我们将使用下面的协议规范：
 *     - 传入数据流是一系列的帧，每个帧都由换行符（\n）分隔；
 *     - 每个帧都由一系列的元素组成，每个元素都由单个空格字符分隔；
 *     - 一个帧的内容代表一个命令，定义为一个命令名称后跟着数目可变的参数。
 *
 * - 我们用于这个协议的自定义解码器将定义以下类：
 *     - Cmd——将帧（命令）的内容存储在ByteBuf中，一个ByteBuf用于名称，另一个用于参数；
 *     - CmdDecoder——从被重写了的decode()方法中获取一行字符串，并从它的内容构建一个Cmd的实例；
 *     - CmdHandler ——从CmdDecoder获取解码的Cmd对象，并对它进行一些处理；
 *     - CmdHandlerInitializer ——为了简便起见，我们将会把前面的这些类定义为专门的ChannelInitializer的嵌套类，其将会把这些ChannelInboundHandler安装到ChannelPipeline中。
 *
 * - 正如将在代码清单11-9中所能看到的那样，这个解码器的关键是扩展 LineBasedFrameDecoder。
 *
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class CmdHandlerInitializer extends ChannelInitializer<Channel> {
    private static final byte SPACE = (byte)' ';

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //添加 CmdDecoder 以提取 Cmd 对象，并将它转发给下一个 ChannelInboundHandler
        pipeline.addLast(new CmdDecoder(64 * 1024));
        //添加 CmdHandler 以接收和处理 Cmd 对象
        pipeline.addLast(new CmdHandler());
    }

    //Cmd POJO
    public static final class Cmd {
        private final ByteBuf name;
        private final ByteBuf args;

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;
        }

        public ByteBuf name() {
            return name;
        }

        public ByteBuf args() {
            return args;
        }
    }

    public static final class CmdDecoder extends LineBasedFrameDecoder {
        public CmdDecoder(int maxLength) {
            super(maxLength);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
            //从 ByteBuf 中提取由行尾符序列分隔的帧
            ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
            if (frame == null) {
                //如果输入中没有帧，则返回 null
                return null;
            }
            //查找第一个空格字符的索引。前面是命令名称，接着是参数
            int index = frame.indexOf(frame.readerIndex(), frame.writerIndex(), SPACE);
            //使用包含有命令名称和参数的切片创建新的 Cmd 对象
            return new Cmd(frame.slice(frame.readerIndex(), index),
                    frame.slice(index + 1, frame.writerIndex()));
        }
    }

    public static final class CmdHandler extends SimpleChannelInboundHandler<Cmd> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
            // Do something with the command
            //处理传经 ChannelPipeline 的 Cmd 对象
        }
    }
}
