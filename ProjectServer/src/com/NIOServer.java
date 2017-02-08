package com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by xy16 on 17-1-31.
 */
public class NIOServer {
	private static int port = 6666;

	private static ByteBuffer rbuffer = ByteBuffer.allocate(1024);

	private Map<SocketChannel, ServerItem> clientsMap = new HashMap<>();

	private static Selector selector;

	private DatabaseConnection dbconn;
	boolean isOver;

	public static Map<String, ArrayList<SocketChannel>> question_socket_list = new HashMap<>();
	public static Map<String, SocketChannel> user_socket_list = new HashMap<>();

	public static void main(String[] args) {
		new NIOServer().listen();
	}

	public NIOServer() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() throws IOException {
		dbconn = new DatabaseConnection();

		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(port));
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("server start on port:" + port);
	}

	private void listen() {
		isOver = false;
		while (! isOver) {
			try {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> ite = selectionKeys.iterator();
				while (ite.hasNext()) {
					handle(ite.next());
					ite.remove();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handle(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel server = null;
		SocketChannel client = null;
		ClientSendMessage.Message message = null;
		int count = 0;

		if (selectionKey.isAcceptable()) {
			//接受客户端连接
			System.out.println("Accept:");
			server = (ServerSocketChannel)selectionKey.channel();
			client = server.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);

			ServerItem serverItem = new ServerItem(client, dbconn);
			clientsMap.put(client, serverItem);

			System.out.println("Accept:"+client.socket());
		} else if (selectionKey.isReadable()) {
			client = (SocketChannel) selectionKey.channel();
			ServerResponseMessage.Message response;

			//读取数据
			rbuffer.clear();
			count = client.read(rbuffer);
			if (count > 0) {
				byte[] readByte = new byte[count];
				byte[] bytes = rbuffer.array();
				for(int i=0;i<count;i++) {
					readByte[i] = bytes[i];
				}
				message = ClientSendMessage.Message.parseFrom(ByteBuffer.wrap(readByte).array());
				ServerItem serverItem = clientsMap.get(client);
				response = serverItem.handleMessage(message);
			} else if(count==-1){
				System.out.println("Terminated:"+client.socket());
				client.close();
				return;
			} else {
				response = ServerResponseMessage.Message.newBuilder().build();
			}
			//回复
			client.write(ByteBuffer.wrap(response.toByteArray()));
		}
	}
}
