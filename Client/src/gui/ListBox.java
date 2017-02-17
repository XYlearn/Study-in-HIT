package gui;

import java.awt.Cursor;
import gui.ListBoxModel.SORT;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.JPanel;
import java.util.function.Consumer;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ListBox extends JPanel
{
	private final JList<ListElementData> mylist;
	private final ListBoxModel mymodel;
	private final JScrollPane myscroll;
	private Consumer<MouseEvent> mouseListener=(MouseEvent e)->{};//调用打开房间接口
	
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
			public void mouseClicked(MouseEvent e)
			{
				mouseListener.accept(e);
			}
		};
		mylist.addMouseListener(ma);
		mylist.addMouseMotionListener(ma);
		
		myscroll=new JScrollPane(mylist);
		add(myscroll,BorderLayout.CENTER);
	}
	
	/**
	 *
	 * @param data  The element data to be added
	 */
	public void add(ListElementData data)
	{
		mymodel.addElement(data);
	}
	
	public void add(String questionID, String askTime, String username, String pictname,String stem,String addition, int good, int usernum, String lastTime)
	{
		mymodel.addElement(new ListElementData(questionID, askTime, username, pictname, stem, addition, good, usernum, lastTime));
	}
	
	public void add(String questionID, String askTime, String username, String stem, String addition, int good, int usernum, String lastTime)
	{
		mymodel.addElement(new ListElementData(questionID, askTime, username, "", stem, addition, good, usernum, lastTime));
	}
	
	public void sort(String sortby)
	{
		switch(sortby)
		{
			case "id":
				mymodel.sortElement(SORT.ID);
				break;
			case "asktime":
				mymodel.sortElement(SORT.ASKTIME);
				break;
			case "good":
				mymodel.sortElement(SORT.GOOD);
				break;
			case "usernum":
				mymodel.sortElement(SORT.USERNUM);
				break;
			case "lasttime":
				mymodel.sortElement(SORT.LASTTIME);
				break;
			default:
				System.out.println("排序依据无效");
		}
	}
	
	@Override
	public void setSize(int width,int height)
	{
		myscroll.setPreferredSize(new Dimension(width,height));
	}
	
	public void setClickListener(Consumer<MouseEvent> listener)
	{
		mouseListener=listener;
	}
	
	public int locationToIndex(Point location)
	{
		return mylist.locationToIndex(location);
	}
	
	public ListElementData getElementAt(int index)
	{
		return mymodel.getElementAt(index);
	}
	
	public void addElement(ListElementData element)
	{
		mymodel.addElement(element);
	}
	
	public void removeElementAt(int index)
	{
		mymodel.removeElementAt(index);
	}
	
	public void readList(String listData)
	{
		//to be finished
	}
}