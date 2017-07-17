package gui;

import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.QuestionListEvent;
import NetEvent.eventcom.SearchQuestionEvent;
import NetEvent.messagecom.QuestionListMessage;
import bin.test;
import java.awt.Cursor;
import gui.ListBoxModel.SORT;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JList;
import javax.swing.JPanel;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import util.Dispatcher;

public class ListBox extends JPanel implements Dispatcher
{
	private final JList<ListElementData> mylist;
	private final ListBoxModel mymodel;
	private final JScrollPane myscroll;
	private Consumer<MouseEvent> mouseListener;
	
	private static final Map<Integer,ListBox> map=new ConcurrentHashMap<Integer,ListBox>();
	private static final Map<Long,String> owner=new ConcurrentHashMap<>();
	private int searchID=0;
	
	private static final String SORT_BY_ID="i";
	private static final String SORT_BY_ASKTIME="a";
	private static final String SORT_BY_GOOD="g";
	private static final String SORT_BY_USERNUM="u";
	private static final String SORT_BY_LASTTIME="l";
	
	public ListBox()
	{
		mylist=new JList<>();
		mymodel=new ListBoxModel();
		mylist.setModel(mymodel);
		mylist.setCellRenderer(new ListBoxCellRenderer());
		mylist.setLayoutOrientation(JList.VERTICAL);
		mylist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mylist.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mylist.setSelectionBackground(Color.LIGHT_GRAY);
		
		//设置鼠标监听
		mouseListener=(MouseEvent e)->
		{
			int index=mylist.locationToIndex(e.getPoint());
			if(index<0) return;
			if(!test.mainFrame.addQuestionTab(mymodel.getElementAt(index).questionID))
				return;
			try
			{
				test.client.enterQuestion(mymodel.getElementAt(index).questionID);
			} catch (IOException ex)
			{
				Logger.getLogger(ListBox.class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("发送进入房间请求失败");
			}
		};
		
		//添加高亮效果
		MouseAdapter ma=new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				mylist.setSelectedIndex(mylist.locationToIndex(e.getPoint()));
			}
			@Override
			public void mouseMoved(MouseEvent e)
			{
				mylist.setSelectedIndex(mylist.locationToIndex(e.getPoint()));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				mylist.setSelectedIndices(new int[]{-1});
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mylist.setSelectedIndices(new int[]{-1});
			}
			//点击触发进入房间
			public void mouseClicked(MouseEvent e)
			{
				mouseListener.accept(e);
			}
		};
		mylist.addMouseListener(ma);
		mylist.addMouseMotionListener(ma);
		
		myscroll=new JScrollPane(mylist);
		setLayout(new BorderLayout());
		add(myscroll,BorderLayout.CENTER);
	}
	
	public void bind(int searchID)
	{
		this.searchID=searchID;
		map.put(searchID, this);
	}
	
	public void unbind()
	{
		map.remove(searchID);
	}
	
	public static void dispatch(NetEvent e)
	{
		switch(e.type)
		{
			case QUESTION_LIST_EVENT:
			{
				QuestionListEvent ex=(QuestionListEvent)e;
				if(map.containsKey(0))
					map.get(0).readList(ex.getQuestionListMessage());
				break;
			}
			case SEARCH_QUESTION_EVENT:
			{
				SearchQuestionEvent ex=(SearchQuestionEvent)e;
				if(map.containsKey(ex.getSearchID()))
					map.get(ex.getSearchID()).readList(ex.getQuestionList());
				break;
			}
			default:
				break;
		}
	}
	
	public static String getOwner(long questionID)
	{
		return owner.getOrDefault(questionID,"");
	}
	
	/**
	 *
	 * @param data  The element data to be added
	 */
	public synchronized void add(ListElementData data)
	{
		mymodel.addElement(data);
	}
	
	public synchronized void add(long questionID, String askTime, String username, String pictname,String stem,String addition, int good, int usernum, String lastTime)
	{
		mymodel.addElement(new ListElementData(questionID, askTime, username, pictname, stem, addition, good, usernum, lastTime));
	}
	
	public synchronized void add(long questionID, String askTime, String username, String stem, String addition, int good, int usernum, String lastTime)
	{
		mymodel.addElement(new ListElementData(questionID, askTime, username, "", stem, addition, good, usernum, lastTime));
	}
	
	public synchronized void sort(String sortby)
	{
		switch(sortby)
		{
			case SORT_BY_ID:
				mymodel.sortElement(SORT.ID);
				break;
			case SORT_BY_ASKTIME:
				mymodel.sortElement(SORT.ASKTIME);
				break;
			case SORT_BY_GOOD:
				mymodel.sortElement(SORT.GOOD);
				break;
			case SORT_BY_USERNUM:
				mymodel.sortElement(SORT.USERNUM);
				break;
			case SORT_BY_LASTTIME:
				mymodel.sortElement(SORT.LASTTIME);
				break;
			default:
				System.out.println("排序依据无效");
		}
	}
	
	public void setClickListener(Consumer<MouseEvent> listener)
	{
		mouseListener=listener;
	}
	
	public synchronized int locationToIndex(Point location)
	{
		return mylist.locationToIndex(location);
	}
	
	public synchronized ListElementData getElementAt(int index)
	{
		return mymodel.getElementAt(index);
	}
	
	public synchronized void addElement(ListElementData element)
	{
		mymodel.addElement(element);
	}
	
	public synchronized void removeElementAt(int index)
	{
		mymodel.removeElementAt(index);
	}
	
	public void readList(Iterable<QuestionListMessage> listData)
	{
		//synchronized(mymodel)
		//{
			while(mymodel.getSize()!=0) mymodel.removeElementAt(0);
			listData.forEach((QuestionListMessage msg)->
			{
				ListElementData d=new ListElementData(
					msg.getQuestionID(),
					"",//askTime
					"",//userName
					"",//pictname
					"",//stem
					msg.getQuestionDescription(),
					msg.getGood(),
					msg.getUserNum(),
					"");//lastTime
				mymodel.addElement(d);
				owner.put(msg.getQuestionID(),msg.getOwner());
			});
		//}
	}
}