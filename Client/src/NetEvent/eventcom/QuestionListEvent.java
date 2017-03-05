package NetEvent.eventcom;

import NetEvent.messagecom.QuestionListMessage;
import com.ServerResponseMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy16 on 17-3-5.
 */
public class QuestionListEvent extends NetEvent {
	private final List<QuestionListMessage> questionListMessage;
	private final int listNum;

	public QuestionListEvent(ServerResponseMessage.GetQuestionListResponse getQuestionListResponse) {
		super(EventType.QUESTION_LIST_EVENT);
		this.listNum = getQuestionListResponse.getNum();
		this.questionListMessage = new ArrayList<>();

		for (ServerResponseMessage.QuestionListMessage qlm :
				  getQuestionListResponse.getQuestionListMessageList()) {
			questionListMessage.add(new QuestionListMessage(qlm));
		}
	}

	public List<QuestionListMessage> getQuestionListMessage() {return this.questionListMessage;}

	public int getListNum() {return this.listNum;}
}
