//package gui;
import javax.swing.*;

public class ChattingBox extends JPanel
{
	private JTextPane myPane;

	public static final void main(String[] args)
	{
		JFrame f=new JFrame("myApplication");
		ChattingBox c=new ChattingBox();
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
	}

	ChattingBox()
	{
		myPane=new JTextPane();
		myPane.setVisible(true);
		this.add(myPane);
	}
}