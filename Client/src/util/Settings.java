package util;

import bin.test;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kqwyf
 */
public class Settings
{
	private static final String SETTINGS_FILE=test.MAINPATH+"Settings.ini";
	private static final String COMMENT="Settings of StudyInHIT";
	private static InputStream inStream;
	private static final Properties propertiesTable=new Properties();
	
	/**
	 * Get property value.
	 * @param key the key
	 * @return value of the corresponding property
	 */
	public static String getProperty(String key)
	{
		return propertiesTable.getProperty(key);
	}
	
	/**
	 * Set and save property.
	 * @param key the key
	 * @param value new value for this property
	 */
	public static void setProperty(String key,String value)
	{
		propertiesTable.setProperty(key, value);
		saveFile();
	}
	
	//key and default value are splited by '@'
	public enum Global
	{
		USERNAME("GlobalUsername@"),
		PASSWORD("GlobalPassword@");
		
		private final String KEY;
		private final String DEFAULT;
		Global(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			if(tmp.length>1) DEFAULT=tmp[1];
			else DEFAULT="";
		}

		public String getKey()
		{
			return KEY;
		}

		public String getDefault()
		{
			return DEFAULT;
		}
	}
	
	public enum Client
	{
		TIME_OUT("ClientTimeOut@3000"),
		SERVER_HOST("ServerHost@123.207.159.156"),
		SERVER_PORT("ServerPort@8972"),
		READ_BUFFER_SIZE("ReadBufferSize@10240"),
		SEND_BUFFER_SIZE("SendBufferSize@10240"),
		RECEIVE_BUFFER_SIZE("ReceiveBufferSize@10240"),
		IDLE_TIME("IdleTime@20"),
		WRITE_TIME_OUT("WriteTimeOut@1000");
		
		private final String KEY;
		private final String DEFAULT;
		Client(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			if(tmp.length>1) DEFAULT=tmp[1];
			else DEFAULT="";
		}

		public String getKey()
		{
			return KEY;
		}

		public String getDefault()
		{
			return DEFAULT;
		}
	}
	
	public enum ListBox
	{
		QUESTION_COUNT("ListBoxQuestionCount@20"),
		SORT_BY("ListBoxSortBy@");
		
		private final String KEY;
		private final String DEFAULT;
		ListBox(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			if(tmp.length>1) DEFAULT=tmp[1];
			else DEFAULT="";
		}

		public String getKey()
		{
			return KEY;
		}

		public String getDefault()
		{
			return DEFAULT;
		}
	}
	
	public enum ChattingBox
	{
		HISTORY_RECORD_COUNT("ChattingBoxHistoryRecordCount@-1"),
		HTML_FILE_PATH("ChattingBoxHtmlFilePath@gui/cb.html"),
		MESSAGE_TYPE_PLAIN("ChattingBoxMessageTypePlain@plain"),
		MESSAGE_TYPE_AUDIO("ChattingBoxMessageTypeAudio@audio"),
		MESSAGE_TYPE_FILE("ChattingBoxMessageTypeFile@file"),
		MESSAGE_TYPE_ANNOUNCEMENT("ChattingBoxMessageTypeAnnouncement@anno"),
		TAG_SPLIT("ChattingBoxTagSplit@<!--SPLIT-->"),
		TAG_TIME("ChattingBoxTagTime@<!--TIME-->"),
		TAG_ANNOUNCEMENT("ChattingBoxTagAnnouncement@<!--ANNOUNCEMENT-->"),
		TAG_RECORD_ID("ChattingBoxTagRecordId@<!--RECORDID-->"),
		TAG_DIRECTION("ChattingBoxTagDirection@<!--DIRECTION-->"),
		TAG_LEFT_HEAD("ChattingBoxTagLeftHead@<!--LEFTHEAD-->"),
		TAG_IMG_PATH("ChattingBoxTagImgPath@<!--IMGPATH-->"),
		TAG_RIGHT_HEAD("ChattingBoxTagRightHead@<!--RIGHTHEAD-->"),
		TAG_MESSAGE("ChattingBoxTagMessage@<!--MESSAGE-->"),
		TAG_PICT_PATH("ChattingBoxTagPictPath@<!--PICTPATH-->"),
		TAG_PICTURE_AT_I("ChattingBoxTagPictureAtI@<!--PICTUREATi-->"),
		TAG_I("ChattingBoxTagI@<!--i-->");
		
		private static String defaultHtml=DEFAULT_HTML_FOR_CHATTING_BOX;
		
		private final String KEY;
		private final String DEFAULT;
		ChattingBox(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			if(tmp.length>1) DEFAULT=tmp[1];
			else DEFAULT="";
		}

		public String getKey()
		{
			return KEY;
		}

		public String getDefault()
		{
			return DEFAULT;
		}
		
		public static String getDefaultHTML()
		{
			return defaultHtml;
		}
		
		static void setDefaultHTML(String html)
		{
			defaultHtml=html;
		}
	}
	
	public enum InputBox
	{
		SEND_KEY("InputBoxSendKey@"+KeyEvent.VK_ENTER);
		
		private final String KEY;
		private final String DEFAULT;
		InputBox(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			if(tmp.length>1) DEFAULT=tmp[1];
			else DEFAULT="";
		}

		public String getKey()
		{
			return KEY;
		}

		public String getDefault()
		{
			return DEFAULT;
		}
	}
	
	public enum WhiteBoard
	{
		DEFAULT_COLOR("DefaultColor@");
		
