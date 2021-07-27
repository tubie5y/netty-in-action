package nia.chapter5;

import io.netty.buffer.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ByteProcessor;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

import static io.netty.channel.DummyChannelHandlerContext.DUMMY_INSTANCE;

/**
 * Created by kerr.
 *
 * Listing 5.1 Backing array
 *
 * Listing 5.2 Direct buffer data access
 *
 * Listing 5.3 Composite buffer pattern using ByteBuffer
 *
 * Listing 5.4 Composite buffer pattern using CompositeByteBuf
 *
 * Listing 5.5 Accessing the data in a CompositeByteBuf
 *
 * Listing 5.6 Access data
 *
 * Listing 5.7 Read all data
 *
 * Listing 5.8 Write data
 *
 * Listing 5.9 Using ByteBufProcessor to find \r
 *
 * Listing 5.10 Slice a ByteBuf
 *
 * Listing 5.11 Copying a ByteBuf
 *
 * Listing 5.12 get() and set() usage
 *
 * Listing 5.13 read() and write() operations on the ByteBuf
 *
 * Listing 5.14 Obtaining a ByteBufAllocator reference
 *
 * Listing 5.15 Reference counting
 *
 * Listing 5.16 Release reference-counted object
 */
public class ByteBufExamples {
    private final static Random random = new Random();
    private static final ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = DUMMY_INSTANCE;
    /**
     * Listing 5.1 Backing array (支撑数组)
     *
     * 1．堆缓冲区
     * - 最常用的ByteBuf模式是将数据存储在JVM的堆空间中。这种模式被称为支撑数组（backing array），它能在没有使用池化的情况下提供快速的分配和释放。
     *   这种方式，如代码清单5-1所示，非常适合于有遗留的数据需要处理的情况。
     * - 注意　当hasArray()方法返回false时，尝试访问支撑数组将触发一个UnsupportedOperationException。这个模式类似于JDK的ByteBuffer的用法。
     */
    public static void heapBuffer() {
        ByteBuf heapBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        if (heapBuf.hasArray()) { // 检查ByteBuf 是否有一个支撑数组
            byte[] array = heapBuf.array(); // 如果有，则获取对该数组的引用
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex(); // 计算第一个字节的偏移量。
            int length = heapBuf.readableBytes(); // 获得可读字节数
            handleArray(array, offset, length); // 使用数组、偏移量和长度作为参数调用你的方法
        }
    }

    /**
     * Listing 5.2 Direct buffer data access (访问直接缓冲区的数据)
     *
     * 2．直接缓冲区
     * - 直接缓冲区是另外一种ByteBuf模式。我们期望用于对象创建的内存分配永远都来自于堆中，但这并不是必须的——NIO在JDK 1.4中引入的ByteBuffer类允许JVM实现通过本地调用来分配内存。
     *   这主要是为了避免在每次调用本地I/O操作之前（或者之后）将缓冲区的内容复制到一个中间缓冲区（或者从中间缓冲区把内容复制到缓冲区）。
     *
     * - ByteBuffer的Javadoc明确指出：“直接缓冲区的内容将驻留在常规的会被垃圾回收的堆之外。”这也就解释了为何直接缓冲区对于网络数据传输是理想的选择。
     *   如果你的数据包含在一个在堆上分配的缓冲区中，那么事实上，在通过套接字发送它之前，JVM将会在内部把你的缓冲区复制到一个直接缓冲区中。
     *
     * - 直接缓冲区的主要缺点是，相对于基于堆的缓冲区，它们的分配和释放都较为昂贵。如果你正在处理遗留代码，你也可能会遇到另外一个缺点：因为数据不是在堆上，所以你不得不进行一次复制，如代码清单5-2所示。
     *
     * - 显然，与使用支撑数组相比，这涉及的工作更多。因此，如果事先知道容器中的数据将会被作为数组来访问，你可能更愿意使用堆内存。
     */
    public static void directBuffer() {
        ByteBuf directBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        if (!directBuf.hasArray()) { // 检查ByteBuf 是否由数组支撑。如果不是，则这是一个直接缓冲区
            int length = directBuf.readableBytes(); // 获取可读字节数
            byte[] array = new byte[length]; // 分配一个新的数组来保存具有该长度的字节数据
            directBuf.getBytes(directBuf.readerIndex(), array); // 将字节复制到该数组
            handleArray(array, 0, length); // 使用数组、偏移量和长度作为参数调用你的方法
        }
    }

