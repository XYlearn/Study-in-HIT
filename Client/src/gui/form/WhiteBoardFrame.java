package gui.form;

import NetEvent.Client;
import gui.wb.WhiteBoard;
import gui.wb.WhiteBoardAdapter;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author kqwyf
 */
public class WhiteBoardFrame extends JFrame
{
	private final WhiteBoard whiteboard;
	
	public WhiteBoardFrame(Client client)
	{
		whiteboard=new WhiteBoard(client);
		addComponentListener(new WhiteBoardAdapter(this));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(whiteboard,BorderLayout.CENTER);
	}
	
	public void choosePanel(long questionID)
	{
		whiteboard.choosePanel(questionID);
	}
	
	public void addPanel(long questionID)
	{
		whiteboard.addPanel(questionID);
	}
	
	public void removePanel(long questionID)
	{
		whiteboard.removePanel(questionID);
	}
}
