package NetEvent.eventcom;

import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class RegisterEvent extends NetEvent{
	private final boolean status;
	private final String information;

	public RegisterEvent(boolean status, String information) {
		super(EventType.REGISTER_EVENT);
		this.status = status;
		this.information = information;
	}

	public RegisterEvent(ServerResponseMessage.RegisterResponse registerResponse) {
		super(EventType.REGISTER_EVENT);

		this.status = registerResponse.getSuccess();
		this.information = registerResponse.getInformation();
	}

	public boolean getStatus() {return this.status;}

	public String getInformation() {return this.information;}
}
