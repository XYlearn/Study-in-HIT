/*
 * main.java
 *
 * Created on __DATE__, __TIME__
 */
package gui.dao;

import NetEvent.eventcom.CreateQuestionEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.messagecom.UserMessage;
import NetEvent.Client;
import bin.test;
import com.ClientSendMessage;
import gui.ChattingBox;
import gui.InputBox;
import gui.ListBox;
import gui.SearchBox;
import gui.form.UserInformation;
import gui.form.WhiteBoardFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import util.AudioTools;
import util.Dispatcher;
import util.UserInfo;

/**
 *
 * @author __USER__
 */
public class MainFrame extends javax.swing.JFrame implements Dispatcher
{
	private final ListBox listBox;
	private final SearchBox searchBox;
	private final JFileChooser fileChooser;
	private NewRoomFrame newRoomFrame=null;
	private final WhiteBoardFrame whiteboardFrame;
	
	private static final Map<Long,JPanel> map=new ConcurrentHashMap<>();
	
	private static final int QUESTION_LIST_NUMBER=20;

	//add by x
	public static MainFrame mainFrame;
	
	public MainFrame()
	{
		mainFrame = this;
		initComponents();
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int)(screenSize.getWidth()*0.7),(int)(screenSize.getHeight()*0.7));
		setLocationRelativeTo(null);
		/*try
		{
			headLabel.setIcon(new ImageIcon(
				ImageIO.read(new File(test.PICTPATH+"test.jpg"))
				.getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
		} catch (IOException ex)
		{
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
		}*/
		headLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		headLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				UserInformation usrInfo=new UserInformation();
				usrInfo.load(UserInfo.getMyUserName());
				usrInfo.setVisible(true);
			}
		});
		newRoomButton.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					
					if(newRoomFrame==null) initNewRoomFrame();
					newRoomFrame.setVisible(true);
				}
			});
		refreshQuestionListButton.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					try
					{
						test.client.requestQuestionList(
								ClientSendMessage.LIST_REFERENCE.TIME,
								ClientSendMessage.RANKORDER.DESCENDING,
								QUESTION_LIST_NUMBER);
					} catch (IOException ex)
					{
						Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			});
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				test.IMGPATH+"texs.jpg"));
		listBox=new ListBox();
		listBox.bind(0);
		listBoxPanel.add(listBox,BorderLayout.CENTER);
		while(tabPane.getTabCount()>0)
			tabPane.remove(0);
		addStartPageTab();
		searchBox=new SearchBox();
		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(searchBox,BorderLayout.CENTER);
		fileChooser=new JFileChooser();
		whiteboardFrame=new WhiteBoardFrame(test.client);
		whiteboardFrame.setLocationRelativeTo(mainFrame);
		whiteboardFrame.setBounds((int)(screenSize.getWidth()*0.4),
							0,
							(int)(screenSize.getWidth()*0.5),
							(int)(screenSize.getHeight()*0.5));
		try
		{
			test.client.requestQuestionList(
				ClientSendMessage.LIST_REFERENCE.TIME,
				ClientSendMessage.RANKORDER.DESCENDING,
				QUESTION_LIST_NUMBER);
		} catch (IOException ex)
		{
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void dispatch(NetEvent e)
	{
		switch(e.type)
		{
			case CREATE_QUESTION_EVENT:
			{
				CreateQuestionEvent ex=(CreateQuestionEvent)e;
				if(ex.isSuccess())
				{
					newRoomFrame.clearText();
					newRoomFrame.setVisible(false);
					addQuestionTab(ex.getQuestionMessage().getId());
					try
					{
						test.client.requestQuestionList(
							ClientSendMessage.LIST_REFERENCE.TIME,
							ClientSendMessage.RANKORDER.DESCENDING,
							QUESTION_LIST_NUMBER);
						test.client.enterQuestion(ex.getQuestionMessage().getId());
					} catch (IOException ex1)
					{
						Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex1);
						System.out.println("发送进入新房间请求失败");
					}
				}
				else
					System.out.println("创建新房间失败");
				break;
			}
			case ENTER_QUESTION_EVENT:
			{
				whiteboardFrame.setVisible(true);
				break;
			}
		}
	}
	
	public void setUserInformation(UserMessage msg)
	{
		System.out.println("头像："+msg.getPicUrl());
		headLabel.setIcon(
				new ImageIcon(new ImageIcon(test.PICTPATH+msg.getPicUrl())
						.getImage()
						.getScaledInstance(headLabel.getWidth(), headLabel.getHeight(), Image.SCALE_DEFAULT)));
		usernameLabel.setText(msg.getUsername());
		UserInfo.setMyUserName(msg.getUsername());
	}
	
	public boolean addQuestionTab(long questionID)
	{
		if(map.containsKey(questionID))
		{
			tabPane.setSelectedComponent(map.get(questionID));
			return false;
		}
		synchronized(tabPane)
		{
			JPanel tmpPanel=new JPanel();
			JPanel middlePanel=new JPanel();
			JPanel buttonPanel=new JPanel();
			ChattingBox tmpChattingBox=new ChattingBox();
			InputBox tmpInputBox=new InputBox();
			
			JButton insertPictureButton=new JButton("图片");
			insertPictureButton.addMouseListener(
				new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						fileChooser.setDialogTitle("选择要发送的图片");
						fileChooser.setFileFilter(
							new FileNameExtensionFilter(
								"图片文件(*.jpg,*.bmp,*.gif,*.png)",
								"jpg","bmp","gif","png"));
						fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						if(fileChooser.showDialog(null,"打开")==JFileChooser.APPROVE_OPTION)
							tmpInputBox.insertImage(fileChooser.getSelectedFile());
					}
				});
			
			JButton recordAudioButton=new JButton("语音");
			final Color tmpRecordingColor=recordAudioButton.getBackground();
			recordAudioButton.addMouseListener(
				new MouseAdapter()
				{
					@Override
					public synchronized void mouseClicked(MouseEvent e)
					{
						if(!AudioTools.isCapturing())
						{
							System.out.println(recordAudioButton.getBackground());
							if(AudioTools.startCapture())
							{
								recordAudioButton.setText("停止");
								recordAudioButton.setBackground(Color.green);
							}
						}
						else
						{
							String filename=AudioTools.stopAndSaveCapture();
							recordAudioButton.setText("语音");
							recordAudioButton.setBackground(tmpRecordingColor);
							if(filename!=null)
								tmpInputBox.sendAudio(filename);
						}
					}
				});
			
			JButton expressionConvertButton=new JButton("公式(Tab)");
			expressionConvertButton.addMouseListener(
				new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						tmpInputBox.readAndInsertExpression();
					}
				});
			
			JButton deleteRoomButton=new JButton("删除房间");
			deleteRoomButton.addMouseListener(
				new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						deleteRoomButton.setEnabled(ListBox.getOwner(questionID).equals(UserInfo.getMyUserName()));
						if(ListBox.getOwner(questionID).equals(UserInfo.getMyUserName()))
						{
							try
							{
								test.client.abandonQuestion(questionID);
								test.client.requestQuestionList(
									ClientSendMessage.LIST_REFERENCE.TIME,
									ClientSendMessage.RANKORDER.DESCENDING,
									QUESTION_LIST_NUMBER);
								tabPane.remove(tabPane.getSelectedIndex());
								map.remove(questionID);
							} catch (IOException ex)
							{
								Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					}
				});
			
			JButton sendButton=new JButton("发送(Enter)");
			sendButton.addMouseListener(
				new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						tmpInputBox.sendMessage();
					}
				});
			
			buttonPanel.setLayout(new GridLayout(1,7,5,5));
			buttonPanel.add(recordAudioButton);
			buttonPanel.add(insertPictureButton);
			buttonPanel.add(expressionConvertButton);
			buttonPanel.add(new JPanel());
			buttonPanel.add(deleteRoomButton);
			buttonPanel.add(new JPanel());
			buttonPanel.add(sendButton);
			
			tabPane.addTab("", tmpPanel);
			tabPane.setTabComponentAt(tabPane.getTabCount()-1,
				getNewTabPanel(questionID,Long.toString(questionID)));
			
			BorderLayout bl1=new BorderLayout();
			BorderLayout bl2=new BorderLayout();
			bl1.setHgap(3);
			bl1.setVgap(3);
			bl2.setHgap(3);
			tmpPanel.setLayout(bl1);
			middlePanel.setLayout(bl2);
			middlePanel.add(buttonPanel,BorderLayout.NORTH);
			middlePanel.add(tmpInputBox, BorderLayout.SOUTH);
			tmpPanel.add(tmpChattingBox, BorderLayout.CENTER);
			tmpPanel.add(middlePanel,BorderLayout.SOUTH);
			tmpInputBox.setPreferredSize(new Dimension(0,150));
			buttonPanel.setPreferredSize(new Dimension(0,25));
			tmpChattingBox.bind(questionID);
			tmpInputBox.bind(questionID);
			tmpInputBox.setEditable(false);
			tabPane.setSelectedComponent(tmpPanel);
			map.put(questionID, tmpPanel);
			return true;
		}
	}
	
	public void addSearchTab(int searchID)
	{
		synchronized(tabPane)
		{
			JPanel tmpPanel=new JPanel();
			ListBox tmpListBox=new ListBox();
			tmpListBox.bind(searchID);
			
			tabPane.addTab("", tmpPanel);
			tabPane.setTabComponentAt(tabPane.getTabCount()-1,
				getNewTabPanel(-2,"搜索结果"));
			
			BorderLayout bl=new BorderLayout();
			bl.setVgap(10);
			bl.setHgap(10);
			tmpPanel.setLayout(bl);
			tmpPanel.add(tmpListBox,BorderLayout.CENTER);
		}
	}
	
	private void addStartPageTab()
	{
		synchronized(tabPane)
		{
			JPanel tmpPanel=new JPanel();
			tabPane.addTab("", tmpPanel);
			tabPane.setTabComponentAt(tabPane.getTabCount()-1,
				getNewTabPanel(-1,"起始页"));
			tmpPanel.setLayout(new BorderLayout());
		}
	}

	//return jScrollPanel to fix mouse position
	public JScrollPane getjScrollPane1() {return this.jScrollPane1;}

	/**
	 *
	 * @param questionID
	 * @param text text on the tab
	 * @return
	 */
	protected JPanel getNewTabPanel(long questionID,String text)
	{
		JPanel tmpTabPanel=new JPanel();
		tmpTabPanel.add(new JLabel(text));
		tmpTabPanel.setOpaque(false);
			
		JButton tmpClose=new JButton("x");
		tmpClose.setMargin(new Insets(0,0,0,0));
		tmpClose.setPreferredSize(new Dimension(15,15));
		tmpTabPanel.add(tmpClose);
		
		tmpClose.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(questionID>=0)
				{
					//给服务器发送退出房间消息
					try {
						Client.client.exitQuestion(questionID);
					} catch (IOException ex) {}
					ChattingBox.unbind(questionID);
					InputBox.unbind(questionID);
					map.remove(questionID);
				}
				tabPane.remove(tabPane.indexOfTabComponent(tmpTabPanel));
				if(tabPane.getTabCount()==0) addStartPageTab();
			}
		});
		return tmpTabPanel;
	}
	
	private void initNewRoomFrame()
	{
		JLabel label1=new JLabel("你的问题：");
		JLabel label2=new JLabel("详细描述：");
		JLabel errLabel=new JLabel();
		errLabel.setForeground(Color.red);
		JTextField stemText=new JTextField();
		JTextArea additionText=new JTextArea();
		JButton okButton=new JButton("创建问题");
		JButton cancelButton=new JButton("取消");
		
		JPanel topPanel=new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(label1,BorderLayout.WEST);
		topPanel.add(stemText);
		label1.setPreferredSize(new Dimension(100,0));
		
		JPanel middlePanel=new JPanel();
		middlePanel.setLayout(new BorderLayout());
		middlePanel.setAlignmentY(0);
		middlePanel.add(label2,BorderLayout.WEST);
		middlePanel.add(additionText);
		label2.setPreferredSize(new Dimension(100,0));
		
		JPanel buttonPanel=new JPanel();
		BorderLayout bl1=new BorderLayout();
		bl1.setHgap(10);
		buttonPanel.setPreferredSize(new Dimension(150,0));
		buttonPanel.setLayout(bl1);
		buttonPanel.add(okButton,BorderLayout.WEST);
		buttonPanel.add(cancelButton,BorderLayout.EAST);
		
		JPanel bottomPanel=new JPanel();
		bottomPanel.setPreferredSize(new Dimension(0,40));
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(buttonPanel,BorderLayout.EAST);
		bottomPanel.add(errLabel,BorderLayout.WEST);
		
		newRoomFrame=new NewRoomFrame(stemText,additionText,"创建问题");
		BorderLayout bl=new BorderLayout();
		bl.setVgap(5);
		newRoomFrame.getContentPane().setLayout(bl);
		newRoomFrame.setPreferredSize(new Dimension(400,300));
		newRoomFrame.setResizable(false);
		newRoomFrame.getContentPane().add(topPanel,BorderLayout.NORTH);
		newRoomFrame.getContentPane().add(middlePanel,BorderLayout.CENTER);
		newRoomFrame.getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		
		newRoomFrame.pack();
		newRoomFrame.setLocationRelativeTo(null);
		
		cancelButton.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					newRoomFrame.setVisible(false);
					errLabel.setText("");
					//stemText.setText("");
					//additionText.setText("");
				}
			});
		
		okButton.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if(stemText.getText().length()<5)
					{
						errLabel.setText("标题字数太少辣！");
						return;
					}
					else if(additionText.getText().length()==0)
					{
						errLabel.setText("问题描述不要留空辣！");
						return;
					}
					else
						errLabel.setText("");
					
					try
					{
						test.client.createQuestion(
								stemText.getText(),
								additionText.getText(),
								SearchBox.getKeyWords(stemText.getText()));
					} catch (IOException ex)
					{
						Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
						errLabel.setText("发送创建房间请求失败");
					}
				}
			});
	}
	
	private class NewRoomFrame extends JFrame
	{
		private JTextField stemText;
		private JTextArea additionText;
		public NewRoomFrame(JTextField stemText,JTextArea additionText,String title)
		{
			super(title);
			this.stemText=stemText;
			this.additionText=additionText;
		}
		public void clearText()
		{
			stemText.setText("");
			additionText.setText("");
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        usernameLabel = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel27 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        tabPane = new javax.swing.JTabbedPane();
        listBoxPanel = new javax.swing.JPanel();
        headLabel = new javax.swing.JLabel();
        searchPanel = new javax.swing.JPanel();
        newRoomButton = new javax.swing.JButton();
        refreshQuestionListButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("学在工大");

        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setText("张三");

        jLabel17.setForeground(new java.awt.Color(255, 51, 51));
        jLabel17.setText("今日公告");

        jLabel18.setText("暂无公告");
        jLabel18.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel19.setText("房间总数：");

        jLabel20.setText("[暂无数据]");

        jLabel21.setText("待解决房间数：");

        jLabel22.setText("[暂无数据]");

        jLabel23.setText("在线人数：");

        jLabel24.setText("[暂无数据]");

        jLabel25.setText("[暂无数据]");

        jLabel26.setText("用户人数：");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel27.setForeground(new java.awt.Color(255, 51, 51));
        jLabel27.setText("笔记记录：");

        jButton1.setText("写入文本");

        listBoxPanel.setLayout(new java.awt.BorderLayout());

        headLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout searchPanelLayout = new org.jdesktop.layout.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        newRoomButton.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        newRoomButton.setText("+");
        newRoomButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        refreshQuestionListButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        refreshQuestionListButton.setText("↑");
        refreshQuestionListButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(listBoxPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(headLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(usernameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(refreshQuestionListButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(newRoomButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                    .add(searchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                            .add(jLabel27)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel19)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                        .add(24, 24, 24)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(layout.createSequentialGroup()
                                                .add(jLabel26)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel25, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .add(layout.createSequentialGroup()
                                                .add(jLabel23)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel24, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                        .add(jLabel21)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel22, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .add(14, 14, 14))
                            .add(jLabel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(0, 33, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(headLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(usernameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(newRoomButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(refreshQuestionListButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(listBoxPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(searchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 40, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel19)
                                    .add(jLabel20))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel21)
                                    .add(jLabel22))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel24)
                                    .add(jLabel23))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel26)
                                    .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel27)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(tabPane))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel headLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel listBoxPanel;
    private javax.swing.JButton newRoomButton;
    private javax.swing.JButton refreshQuestionListButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables

}
