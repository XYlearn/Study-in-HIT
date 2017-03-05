package NetEvent.eventcom;

import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class SolvedQuestionEvent extends NetEvent {
	private final boolean success;
	private final long questionID;

	public SolvedQuestionEvent(ServerResponseMessage.SolvedQuestionResponse response) {
		super(EventType.SOLVED_QUESTION_EVENT);

		this.success = response.getSuccess();
		this.questionID = response.getQuestionID();
	}

	public boolean isSuccess() {return this.success;}

	public long getQuestionID() {return this.questionID;}
}
