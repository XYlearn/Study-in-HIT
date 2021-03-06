package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;

class ListBoxModel extends AbstractListModel<ListElementData>
{
	private final List<ListElementData> dataList=new ArrayList<>();
	static enum SORT {ID,ASKTIME,GOOD,USERNUM,LASTTIME};
	
	void addElement(ListElementData element)
	{
		dataList.add(element);
		super.fireIntervalAdded(this, dataList.size()-1, dataList.size()-1);
	}
	
	void removeElementAt(int index)
	{
		dataList.remove(index);
		super.fireIntervalRemoved(this, index, index);
	}
	
	@Override
	public ListElementData getElementAt(int index)
	{
		return dataList.get(index);
	}
	
	@Override
	public int getSize()
	{
		return dataList.size();
	}
	
	/**
	 *
	 * @param sortby
	 */
	void sortElement(SORT sortby)
	{
		switch(sortby)
		{
			case ID:
				Collections.sort(dataList,(data1, data2)->
					{
						long tmp=((ListElementData)data1).questionID-((ListElementData)data2).questionID;
						if(tmp<0) return -1;
						else if(tmp==0) return 0;
						else return 1;
					});
				break;
			case ASKTIME:
				Collections.sort(dataList,(data1,data2)->data1.askTime.compareTo(data2.askTime));
				break;
			case GOOD:
				Collections.sort(dataList,(data1,data2)->data1.good-data2.good);
				break;
			case USERNUM:
				Collections.sort(dataList,(data1,data2)->data1.usernum-data2.usernum);
				break;
			case LASTTIME:
				Collections.sort(dataList,(data1,data2)->data1.lastTime.compareTo(data2.lastTime));
				break;
			default:
		}
	}
}
