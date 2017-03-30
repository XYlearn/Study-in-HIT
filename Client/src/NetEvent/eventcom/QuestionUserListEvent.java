package NetEvent.eventcom;

import com.ServerResponseMessage;

import java.util.Map;

/**
 * Created by xy16 on 17-3-30.
 */
public class QuestionUserListEvent extends NetEvent {
	boolean success;
	Long questionID;
	Map<String, String> userAndPictureMap;

	public QuestionUserListEvent(ServerResponseMessage.GetUserListResponse getUserListResponse) {
		super(EventType.QUESTION_USER_LIST_EVENT);

		this.success = getUserListResponse.getSuccess();
		this.userAndPictureMap = getUserListResponse.getUsersMap();
		this.questionID = getUserListResponse.getQuestionID();
	}

	public boolean isSuccess() {return this.success;}

	public Map<String, String> getUserAndPictureMap() {return this.userAndPictureMap;}

	public Long getQuestionID() {return this.questionID;}
}
