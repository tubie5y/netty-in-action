package nia.chapter4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Listing 4.1 Blocking networking without Netty
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class PlainOioServer {
    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port); // 将服务器绑定到指定端口
        try {
            for(;;) {
                final Socket clientSocket = socket.accept(); // 接受连接
                System.out.println("Accepted connection from " + clientSocket);
                new Thread(new Runnable() { // 创建一个新的线程来处理该连接
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8"))); // 将消息写给已连接的客户端
                            out.flush();
                            clientSocket.close(); // 关闭连接
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                // ignore on close
                            }
                        }
                    }
                }).start(); // 启动线程
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
