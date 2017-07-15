package com;

import com.config.Config;
import com.dataPack.NetPackageCodeFacotry;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by xy16 on 17-2-24.
 */
public class Server {
	private static int port = Config.ServerConfig.getPort();
	public static DatabaseConnection databaseConnection = new DatabaseConnection();
	public static Panel tempPanel = new Panel();
	static {
		tempPanel.setSize(1600, 900);
	}
	private static HeartBeatHandler heartBeatHandler = new HeartBeatHandler();

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
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);

		//bind the port
		acceptor.bind(new InetSocketAddress(port));

		heartBeatHandler.start();

		System.out.println("Server start...");
	}

}
