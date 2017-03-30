package NetEvent.eventcom;

import com.ServerResponseMessage;

import java.util.Map;

/**
 * Created by xy16 on 17-3-30.
 */
public class AcquaintanceUserListEvent extends NetEvent {
	boolean success;
	Map<String, String> userAndPictureMap;

	public AcquaintanceUserListEvent(ServerResponseMessage.GetUserListResponse getUserListResponse) {
		super(EventType.ACQUAINTANCE_USER_LIST_EVENT);

		this.success = getUserListResponse.getSuccess();
		this.userAndPictureMap = getUserListResponse.getUsersMap();
	}

	public boolean isSuccess() {return this.success;}

	public Map<String, String> getUserAndPictureMap() {return this.userAndPictureMap; }
}
