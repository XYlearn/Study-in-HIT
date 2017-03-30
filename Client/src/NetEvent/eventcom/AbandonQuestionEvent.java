package NetEvent.eventcom;

import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class AbandonQuestionEvent extends NetEvent {
	private final boolean success;

	public AbandonQuestionEvent(ServerResponseMessage.AbandonQuestionResponse abandonQuestionResponse) {
		super(EventType.ABANDON_QUESTION_EVENT);

		this.success = abandonQuestionResponse.getSuccess();
	}

	public boolean isSuccess() {return this.success;}

}
