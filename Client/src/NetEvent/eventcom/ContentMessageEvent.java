package NetEvent.eventcom;

import com.ServerResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xy16 on 17-3-5.
 */
public class ContentMessageEvent extends NetEvent {
	private final long questionID;
	private final String content;
	private final String time;
	private final String user;
	private final List<String> pictures;
	private final boolean success;
	private final boolean isMyself;
	private final 	Map<Integer, Long> markMap;

	public ContentMessageEvent(ServerResponseMessage.SendContent content) {
		super(EventType.CONTENT_MESSAGE_EVENT);
		this.questionID = content.getQuestionID();
		this.content = content.getContent();
		this.time = content.getTime();
		this.user = content.getUser();
		this.pictures = new ArrayList<>();
		this.success = content.getSuccess();
		this.isMyself = content.getIsmyself();
		for(String pic : content.getPicturesList()) {
			pictures.add(pic);
		}
		this.markMap = content.getMarkMapMap();
	}

	public long getQuestionID() {return this.questionID;}

	public String getContent() {return this.content;}

	public String getTime() {return this.time;}

	public String getUser() {return this.user;}

	public List<String> getPictures() {return this.pictures;}

	public boolean isSuccess() {return this.success;}

	public boolean isMyself() {return this.isMyself;}

	public Map<Integer, Long> getMarkMap() {return this.markMap;}
}
