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
     */
    public void serve(int portNumber) throws IOException {
        // 创建一个新的ServerSocket，用以监听指定端口上的连接请求
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // 对accept()方法的调用将被阻塞，直到一个连接建立, 随后返回一个新的Socket用于客户端和服务器之间的通信。该ServerSocket将继续监听传入的连接。
        Socket clientSocket = serverSocket.accept();
        // 这些流对象都派生于该套接字的流对象: BufferedReader和PrintWriter都衍生自Socket的输入输出流。前者从一个字符输入流中读取文本，后者打印对象的格式化的表示到文本输出流。
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String request, response;
        // 处理循环开始
        while ((request = in.readLine()) != null) { // readLine()方法将会阻塞，直到在❸处一个由换行符或者回车符结尾的字符串被读取。
            // 如果客户端发送了“Done”，则退出处理循环
            if ("Done".equals(request)) {
                break;
            }
            // 请求被传递给服务器的处理方法
            response = processRequest(request);
            // 服务器的响应被发送给了客户端
            out.println(response);
        }
    }

    private String processRequest(String request) {
        return "Processed";
    }
}
