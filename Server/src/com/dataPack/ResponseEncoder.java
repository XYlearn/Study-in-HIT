package NetEvent.dataPack;

import com.ServerResponseMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Created by xy16 on 17-2-24.
 */
public class ResponseEncoder implements ProtocolEncoder {
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
		ServerResponseMessage.Message serverResponseMessage = (ServerResponseMessage.Message) message;
		byte[] sendByte = serverResponseMessage.toByteArray();
		int length = Integer.BYTES+sendByte.length;
		IoBuffer buffer = IoBuffer.allocate(length);
		buffer.putInt(length);
		buffer.put(sendByte);
		buffer.flip();
		out.write(buffer);
	}

	@Override
	public void dispose(IoSession session) throws Exception {

	}
}
