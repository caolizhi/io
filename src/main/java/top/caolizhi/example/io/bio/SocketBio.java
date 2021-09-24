package top.caolizhi.example.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketBio {

	public static void main(String[] args) throws IOException {
		// 打开 socket server 服务
		final ServerSocket server = new ServerSocket(9999);
		System.out.println("start server：" + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort());

		// 这里 while 是模拟 BIO 模型下一直在等待连接
		while (true) {
			System.out.println(Thread.currentThread().getName() + ": 服务器正在等待连接...");
			final Socket client = server.accept(); // 阻塞住

			System.out.println("client: " + client.getInetAddress() + ":" + client.getPort());

			// 当客户端连接成功以后，开启一个线程来处理
			new Thread(() -> {
				try {

					// 拿到字节流，socket 通信
					final InputStream in = client.getInputStream();
					// 字节流转成字符流
					final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					System.out.println(Thread.currentThread().getName() + ": 服务器正在等待数据...");
					// 一直循环等待消息
					while (true) {
						final String line = reader.readLine();
						if (null != line) {
							System.out.println(Thread.currentThread().getName() + ": 服务器已经接收到数据：" + line);
						} else {
							client.close();
							break;
						}
					}
					System.out.println(Thread.currentThread().getName() + ": 客户端 --> " + client.getInetAddress().getHostAddress() + "断开！");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		}

	}
}