    /**
     * Listing 5.3 Composite buffer pattern using ByteBuffer (使用ByteBuffer的复合缓冲区模式)
     *
     * 3．复合缓冲区
     * - 第三种也是最后一种模式使用的是复合缓冲区，它为多个ByteBuf提供一个聚合视图。在这里你可以根据需要添加或者删除ByteBuf实例，这是一个JDK的ByteBuffer实现完全缺失的特性。
     *
     * - Netty通过一个ByteBuf子类——CompositeByteBuf——实现了这个模式，它提供了一个将多个缓冲区表示为单个合并缓冲区的虚拟表示。
     *
     * - 警告: CompositeByteBuf中的ByteBuf实例可能同时包含直接内存分配和非直接内存分配。如果其中只有一个实例，
     *   那么对CompositeByteBuf上的hasArray()方法的调用将返回该组件上的hasArray()方法的值；否则它将返回false。
     *
     * - 为了举例说明，让我们考虑一下一个由两部分——头部和主体——组成的将通过HTTP协议传输的消息。这两部分由应用程序的不同模块产生，将会在消息被发送的时候组装。
     *   该应用程序可以选择为多个消息重用相同的消息主体。当这种情况发生时，对于每个消息都将会创建一个新的头部。
     * - 因为我们不想为每个消息都重新分配这两个缓冲区，所以使用CompositeByteBuf是一个完美的选择。它在消除了没必要的复制的同时，暴露了通用的ByteBuf API。图5-2展示了生成的消息布局。
     *
     *
     * 代码清单5-3展示了如何通过使用JDK的ByteBuffer来实现这一需求。创建了一个包含两个ByteBuffer的数组用来保存这些消息组件，同时创建了第三个ByteBuffer用来保存所有这些数据的副本。
     * 分配和复制操作，以及伴随着对数组管理的需要，使得这个版本的实现效率低下而且笨拙。代码清单5-4展示了一个使用了CompositeByteBuf的版本。
     */
    public static void byteBufferComposite(ByteBuffer header, ByteBuffer body) {
        // Use an array to hold the message parts
        ByteBuffer[] message =  new ByteBuffer[]{ header, body };

        // Create a new ByteBuffer and use copy to merge the header and body
        ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
        message2.put(header);
        message2.put(body);
        message2.flip();
    }


    /**
     * Listing 5.4 Composite buffer pattern using CompositeByteBuf (使用CompositeByteBuf的复合缓冲区模式)
     */
    public static void byteBufComposite() {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf headerBuf = BYTE_BUF_FROM_SOMEWHERE; // can be backing or direct
        ByteBuf bodyBuf = BYTE_BUF_FROM_SOMEWHERE;   // can be backing or direct
        messageBuf.addComponents(headerBuf, bodyBuf); // 将ByteBuf 实例追加到CompositeByteBuf
        //...
        messageBuf.removeComponent(0); // remove the header: 删除位于索引位置为 0（第一个组件）的ByteBuf
        for (ByteBuf buf : messageBuf) { // 循环遍历所有的ByteBuf 实例
            System.out.println(buf.toString());
        }
    }

    /**
     * Listing 5.5 Accessing the data in a CompositeByteBuf (访问CompositeByteBuf中的数据)
     *
     * CompositeByteBuf可能不支持访问其支撑数组，因此访问CompositeByteBuf中的数据类似于（访问）直接缓冲区的模式，如代码清单5-5所示。
     *
     * 需要注意的是，Netty使用了CompositeByteBuf来优化套接字的I/O操作，尽可能地消除了由JDK的缓冲区实现所导致的性能以及内存使用率的惩罚。
     * 这种优化发生在Netty的核心代码中，因此不会被暴露出来，但是你应该知道它所带来的影响。
     *
     * CompositeByteBuf API　除了从ByteBuf继承的方法，CompositeByteBuf提供了大量的附加功能。请参考Netty的Javadoc以获得该API的完整列表。
     */
    public static void byteBufCompositeArray() {
        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
        int length = compBuf.readableBytes(); // 获得可读字节数
        byte[] array = new byte[length]; // 分配一个具有可读字节数长度的新数组
        compBuf.getBytes(compBuf.readerIndex(), array); // 将字节读到该数组中
        handleArray(array, 0, array.length); // 使用偏移量和长度作为参数使用该数组
    }

