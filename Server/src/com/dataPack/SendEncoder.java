package NetEvent.dataPack;

import com.ClientSendMessage;
import com.ServerResponseMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Created by xy16 on 17-2-24.
 */
public class SendEncoder implements ProtocolEncoder {
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
		ClientSendMessage.Message clientSendMessage = (ClientSendMessage.Message) message;
		byte[] sendByte = clientSendMessage.toByteArray();
		//length of the data and integer
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
