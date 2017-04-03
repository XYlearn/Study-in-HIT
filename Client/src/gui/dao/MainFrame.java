/*
 * main.java
 *
 * Created on __DATE__, __TIME__
 */
package gui.dao;

import NetEvent.eventcom.NetEvent;
import bin.test;
import com.ServerResponseMessage.QuestionListMessage;
import gui.ChattingBox;
import gui.InputBox;
import gui.ListBox;
import gui.SearchBox;
import gui.form.UserInformation;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.Dispatcher;

/**
 *
 * @author __USER__
 */
public class MainFrame extends javax.swing.JFrame implements Dispatcher
{
	private final ListBox listBox;
	private final SearchBox searchBox;
	
	public MainFrame()
	{
		initComponents();
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int)(screenSize.getWidth()*0.6),(int)(screenSize.getHeight()*0.6));
		setLocationRelativeTo(null);
		try
		{
			headLabel.setIcon(new ImageIcon(ImageIO.read(new File(test.IMGPATH+"test.jpg"))
					.getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
		} catch (IOException ex)
		{
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		headLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		headLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				//new UserInformation(UserInfo.getMyUserName()).setVisible(true);
				new UserInformation("test").setVisible(true);
			}
		});
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				test.IMGPATH+"texs.jpg"));
		listBox=new ListBox();
		listBoxPanel.add(listBox,BorderLayout.CENTER);
		tabPane.remove(0);
		addStartPageTab();
		addQuestionTab(1);
		searchBox=new SearchBox();
		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(searchBox,BorderLayout.CENTER);
	}
	
	public void dispatch(NetEvent e)
	{
		switch(e.type)
		{
			case QUESTION_LIST_EVENT:
			{
				
				break;
			}
		}
	}
	
	public void addQuestionTab(long questionID)
	{
		synchronized(tabPane)
		{
			JPanel tmpPanel=new JPanel();
			ChattingBox tmpChattingBox=new ChattingBox();
			InputBox tmpInputBox=new InputBox();
			
			tabPane.addTab("", tmpPanel);
			tabPane.setTabComponentAt(tabPane.getTabCount()-1,
				getNewTabPanel(Long.toString(questionID)));
			
			tmpPanel.setLayout(new BorderLayout());
			tmpPanel.add(tmpChattingBox, BorderLayout.CENTER);
			tmpPanel.add(tmpInputBox, BorderLayout.SOUTH);
			tmpInputBox.setPreferredSize(new Dimension(tmpInputBox.getWidth(),150));
			tmpChattingBox.bind(questionID);
			tmpInputBox.bind(questionID);
		}
	}
	
	public void addSearchTab(Iterable<QuestionListMessage> listData)
	{
		synchronized(tabPane)
		{
			JPanel tmpPanel=new JPanel();
			ListBox tmpListBox=new ListBox();
			tmpListBox.readList(listData);
			
			tabPane.addTab("", tmpPanel);
			tabPane.setTabComponentAt(tabPane.getTabCount()-1,
				getNewTabPanel("搜索结果"));
			
			//tmpListBox.setSize(tmpPanel.getWidth()/4*3,tmpPanel.getHeight()/4*3);
			
			tmpPanel.setLayout(new BorderLayout());
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
				getNewTabPanel("起始页"));
			tmpPanel.setLayout(new BorderLayout());
		}
	}
	
	/**
	 *
	 * @param text text on the tab
	 * @return
	 */
	protected JPanel getNewTabPanel(String text)
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
				tabPane.remove(tabPane.indexOfTabComponent(tmpTabPanel));
				if(tabPane.getTabCount()==0) addStartPageTab();
			}
		});
		return tmpTabPanel;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel2 = new javax.swing.JLabel();
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
        jPanel1 = new javax.swing.JPanel();
        listBoxPanel = new javax.swing.JPanel();
        headLabel = new javax.swing.JLabel();
        searchPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("学在工大");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("张三");

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

        jPanel1.setLayout(new java.awt.BorderLayout());
        tabPane.addTab("startPage", jPanel1);

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
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)))
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
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel listBoxPanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables

}
