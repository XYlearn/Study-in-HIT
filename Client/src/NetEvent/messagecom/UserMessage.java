package NetEvent.messagecom;

import com.ServerResponseMessage;

/**
 * Created by xy16 on 17-3-5.
 */
public class UserMessage {
	private final String username;
	private final int good;
	private final int questionNum;
	private final int solvedQuestionNum;
	private final int bonus;
	private final String signature;
	private final String mail_address;
	private final String pic_url;

	public UserMessage(String username, int good, int questionNum, int solvedQuestionNum,
					int bonus, String signature, String mail_address, String pic_url) {
		this.username = username;
		this.good = good;
		this.questionNum = questionNum;
		this.solvedQuestionNum = solvedQuestionNum;
		this.bonus = bonus;
		this.signature = signature;
		this.mail_address = mail_address;
		this.pic_url = pic_url;
	}

	public UserMessage(ServerResponseMessage.UserMessage userMessage) {
		this.username = userMessage.getUsername();
		this.good = userMessage.getGood();
		this.questionNum = userMessage.getQuestionNum();
		this.solvedQuestionNum = userMessage.getSolvedQuestionNum();
		this.bonus = userMessage.getBonus();
		this.signature = userMessage.getSignature();
		this.mail_address = userMessage.getMailAddress();
		this.pic_url = userMessage.getPicUrl();
	}

	public String getUsername() {return this.username;}

	public int getGood() {return this.good;}

	public int getQuestionNum() {return this.questionNum;}

	public int getSolvedQuestionNum() {return this.solvedQuestionNum;}

	public int getBonus() {return this.bonus; }

	public String getSignature() {return this.signature;}

	public String getMailAddress() {return this.mail_address;}

	public String getPicUrl() {return this.pic_url;}
}
