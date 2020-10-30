package nia.chapter10;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * Listing 10.10 CombinedChannelDuplexHandler<I,O>
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */

public class CombinedByteCharCodec extends CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> { // 通过该解码器和编码器实现参数化CombinedByteCharCodec
    public CombinedByteCharCodec() {
        super(new ByteToCharDecoder(), new CharToByteEncoder()); // 将委托实例传递给父类
    }
}
