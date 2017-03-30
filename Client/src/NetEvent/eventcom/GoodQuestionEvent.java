package NetEvent.eventcom;

import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class GoodQuestionEvent extends NetEvent {
	private final boolean success;

	public GoodQuestionEvent(boolean success) {
		super(EventType.GOOD_QUESTION_EVENT);
		this.success = success;
	}

	public GoodQuestionEvent(ServerResponseMessage.GoodQuestionResponse goodQuestionResponse) {
		super(EventType.GOOD_QUESTION_EVENT);
		this.success = goodQuestionResponse.getSuccess();
	}

	public boolean isSuccess() {return this.success;}
}
