package NetEvent;

import java.util.ArrayList;

public class Record
	{
		private final String user; //发送者
		private final String content; //内容
		private final String time; //发送时间
		private final ArrayList<String> pictures; //图片列表
		
		/**
		 *
		 * @param user 用户名
		 * @param content 发送内容
		 * @param time 发送时间
		 * @param pictures 图片列表
		 */
		public Record(String user,String content,String time,ArrayList<String> pictures)
		{
			this.user=user;
			this.content=content;
			this.time=time;
			this.pictures=pictures;
		}
		
		public Record(String user,String content,String time)
		{
			this.user=user;
			this.content=content;
			this.time=time;
			this.pictures=new ArrayList<>();
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
		
		public ArrayList<String> getPictures()
		{
			return pictures;
		}
		
		public String getPictureAt(int index)
		{
			return pictures.get(index);
		}
		public void addPicture(String picture)
		{
			pictures.add(picture);
		}
	}