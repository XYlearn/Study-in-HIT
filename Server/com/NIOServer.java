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

	private static ByteBuffer rbuffer = ByteBuffer.allocate(10240);

	private Map<SocketChannel, ServerItem> clientsMap = new HashMap<>();

	private static Selector selector;

	private DatabaseConnection dbconn;
	boolean isOver;

	public static Map<String, ArrayList<SocketChannel>> question_socket_list = new HashMap<>();
	public static Map <SocketChannel, ArrayList<String>> socket_question_list = new HashMap<>();
	public static Map<String, SocketChannel> user_socket_list = new HashMap<>();

	//与接受包有关变量
	private int count = 0;
	private int bodyLen = -1;

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

	private synchronized void handle(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel server = null;
		SocketChannel client = null;
		ClientSendMessage.Message message = null;

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
			ServerResponseMessage.Message response = null;

			//读取数据
			ByteBuffer tempBuffer = ByteBuffer.allocate(10240);
			count = client.read(tempBuffer);
			tempBuffer.flip();
			if (count > 0) {
				rbuffer.put(tempBuffer.slice());
				rbuffer.flip();
				int remain = rbuffer.remaining();
				while (remain > 0) {
					if(bodyLen <= 0) {
						//包头可读
						if (Integer.BYTES <= remain) {
							bodyLen = rbuffer.getInt();
							remain-=Integer.BYTES;
							continue;
						}
						//包头残缺
						else {
							ByteBuffer head = rbuffer.slice();
							rbuffer.clear();
							rbuffer.put(head);
							remain+=Integer.BYTES;
							return;
						}
					}
					//包头已读
					else if(bodyLen > 0) {
						//包体完整
						if(remain >= bodyLen) {
							byte[] readByte = new byte[bodyLen];
							for (int i = 0; i < bodyLen; i++) {
								readByte[i] = rbuffer.get();
							}
							message = ClientSendMessage.Message.parseFrom(readByte);
							ServerItem serverItem = clientsMap.get(client);
							response = serverItem.handleMessage(message);
							remain-=bodyLen;
							bodyLen = -1;

							//回复
							byte[] responseByte = response.toByteArray();
							ByteBuffer responseBB = ByteBuffer.allocate(responseByte.length+Integer.BYTES);
							responseBB.putInt(responseByte.length).put(responseByte);
							responseBB.flip();

							while(responseBB.hasRemaining())
								client.write(responseBB);

							//检测rbuffer是否读完，读完则重置rbuffer
							if(remain == 0) {
								rbuffer.clear();
								break;
							}

						}
						//包体残缺
						else {
							ByteBuffer bodyLeft = rbuffer.slice();
							rbuffer.clear();
							rbuffer.putInt(bodyLen);
							rbuffer.put(bodyLeft);
							return;
						}
					}
				}
			} else {
				System.out.println("Terminated:"+client.socket());
				clientsMap.remove(client);
				ArrayList<String> questions = socket_question_list.get(client);
				if(null != questions) {
					for (String question : questions) {
						question_socket_list.get(question).remove(client);
					}
				}
				client.socket().close();
				client.close();
				return;
			}
		}
	}
}
