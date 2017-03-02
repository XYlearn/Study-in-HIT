package com;

import com.dataPack.NetPackageCodeFacotry;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by xy16 on 17-2-24.
 */
public class Server {
	private static int port = 8972;
	public static DatabaseConnection databaseConnection = new DatabaseConnection();

	public static void main(String[] args) throws IOException {
		IoAcceptor acceptor = new NioSocketAcceptor();

		//set package code factory
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				  new NetPackageCodeFacotry(false)
		));

		//set handler
		acceptor.setHandler(new ServerHandler());

		//set default buffersize
		acceptor.getSessionConfig().setReadBufferSize(102400);

		//set idle time
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 20);

		//bind the port
		acceptor.bind(new InetSocketAddress(port));

		System.out.println("Server start...");
	}

}