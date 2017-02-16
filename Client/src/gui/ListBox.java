package gui;

import gui.ListBoxModel.SORT;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ListBox extends JPanel
{
	JList<ListElementData> mylist;
	ListBoxModel mymodel;
	JScrollPane myscroll;
	public ListBox()
	{
		mylist=new JList<>();
		mymodel=new ListBoxModel();
		mylist.setModel(mymodel);
		mylist.setCellRenderer(new ListBoxCellRenderer());
		mylist.setLayoutOrientation(JList.VERTICAL);
		mylist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		myscroll=new JScrollPane(mylist);
		add(myscroll,BorderLayout.CENTER);
	}
	
	/**
	 *
	 * @param data
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
}