    /**
     * Listing 5.6 Access data (访问数据)
     *
     * 字节级操作 -- 随机访问索引
     *      - 如同在普通的Java字节数组中一样，ByteBuf的索引是从零开始的：第一个字节的索引是0，最后一个字节的索引总是 capacity() - 1。代码清单5-6表明，对存储机制的封装使得遍历ByteBuf的内容非常简单。
     *
     *      - 需要注意的是，使用那些需要一个索引值参数的方法（的其中）之一来访问数据既不会改变readerIndex也不会改变writerIndex。
     *        如果有需要，也可以通过调用readerIndex(index)或者writerIndex(index)来手动移动这两者。
     */
    public static void byteBufRelativeAccess() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.getByte(i);
            System.out.println((char) b);
        }
    }

    /**
     * Listing 5.7 Read all data (读取所有数据)
     *      - 代码清单5-7展示了如何读取所有可以读的字节。
     *
     * 可读字节
     *      - ByteBuf的可读字节分段存储了实际数据。新分配的、包装的或者复制的缓冲区的默认的readerIndex值为0。
     *        任何名称以read或者skip开头的操作都将检索或者跳过位于当前readerIndex的数据，并且将它增加已读字节数。
     *
     *      - 如果被调用的方法需要一个ByteBuf参数作为写入的目标，并且没有指定目标索引参数，那么该目标缓冲区的writerIndex也将被增加，例如：readBytes(ByteBuf dest);
     *
     */
    public static void readAllData() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        while (buffer.isReadable()) {
            System.out.println(buffer.readByte());
        }
    }

    /**
     * Listing 5.8 Write data (写数据)
     *
     * 代码清单5-8是一个用随机整数值填充缓冲区，直到它空间不足为止的例子。writeableBytes()方法在这里被用来确定该缓冲区中是否还有足够的空间。
     *
     * 可写字节
     *      可写字节分段是指一个拥有未定义内容的、写入就绪的内存区域。新分配的缓冲区的writerIndex的默认值为0。任何名称以write开头的操作都将从当前的writerIndex处开始写数据，
     *      并将它增加已经写入的字节数。如果写操作的目标也是ByteBuf，并且没有指定源索引的值，则源缓冲区的readerIndex也同样会被增加相同的大小。这个调用如下所示：writeBytes(ByteBuf dest);
     */
    public static void write() {
        // Fills the writable bytes of a buffer with random integers.
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        while (buffer.writableBytes() >= 4) {
            buffer.writeInt(random.nextInt());
        }
    }

    /**
     * Listing 5.9 Using ByteProcessor to find \r (使用ByteBufProcessor来寻找\r)
     *      代码清单5-9展示了一个查找回车符（\r）的例子。
     *
     * use {@link io.netty.buffer.ByteBufProcessor in Netty 4.0.x}
     */
    public static void byteProcessor() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        int index = buffer.forEachByte(ByteProcessor.FIND_CR);
    }

    /**
     * Listing 5.9 Using ByteBufProcessor to find \r
     *
     * use {@link io.netty.util.ByteProcessor in Netty 4.1.x}
     */
    public static void byteBufProcessor() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        int index = buffer.forEachByte(ByteBufProcessor.FIND_CR);
    }

    /**
     * Listing 5.10 Slice a ByteBuf (对ByteBuf进行切片)
     *      代码清单5-10展示了如何使用slice(int,int)方法来操作ByteBuf的一个分段。
     *
     * 派生缓冲区
     *      - 派生缓冲区为ByteBuf提供了以专门的方式来呈现其内容的视图。这类视图是通过以下方法被创建的：
     *          - duplicate()；
     *          - slice()；
     *          - slice(int, int)；
     *          - Unpooled.unmodifiableBuffer(…)；
     *          - order(ByteOrder)；
     *          - readSlice(int)。
     *      - 每个这些方法都将返回一个新的ByteBuf实例，它具有自己的读索引、写索引和标记索引。其内部存储和JDK的ByteBuffer一样也是共享的。
     *        这使得派生缓冲区的创建成本是很低廉的，但是这也意味着，如果你修改了它的内容，也同时修改了其对应的源实例，所以要小心。
     *
     *      - ByteBuf复制: 如果需要一个现有缓冲区的真实副本，请使用copy()或者copy(int, int)方法。不同于派生缓冲区，由这个调用所返回的ByteBuf拥有独立的数据副本。
     *        除了修改原始ByteBuf的切片或者副本的效果以外，这两种场景是相同的。只要有可能，使用slice()方法来避免复制内存的开销。
     */
    public static void byteBufSlice() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8); // 创建一个用于保存给定字符串的字节的ByteBuf
        ByteBuf sliced = buf.slice(0, 15); // 创建该ByteBuf 从索引0 开始到索引15结束的一个新切片
        System.out.println(sliced.toString(utf8)); // 将打印“Netty in Action”
        buf.setByte(0, (byte)'J'); // 更新索引0 处的字节
        assert buf.getByte(0) == sliced.getByte(0); // 将会成功，因为数据是共享的，对其中一个所做的更改对另外一个也是可见的
    }

    /**
     * Listing 5.11 Copying a ByteBuf (复制一个ByteBuf)
     *      现在，让我们看看ByteBuf的分段的副本和切片有何区别，如代码清单5-11所示。
     */
    public static void byteBufCopy() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8); // 创建ByteBuf 以保存所提供的字符串的字节
        ByteBuf copy = buf.copy(0, 15); // 创建该ByteBuf 从索引0 开始到索引15结束的分段的副本
        System.out.println(copy.toString(utf8)); // 将打印“Netty in Action”
        buf.setByte(0, (byte)'J'); // 更新索引0 处的字节
        assert buf.getByte(0) != copy.getByte(0); // 将会成功，因为数据不是共享的
    }

    /**
     * Listing 5.12 get() and set() usage (get()和set()方法的用法)
     *      代码清单5-12说明了get()和set()方法的用法，表明了它们不会改变读索引和写索引。
     */
    public static void byteBufSetGet() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8); // 创建一个新的ByteBuf以保存给定字符串的字节
        System.out.println((char)buf.getByte(0)); // 打印第一个字符'N'
        int readerIndex = buf.readerIndex(); // 存储当前的readerIndex 和writerIndex
        int writerIndex = buf.writerIndex();
        buf.setByte(0, (byte)'B'); // 将索引0 处的字节更新为字符'B'
        System.out.println((char)buf.getByte(0)); // 打印第一个字符，现在是'B'
        assert readerIndex == buf.readerIndex(); // 将会成功，因为这些操作并不会修改相应的索引
        assert writerIndex == buf.writerIndex();
    }

    /**
     * Listing 5.13 read() and write() operations on the ByteBuf (ByteBuf上的read()和write()操作)
     */
    public static void byteBufWriteRead() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8); // 创建一个新的ByteBuf 以保存给定字符串的字节
        System.out.println((char)buf.readByte()); // 打印第一个字符'N'
        int readerIndex = buf.readerIndex(); // 存储当前的readerIndex
        int writerIndex = buf.writerIndex(); // 存储当前的writerIndex
        buf.writeByte((byte)'?'); // 将字符'?'追加到缓冲区
        assert readerIndex == buf.readerIndex();
        assert writerIndex != buf.writerIndex(); // 将会成功，因为writeByte()方法移动了writerIndex
    }

    private static void handleArray(byte[] array, int offset, int len) {}

    /**
     * Listing 5.14 Obtaining a ByteBufAllocator reference (获取一个到ByteBufAllocator的引用)
     *      可以通过Channel（每个都可以有一个不同的ByteBufAllocator实例）或者绑定到ChannelHandler的ChannelHandlerContext获取一个到ByteBufAllocator的引用。代码清单5-14说明了这两种方法。
     *
     * - Netty提供了两种ByteBufAllocator的实现：PooledByteBufAllocator和UnpooledByteBufAllocator。前者池化了ByteBuf的实例以提高性能并最大限度地减少内存碎片。
     *   此实现使用了一种称为jemalloc的已被大量现代操作系统所采用的高效方法来分配内存。后者的实现不池化ByteBuf实例，并且在每次它被调用时都会返回一个新的实例。
     * - 虽然Netty默认使用了PooledByteBufAllocator，但这可以很容易地通过ChannelConfig API或者在引导你的应用程序时指定一个不同的分配器来更改。更多的细节可在第8章中找到。
     */
    public static void obtainingByteBufAllocatorReference(){
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference form somewhere
        ByteBufAllocator allocator = channel.alloc(); // 从Channel 获取一个到ByteBufAllocator 的引用
        //...
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere: 从ChannelHandlerContext 获取一个到ByteBufAllocator 的引用
        ByteBufAllocator allocator2 = ctx.alloc();
        //...
    }

    /**
     * Listing 5.15 Reference counting (引用计数)
     * */
    public static void referenceCounting(){
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference form somewhere
        ByteBufAllocator allocator = channel.alloc(); // 从Channel 获取ByteBufAllocator
        //...
        ByteBuf buffer = allocator.directBuffer(); // 从ByteBufAllocator分配一个ByteBuf
        assert buffer.refCnt() == 1; // 检查引用计数是否为预期的1
        //...
    }

    /**
     * Listing 5.16 Release reference-counted object (释放引用计数的对象)
     */
    public static void releaseReferenceCountedObject(){
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        boolean released = buffer.release(); // 减少到该对象的活动引用。当减少到0 时，该对象被释放，并且该方法返回true
        //...
    }


}
