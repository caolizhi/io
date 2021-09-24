package top.caolizhi.example.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.google.common.collect.Lists;

public class SocketNio {

	public static void main(String[] args) throws IOException {

		List<SocketChannel> clientList = Lists.newLinkedList();
		final ServerSocketChannel socketChannel = ServerSocketChannel.open();
		socketChannel.bind(new InetSocketAddress(9999));
		socketChannel.configureBlocking(false);

		while (true) {
			final SocketChannel client = socketChannel.accept();

			if (null != client) {
				client.configureBlocking(false);  //  配置非阻塞
				System.out.println("client :" + client.socket().getInetAddress() + ":" + client.socket().getPort());
				clientList.add(client);
			}else {
				System.out.println("等待客户端连接....");
			}



		}


	}

}
