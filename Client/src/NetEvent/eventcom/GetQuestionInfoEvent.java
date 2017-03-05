package NetEvent.eventcom;

import NetEvent.messagecom.QuestionMessage;
import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class GetQuestionInfoEvent extends NetEvent {
	private final QuestionMessage questionMessage;

	public GetQuestionInfoEvent(ServerResponseMessage.QuestionInformationResponse questionInformationResponse) {
		super(EventType.QUESTION_INFO_EVENT);
		this.questionMessage = new QuestionMessage(questionInformationResponse.getQuestionMessage());
	}

	public QuestionMessage getQuestionMessage() {return this.questionMessage;}
}
