package NetEvent.eventcom;

import NetEvent.messagecom.QuestionMessage;
import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class CreateQuestionEvent extends NetEvent {
	private final boolean success;
	private final QuestionMessage questionMessage;

	public CreateQuestionEvent(ServerResponseMessage.CreateQuestionResponse createQuestionResponse) {
		super(EventType.CREATE_QUESTION_EVENT);

		this.success = createQuestionResponse.getSuccess();
		this.questionMessage = new QuestionMessage(createQuestionResponse.getQuestionMessage());
	}

	public boolean isSuccess() {return this.success;}

	public QuestionMessage getQuestionMessage() {return this.questionMessage;}
}
