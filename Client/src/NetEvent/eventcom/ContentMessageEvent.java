package NetEvent.eventcom;

import NetEvent.messagecom.Record;
import com.ServerResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xy16 on 17-3-5.
 */
public class ContentMessageEvent extends NetEvent {
	private final long questionID;
	private final boolean success;
	private final boolean isMyself;
	private final Record record;

	public ContentMessageEvent(ServerResponseMessage.SendContent content) {
		super(EventType.CONTENT_MESSAGE_EVENT);
		this.questionID = content.getQuestionID();
		this.record = new Record(
				  content.getUser(),
				  content.getContent(),
				  content.getTime(),
				  content.getRecordID(),
				  content.getPicturesList(),
				  content.getMarkMapMap()
		);
		this.success = content.getSuccess();
		this.isMyself = content.getIsmyself();
	}

	public long getQuestionID() {return this.questionID;}

	public Record getRecord() {return this.record;}

	public boolean isSuccess() {return this.success;}

	public boolean isMyself() {return this.isMyself;}

}
