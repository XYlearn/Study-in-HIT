package com.dataPack;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by xy16 on 17-2-24.
 */
public class NetPackageCodeFacotry implements ProtocolCodecFactory {
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public NetPackageCodeFacotry(boolean client) {
		if(client) {
			encoder = new SendEncoder();
			decoder = new ResponseDecoder();
		} else {
			encoder = new ResponseEncoder();
			decoder = new SendDecoder();
		}
	}

	public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {

		return encoder;

	}

	public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {

		return decoder;

	}
}
