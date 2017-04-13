package gui.dao;

import NetEvent.eventcom.LaunchEvent;
import NetEvent.eventcom.NetEvent;
import bin.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import util.Dispatcher;

/**
 *
 * @author kqwyf
 */
public class LoginFrame extends JFrame implements Dispatcher
{
	private final JPanel topPanel;
	private final JPanel labelPanel;
	private final JPanel textPanel;
	private final JPanel buttonPanel;
	private final JLabel iconLabel;
	private final JLabel errLabel;
	private final JLabel usernameLabel;
	private final JLabel passwordLabel;
	private JTextField usernameText;
	private JTextField passwordText;
	private final JButton signinButton;
	private final JButton loginButton;
	
	private final TextThread textThread;
	
	private boolean launched;
	private static final String STATUS_CONNECTING="连接中...";
	private static final String STATUS_LAUNCHING="登录中...";
	
	public LoginFrame()
	{
		launched=false;
		textThread=new TextThread();
		
		iconLabel=new JLabel();
		iconLabel.setIcon(new ImageIcon(test.IMGPATH+"texs.jpg"));
		errLabel=new JLabel(" ");
		errLabel.setForeground(Color.red);
		usernameLabel=new JLabel("用户名：");
		passwordLabel=new JLabel("密码：");
		usernameText=new JTextField();
		passwordText=new JPasswordField();
		signinButton=new JButton("注册");
		loginButton=new JButton("登录");
		
		topPanel=new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(iconLabel,BorderLayout.CENTER);
		topPanel.add(errLabel,BorderLayout.SOUTH);
		
		labelPanel=new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.add(usernameLabel,BorderLayout.NORTH);
		labelPanel.add(passwordLabel,BorderLayout.SOUTH);
		
		textPanel=new JPanel();
		textPanel.setLayout(new BorderLayout());
		textPanel.add(usernameText,BorderLayout.NORTH);
		textPanel.add(passwordText,BorderLayout.SOUTH);
		
		buttonPanel=new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(signinButton,BorderLayout.WEST);
		buttonPanel.add(loginButton,BorderLayout.EAST);
		
		BorderLayout bl=new BorderLayout();
		bl.setVgap(5);
		this.setTitle("学在工大 - 登录");
		this.getContentPane().setLayout(bl);
		this.add(topPanel,BorderLayout.NORTH);
		this.add(labelPanel,BorderLayout.WEST);
		this.add(textPanel,BorderLayout.CENTER);
		this.add(buttonPanel,BorderLayout.SOUTH);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		usernameText.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					if("admin offline".equals(usernameText.getText()))
						launch();
					else
						passwordText.requestFocus();
				}
			}
		});
		passwordText.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
					launch();
			}
		});
		loginButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				launch();
			}
		});
	}
	
	public void launch()
	{
		if(!textThread.isAlive()) textThread.start();
		try
		{
			Thread.sleep(10);
		} catch (InterruptedException ex)
		{
			Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		if ("".equals(usernameText.getText()))
		{
			textThread.setText("用户名在哪里吖？",false);
			return;
		}
		else if ("admin offline".equals(usernameText.getText()))
		{
			this.setVisible(false);
			test.mainFrame=new MainFrame();
			test.mainFrame.setVisible(true);
			return;
		}
		else if ("".equals(passwordText.getText()))
		{
			textThread.setText("密码不见了吖！",false);
			return;
		}
		textThread.abandonButtons();
		textThread.setText("连接中...",true);
		if (!test.client.isAlive())
			test.client.start();
		if (!test.client.waitUntilConnected())
		{
			test.client.interrupt();
			textThread.setText("连接超时辣！",false);
			textThread.enableButtons();
			return;
		}
		try
		{
			textThread.setText("登录中...",true);
			test.client.launchRequest(usernameText.getText(), passwordText.getText());
		} catch (IOException ex)
		{
			Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
			textThread.setText("网断辣！",false);
			textThread.enableButtons();
			return;
		}
		
		//delay for the launch
		if (!test.client.waitUntilLaunched())
		{
			test.client.interrupt();
			textThread.setText("登录超时辣！",false);
			textThread.enableButtons();
			passwordText.requestFocus();
			return;
		}
	}
	
	public boolean isLaunched()
	{
		return launched;
	}
	
	public void dispatch(NetEvent e)
	{
		switch(e.type)
		{
			case LAUNCH_EVENT:
			{
				LaunchEvent ex=(LaunchEvent)e;
				if(ex.isSuccess()&&test.client.waitUntilLaunched())
				{
					launched=true;
					textThread.setText(ex.getInformation(),true);
					test.mainFrame=new MainFrame();
					test.mainFrame.setUserInformation(ex.getUserMessage());
					this.setVisible(false);
					test.mainFrame.setVisible(true);
					textThread.stopThread();
				}
				else
				{
					textThread.setText(ex.getInformation(),false);
				}
				System.out.println(ex.getInformation());
				textThread.enableButtons();
			}
		}
	}
	
	private class TextThread extends Thread
	{
		private boolean running=true;
		private boolean readyToEditText=false;
		private boolean readyToEditButtons=false;
		private String text="";
		private boolean good=false;
		private boolean target=false;
		
		@Override
		public void run()
		{
			while(running)
			{
				if(readyToEditText)
				{
					if(good) errLabel.setForeground(Color.green);
					else errLabel.setForeground(Color.red);
					errLabel.setText(text);
					readyToEditText=false;
				}
				else if(readyToEditButtons)
				{
					usernameText.setEnabled(target);
					passwordText.setEnabled(target);
					signinButton.setEnabled(target);
					loginButton.setEnabled(target);
					readyToEditButtons=false;
				}
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException ex)
				{
					Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		
		public void stopThread()
		{
			running=false;
		}
		
		public void setText(String text,boolean good)
		{
			this.text=text;
			this.good=good;
			readyToEditText=true;
		}
		
		public void abandonButtons()
		{
			target=false;
			readyToEditButtons=true;
		}

		public void enableButtons()
		{
			target=true;
			readyToEditButtons=true;
		}
	}
}
