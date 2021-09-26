package top.caolizhi.example.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class SocketNio {

	public static void main(String[] args) throws IOException, InterruptedException {

		List<SocketChannel> clientList = new LinkedList<>();
		final ServerSocketChannel socketChannel = ServerSocketChannel.open();
		socketChannel.bind(new InetSocketAddress(9999));
		socketChannel.configureBlocking(false); // 不设置，就是阻塞，调用 accept()

		System.out.println("server started ..." + socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getLocalPort());

		while (true) {
			Thread.sleep(1000);
			final SocketChannel client = socketChannel.accept();

			if (null != client) {
				client.configureBlocking(false);  //  配置非阻塞，否则一直等待客户端的数据
				System.out.println("client :" + client.socket().getInetAddress() + ":" + client.socket().getPort());
				clientList.add(client);
			}else {
				System.out.println("waiting for connection ....");
			}

			final ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

			// 遍历客户端，读写数据
			for (SocketChannel channel : clientList) {
				System.out.println("read data from client " + channel.socket().getInetAddress() + ":" + channel.socket().getPort());
				final int byteNum = channel.read(byteBuffer);
				if (byteNum > 0) {
					byteBuffer.flip(); // 翻转，由写转成读
					final byte[] readBytes = new byte[byteBuffer.limit()];
					byteBuffer.get(readBytes); // 把 buffer 里面的数据 copy 到 readBytes 数组
					final String data = new String(readBytes);
					System.out.println(channel.socket().getInetAddress() + ":" + channel.socket().getPort() + "'s data :" + data) ;
				}
			}
		}
	}

}