		private final String KEY;
		private final String DEFAULT;
		WhiteBoard(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			if(tmp.length>1) DEFAULT=tmp[1];
			else DEFAULT="";
		}

		public String getKey()
		{
			return KEY;
		}

		public String getDefault()
		{
			return DEFAULT;
		}
	}
	
	private static boolean loadFile()
	{
		File f=new File(SETTINGS_FILE);
		if(!f.exists()) return false;
		try {
			inStream=new FileInputStream(f);
			propertiesTable.load(inStream);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} catch (IOException ex)
		{
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		f=new File(test.MAINPATH+getProperty(ChattingBox.HTML_FILE_PATH.getKey()));
		FileReader fileReader;
		BufferedReader reader;
		StringBuilder sb=new StringBuilder();
		String tmp;
		try
		{
			fileReader=new FileReader(f);
			reader=new BufferedReader(fileReader);
		} catch (FileNotFoundException ex)
		{
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		try
		{
			while((tmp=reader.readLine())!=null)
				sb.append(tmp);
		} catch (IOException ex)
		{
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
		}
		tmp=sb.toString();
		if(!tmp.equals("")) ChattingBox.setDefaultHTML(tmp);
		return true;
	}
	
	private static boolean saveFile()
	{
		File ini=new File(SETTINGS_FILE);
		OutputStream fos;
		if (!ini.exists())
			try
			{
				ini.createNewFile();
			} catch (IOException ex)
			{
				Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
			}
		try
		{
			fos=new FileOutputStream(ini);
			propertiesTable.store(fos, COMMENT);
		} catch (FileNotFoundException ex)
		{
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} catch (IOException ex)
		{
			Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}
	
	static
	{
		propertiesTable.clear();
		for (           Global e   :Global.values())
			propertiesTable.setProperty(e.getKey(), e.getDefault());
		for (            Client e   :Client.values())
			propertiesTable.setProperty(e.getKey(), e.getDefault());
		for (         ListBox e   :ListBox.values())
			propertiesTable.setProperty(e.getKey(), e.getDefault());
		for (ChattingBox e   :ChattingBox.values())
			propertiesTable.setProperty(e.getKey(), e.getDefault());
		for (      InputBox e   :InputBox.values())
			propertiesTable.setProperty(e.getKey(), e.getDefault());
		for ( WhiteBoard e   :WhiteBoard.values())
			propertiesTable.setProperty(e.getKey(), e.getDefault());
		if (!loadFile())
			saveFile();
	}
	
	private static final String DEFAULT_HTML_FOR_CHATTING_BOX=
			"<html>"
			+"<head>"
			+"<meta charset='UTF-8'>"
			+"</head>"
			+"<body>"
			+"<p id='-1'>&nbsp;</p>"
			+"</body>"
			+"</html>"
			+"<!--SPLIT-->"
			+"<p align='center'>"
			+"<!--ANNOUNCEMENT-->"
			+"</p>"
			+"<!--SPLIT-->"
			+"<p align='center'>"
			+"<!--TIME-->"
			+"</p>"
			+"<!--SPLIT-->"
			+"<table id='<!--RECORDID-->' border='0' white-space='0' align='<!--DIRECTION-->' cellspacing='0' cellpadding='0' style='font-size:0;-webkit-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none;'>"
			+"<tr>"
			+"<td id='<!--RECORDID-->-leftHead' rowspan='3'>"
			+"<!--LEFTHEAD-->"
			+"</td>"
			+"<td>"
			+"<img src='<!--IMGPATH-->bubble_lu.jpg'>"
			+"</td>"
			+"<td style='background-image:url("
			+"<!--IMGPATH-->bubble_up.jpg);background-repeat:repeat-x;'>"
			+"&nbsp;"
			+"</td>"
			+"<td>"
			+"<img src='<!--IMGPATH-->bubble_ru.jpg'>"
			+"</td>"
			+"<td id='<!--RECORDID-->-rightHead' rowspan='3'>"
			+"<!--RIGHTHEAD-->"
			+"</td>"
			+"</tr>"
			+"<tr>"
			+"<td style='background-image:url(<!--IMGPATH-->bubble_le.jpg)'>&nbsp;</td>"
			+"<td id='<!--RECORDID-->-message' style='-webkit-user-select:text;-moz-user-select:text;-ms-user-select:text;user-select:text;font-size:12px;'>"
			+"<!--MESSAGE-->"
			+"</td>"
			+"<td style='background-image:url(<!--IMGPATH-->bubble_ri.jpg)'>"
			+"&nbsp;"
			+"</td>"
			+"</tr>"
			+"<tr>"
			+"<td>"
			+"<img src='<!--IMGPATH-->bubble_ld.jpg'>"
			+"</td>"
			+"<td style='background-image:url(<!--IMGPATH-->bubble_do.jpg)'>"
			+"&nbsp;"
			+"</td>"
			+"<td>"
			+"<img src='<!--IMGPATH-->bubble_rd.jpg'>"
			+"</td>"
			+"</tr>"
			+"</table>"
			+"<br/>"
			+"<!--SPLIT-->" 
			+"<a href='pict:<!--PICTPATH--><!--PICTUREATi-->'>" 
			+"<img id='<!--RECORDID-->-<!--i-->' border='0' width=160px height=100px src='<!--PICTPATH--><!--PICTUREATi-->' alt='正在加载图片'>\n" 
			+"</a>";
}
