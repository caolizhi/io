package top.caolizhi.example.io.multiplexing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SocketMultiplexing {

	public static void main(String[] args) throws IOException {
		// 开启一个服务端
		final ServerSocketChannel server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.bind(new InetSocketAddress(9999));

		// Selector 类是多路复用的 java 实现
		final Selector selector = Selector.open(); // 相当于调用了 epoll_create

		/**
		 * 如果是 epoll 模型：相当于调用了 epoll_ctl，监听 EPOLLIN 事件
		 * 如果是 select/poll 模型：会在 jvm 里面开辟一个数组，把 fd 放进去。
		 */
		server.register(selector, SelectionKey.OP_ACCEPT);

		System.out.println("server start: " + server.socket().getInetAddress() + ":" + server.getLocalAddress());

		while (true) {
			final Set<SelectionKey> selectionKeys = selector.keys();
			System.out.println("total checking fd size: " + selectionKeys.size());


			if (selector.select() > 0 ) { // select() 方法就是拿到有 IO 状态变化的 fd 数量
				final Set<SelectionKey> selectedKeys = selector.selectedKeys(); // 拿到 IO 状态变化的 fd 集合
				final Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while (keyIterator.hasNext()) {
					final SelectionKey key = keyIterator.next(); // 拿到 fd
					keyIterator.remove(); // 移除掉，不然一直在循环处理
					// 接下来，对不同的 IO 事件进行处理，是建立连接还是读数据还是写数据？
					if (key.isAcceptable()) {

						final ServerSocketChannel channel = (ServerSocketChannel)key.channel(); // 拿到的是 ServerSocketChannel，服务端
						final SocketChannel client = channel.accept(); // accept 之后会拿到一个新的 fd
						client.configureBlocking(false);

						System.out.println("client: " + client.socket().getInetAddress() + ":" + client.getLocalAddress());

						ByteBuffer buffer = ByteBuffer.allocate(4096);

						//需要把上面调用 accept 产生的新的 fd 也要放到监听的列表里面去，并且监听的时间是 READ，绑定一个 buffer 到这个 fd 上。
						client.register(selector, SelectionKey.OP_READ, buffer);

					} else if (key.isReadable()) {

						final SocketChannel client = (SocketChannel) key.channel(); // 拿到的是 SocketChannel 对象，客户端
						// 拿到客户端传过来的数据，一个 buffer，因为上面 register 的时候，绑定了一个 buffer 在这个 fd 上。
						final ByteBuffer buffer  = (ByteBuffer) key.attachment();
						buffer.clear();
						while (true) {
							final int read = client.read(buffer);
							if (read > 0) { // 有数据
								buffer.flip(); // 翻转，由读变成写
								while (buffer.hasRemaining()) {
									client.write(buffer); // 写回 client
								}
							} else if (read == 0) { // 没有数据
								break;
							} else {
								client.close();
								break;
							}
						}
					}
				}
			}
		}
	}

}
