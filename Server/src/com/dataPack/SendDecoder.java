package com.dataPack;

import com.ClientSendMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Created by xy16 on 17-2-24.
 */
public class SendDecoder extends CumulativeProtocolDecoder {
	@Override
	public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if(in.remaining() < Integer.BYTES) {
			return false;
		} else {
			//get the length of data
			int len = in.getInt() - Integer.BYTES;

			if(in.remaining() < len) {
				return false;
			} else {
				byte[] messageByte = new byte[len];
				in.get(messageByte);
				try {
					ClientSendMessage.Message request = ClientSendMessage.Message.parseFrom(messageByte);
					out.write(request);
					return true;
				} catch (InvalidProtocolBufferException e) {
					return false;
				}
			}
		}
	}

}
