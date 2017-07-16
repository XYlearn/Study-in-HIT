package NetEvent;

import com.ClientSendMessage;
import gui.wb.WhiteBoard;
import gui.wb.WhiteBoardAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by xy16 on 17-2-12.
 */
public class Test {
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
		JFrame frame = new JFrame();
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();

		frame.add(new WhiteBoard(client));
		frame.setTitle("White Board");
		frame.setSize(screenSize.width/2, screenSize.height/2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocation(screenSize.width/4, screenSize.height/4);
		frame.addComponentListener(new WhiteBoardAdapter(frame));

		try {
			client.launchRequest("test", "123456");
			client.waitUntilLaunched();
			client.downloadFile("xy16.jpg");
		} catch (Exception e) { e.printStackTrace();}

	}
}
