package NetEvent.eventcom;

import NetEvent.messagecom.QuestionMessage;
import com.ServerResponseMessage;
import sun.nio.ch.Net;

/**
 * Created by xy16 on 17-3-5.
 */
public class EnterQuestionEvent extends NetEvent {
	private final boolean success;
	private final QuestionMessage questionMessage;

	public EnterQuestionEvent(ServerResponseMessage.QuestionEnterResponse enterResponse) {
		super(EventType.ENTER_QUESTION_EVENT);
		this.success = enterResponse.getAllow();
		this.questionMessage = new QuestionMessage(enterResponse.getQuestionMessage());
	}

	public boolean isSuccess() {return success;}

	public QuestionMessage getQuestionMessage() {return questionMessage;}
}
