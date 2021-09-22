package top.caolizhi.example.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketBio {

	public static void main(String[] args) throws IOException {
		final ServerSocket server = new ServerSocket(9999);

		System.out.println("开启服务端：" + server.getInetAddress() + ":" + server.getLocalPort());

		while (true) {
			final Socket client = server.accept();
			System.out.println("client:\t" + client.getInetAddress() + ":" + client.getPort());

			new Thread(() -> {
				try {
					final InputStream in = client.getInputStream();
					final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					while (true) {
						final String line = reader.readLine();
						if (null != line) {
							System.out.println(line);
						} else {
							client.close();
							break;
						}
					}
					System.out.println("客户端断开！");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		}

	}

}
