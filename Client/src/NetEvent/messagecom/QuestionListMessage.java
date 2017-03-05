package NetEvent.messagecom;

import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class QuestionListMessage {
	private final long questionID;
	private final String questionDescription;
	private final int good;
	private final int userNum;
	private final String time;
	private final String owner;

	public QuestionListMessage(ServerResponseMessage.QuestionListMessage questionListMessage) {
		this.questionID = questionListMessage.getQuestionID();
		this.questionDescription = questionListMessage.getQuestionDescription();
		this.good = questionListMessage.getGood();
		this.userNum = questionListMessage.getUserNum();
		this.time = questionListMessage.getTime();
		this.owner = questionListMessage.getOwner();
	}

	public long getQuestionID() {return this.questionID;}

	public String getQuestionDescription() {return this.questionDescription;}

	public int getGood() {return this.good;}

	public int getUserNum() {return this.userNum;}

	public String getTime() {return this.time;}

	public String getOwner() {return this.owner;}
}
