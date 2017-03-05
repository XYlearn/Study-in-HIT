package NetEvent.eventcom;

import NetEvent.messagecom.QuestionListMessage;
import com.ServerResponseMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy16 on 17-3-5.
 */
public class SearchQuestionEvent extends NetEvent {
	private final List<QuestionListMessage> questionList;

	public SearchQuestionEvent(ServerResponseMessage.SearchInformationResponse response) {
		super(EventType.SEARCH_QUESTION_EVENT);

		this.questionList = new ArrayList<>();
		for (ServerResponseMessage.QuestionListMessage qlm : response.getQuestionListMessageList()) {
			questionList.add(new QuestionListMessage(qlm));
		}
	}

	public List<QuestionListMessage> getQuestionList() {return this.questionList;}
}
