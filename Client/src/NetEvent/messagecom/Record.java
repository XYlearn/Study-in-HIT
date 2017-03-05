package NetEvent.messagecom;

import NetEvent.eventcom.NetEvent;
import com.ServerResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Record
	{
		private final String user; //发送者
		private final String content; //内容
		private final String time; //发送时间
		private final long recordID;
		private final List<String> recordpic; //图片列表
		private final Map<Integer, Long> markMap;
		
		/**
		 *
		 * @param user 用户名
		 * @param content 发送内容
		 * @param time 发送时间
		 * @param recordpic 图片列表
		 */
		public Record(String user, String content, String time,
						  long recordID, List<String> recordpic, Map<Integer, Long> markMap)
		{
			this.user=user;
			this.content=content;
			this.time=time;
			this.recordID = recordID;
			this.recordpic = recordpic;
			this.markMap = markMap;
		}

		public Record(ServerResponseMessage.Record record) {
			this.user = record.getUser();
			this.content = record.getContentMessage();
			this.time = record.getTime();
			this.recordID = record.getRecordID();
			this.recordpic = new ArrayList<>();
			for(String pic : record.getRecordpicList()) {
				recordpic.add(pic);
			}
			this.markMap = record.getMarkMapMap();
		}
		
		public String getUser()
		{
			return user;
		}
		
		public String getContent()
		{
			return content;
		}
		
		public String getTime()
		{
			return time;
		}
		
		public List<String> getPictures()
		{
			return recordpic;
		}
		
		public String getPictureAt(int index)
		{
			return recordpic.get(index);
		}
		public void addPicture(String picture)
		{
			recordpic.add(picture);
		}
	}