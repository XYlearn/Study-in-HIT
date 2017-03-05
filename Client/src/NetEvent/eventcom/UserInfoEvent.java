package NetEvent.eventcom;

import NetEvent.messagecom.UserMessage;
import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class UserInfoEvent extends NetEvent {
	private final boolean exist;
	private final UserMessage userMessage;

	public UserInfoEvent(ServerResponseMessage.UserInformationResponse userInformationResponse) {
		super(EventType.USER_INFO_EVENT);

		this.exist = userInformationResponse.getExist();
		this.userMessage = new UserMessage(userInformationResponse.getUserMessage());
	}

	public boolean isExist() {return this.exist;}

	public UserMessage getUserMessage() {return this.userMessage;}
}
