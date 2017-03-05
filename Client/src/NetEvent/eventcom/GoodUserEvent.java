package NetEvent.eventcom;

import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class GoodUserEvent extends NetEvent{
	private final boolean success;

	public GoodUserEvent(boolean success) {
		super(NetEvent.EventType.GOOD_USER_EVENT);
		this.success = success;
	}

	public GoodUserEvent(ServerResponseMessage.GoodUserResponse goodUserResponse) {
		super(NetEvent.EventType.GOOD_USER_EVENT);
		this.success = goodUserResponse.getSuccess();
	}

	public boolean isSuccess() {return this.success;}
}
