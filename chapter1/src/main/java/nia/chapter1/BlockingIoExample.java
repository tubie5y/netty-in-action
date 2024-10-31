package nia.chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kerr.
 * <p>
 * Listing 1.1 Blocking I/O example
 */
public class BlockingIoExample {

    /**
     * Listing 1.1 Blocking I/O example
     * - 代码清单1-1实现了SocketAPI的基本模式之一。
     *   这段代码片段将只能同时处理一个连接,要管理多个并发客户端,需要为每个新的客户端Socket创建一个新的Thread,如图1-1所示。
     *
     * - 让我们考虑一下这种方案(即为每个新的客户端Socket创建一个新的Thread)的影响。
     *   第一，在任何时候都可能有大量的线程处于休眠状态，只是等待输入或者输出数据就绪，这可能算是一种资源浪费。
     *   第二，需要为每个线程的调用栈都分配内存，其默认值大小区间为64 KB到1 MB，具体取决于操作系统。
     *   第三，即使Java虚拟机（JVM）在物理上可以支持非常大数量的线程，但是远在到达该极限之前，上下文切换所带来的开销就会带来麻烦，例如，在达到10 000个连接的时候。
     *
     * - 虽然这种并发方案对于支撑中小数量的客户端来说还算可以接受，但是为了支撑10_0000或者更多的并发连接所需要的资源使得它很不理想。幸运的是，还有一种方案(Java NIO)。
     *
     * - 尽管已经有许多直接使用Java NIO API的应用程序被构建了，但是要做到如此正确和安全并不容易。
     *   特别是，在高负载下可靠和高效地处理和调度I/O操作是一项繁琐而且容易出错的任务，最好留给高性能的网络编程专家——Netty。
     */
    public void serve(int portNumber) throws IOException {
        // 创建一个新的ServerSocket，用以监听指定端口上的连接请求
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // ServerSocket上的accept()方法将会一直阻塞到一个连接建立①,随后返回一个新的Socket用于客户端和服务器之间的通信。该ServerSodcket将继续监听传入的连接。
        Socket clientSocket = serverSocket.accept(); // ①
        // 这些流对象都派生于该套接字的流对象: BufferedReader和PrintWriter都衍生自Socket的输入输出流②。前者从一个字符输入流中读取文本, 后者打印对象的格式化的表示到文本输出流统。
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // ②
        String request, response;
        // 处理循环开始
        while ((request = in.readLine()) != null) { // ③ readLine()方法将会阻塞,直到在③处一个由换行符或者回车符结尾的字符串被读取。
            // 如果客户端发送了“Done”，则退出处理循环
            if ("Done".equals(request)) {
                break;
            }
            // 请求被传递给服务器的处理方法
            response = processRequest(request); // ④
            // 服务器的响应被发送给了客户端
            out.println(response);
        } //  继续执行处理循环
    }

    private String processRequest(String request) {
        return "Processed";
    }
}
