package gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import bin.test;
import java.awt.Image;
import javax.swing.SwingConstants;

class ListBoxCellRenderer extends DefaultListCellRenderer
{
	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list,
			Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if(!"".equals(((ListElementData)value).pictname))
		{
			ImageIcon ii=new ImageIcon(test.PICTPATH+((ListElementData)value).pictname);
			Image image=ii.getImage();
			int tmpheight=image.getHeight(this);
			int tmpwidth=image.getWidth(this)
			if(tmpheight>100) ii.setImage(image.getScaledInstance(
					tmpwidth*100/tmpheight,100,
					Image.SCALE_DEFAULT));
			if(tmpwidth>200) ii.setImage(image.getScaledInstance(
					200,tmpheight*200/tmpwidth,
					Image.SCALE_DEFAULT));
			setIcon(ii);
		}
		setText(((ListElementData)value).stem);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
		return this;
	}
}