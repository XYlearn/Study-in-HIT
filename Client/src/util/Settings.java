package util;

import bin.test;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	 * 读取属性值。
	 * @param key 键值
	 * @return 所查询的属性值
	 */
	public static String getProperty(String key)
	{
		return propertiesTable.getProperty(key);
	}
	
	/**
	 * 设置并保存新属性。
	 * @param key 键值
	 * @param value 新的属性值
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
			DEFAULT=tmp[1];
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
			DEFAULT=tmp[1];
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
			DEFAULT=tmp[1];
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
		MESSAGE_TYPE_ANNOUNCEMENT("ChattingBoxMessageTypeAnnouncement@anno");
		
		private final String KEY;
		private final String DEFAULT;
		ChattingBox(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			DEFAULT=tmp[1];
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
	
	public enum InputBox
	{
		SEND_KEY("InputBoxSendKey@"+KeyEvent.VK_ENTER);
		
		private final String KEY;
		private final String DEFAULT;
		InputBox(String key)
		{
			String[] tmp=key.split("@");
			KEY=tmp[0];
			DEFAULT=tmp[1];
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
			DEFAULT=tmp[1];
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
}
