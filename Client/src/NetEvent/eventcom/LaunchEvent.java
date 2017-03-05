package NetEvent.eventcom;

import NetEvent.messagecom.UserMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class LaunchEvent extends NetEvent{
	private final boolean success;
	private final String information;
	private final UserMessage userMessage;

	public LaunchEvent(boolean success, String information, UserMessage userMessage) {
		super(EventType.LAUNCH_EVENT);
		this.success = success;
		this.information = information;
		this.userMessage = userMessage;
	}

	public boolean isStatus() {return this.success;}

	public String getInformation() {return this.information;}

	public UserMessage getUserMessage() {return this.userMessage;}
}
