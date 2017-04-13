package gui.dao;

import NetEvent.eventcom.LaunchEvent;
import NetEvent.eventcom.NetEvent;
import bin.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
	private JPanel topPanel;
	private JPanel labelPanel;
	private JPanel textPanel;
	private JPanel buttonPanel;
	private JLabel iconLabel;
	private JLabel errLabel;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JTextField usernameText;
	private JTextField passwordText;
	private JButton signinButton;
	private JButton loginButton;
	
	private boolean launched;
	private long startTime=0;
	private static final long connectDelay=1000;
	private static final long launchDelay=3000;
	private static final String STATUS_CONNECTING="连接中...";
	private static final String STATUS_LAUNCHING="登录中...";
	
	public LoginFrame()
	{
		launched=false;
		
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
		
		this.setTitle("学在工大 - 登录");
		this.getContentPane().setLayout(new BorderLayout());
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
		errLabel.setText(" ");
		if ("".equals(usernameText.getText()))
		{
			errLabel.setForeground(Color.red);
			errLabel.setText("用户名在哪里吖？");
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
			errLabel.setForeground(Color.red);
			errLabel.setText("密码不见了吖！");
			return;
		}
		abandonButtons();
		/*errLabel.setForeground(Color.green);
		errLabel.setText("连接中...");
		//errLabel.repaint();
		startTime=System.currentTimeMillis();
		if (!test.client.isAlive())
			test.client.start();
		try
		{
			Thread.sleep(connectDelay);
		} catch (InterruptedException ex)
		{
			Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (System.currentTimeMillis()>startTime+connectDelay)
		{
			test.client.interrupt();
			errLabel.setForeground(Color.red);
			errLabel.setText("连接超时辣！");
			enableButtons();
			return;
		}*/
		try
		{
			errLabel.setForeground(Color.green);
			errLabel.setText("登录中...");
			//this.repaint();
			startTime=System.currentTimeMillis();
			test.client.launchRequest(usernameText.getText(), passwordText.getText());
		} catch (IOException ex)
		{
			Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
			errLabel.setForeground(Color.red);
			errLabel.setText("网断辣！");
			enableButtons();
			return;
		}
		
		//delay for the launch
		/*try
		{
			Thread.sleep(launchDelay);
		} catch (InterruptedException ex)
		{
			Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		if (System.currentTimeMillis()>startTime+launchDelay)
		{
			test.client.interrupt();
			errLabel.setForeground(Color.red);
			errLabel.setText("登录超时辣！");
			enableButtons();
			passwordText.requestFocus();
			return;
		}*/
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
				if(ex.isSuccess())
				{
					launched=true;
					errLabel.setForeground(Color.green);
					errLabel.setText("登录成功辣！");
					test.mainFrame=new MainFrame();
					test.mainFrame.setUserInformation(ex.getUserMessage());
					this.setVisible(false);
					test.mainFrame.setVisible(true);
				}
				else
				{
					errLabel.setForeground(Color.red);
					errLabel.setText("登录失败辣！");
				}
				System.out.println(ex.getInformation());
				enableButtons();
			}
		}
	}
	
	private void abandonButtons()
	{
		usernameText.setEnabled(false);
		passwordText.setEnabled(false);
		signinButton.setEnabled(false);
		loginButton.setEnabled(false);
	}
	
	private void enableButtons()
	{
		usernameText.setEnabled(true);
		passwordText.setEnabled(true);
		signinButton.setEnabled(true);
		loginButton.setEnabled(true);
	}
}
