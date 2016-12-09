package com;

import javax.swing.*;
import java.awt.*;

/**
 * Created by xy16 on 16-12-1.
 */
public class HomePage extends JFrame{

	public HomePage() {
		resourceInit();
		init();
		setLaunchPanel();
		this.setBounds(30,40,1600,1000);
		this.setVisible(true);
	}

	public void setConstraints(GridBagConstraints constraints, int gridx, int gridy
	,int gridwidth, int gridheight, double weightx, double weighty) {
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
	}

	public void resourceInit() {
		//资源
		ImageIcon imageIcon = new ImageIcon("/home/xy16/Pictures/HIT.jpg");
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(120,100,Image.SCALE_REPLICATE));
		launchIcon = imageIcon;
		titleIcon = imageIcon;
	}

	public void init() {
		//布局
		layout = new GridBagLayout();
		constraints = new GridBagConstraints();

		launchPanel = new JPanel();
		roomMessagePanel = new JPanel();
		buttonPanel = new JPanel();
		windowPanel = new JPanel();
		centerPanel = new JTabbedPane();
		announcementPane = new JTextPane();

		launchPanel.setBackground(Color.BLUE);
		roomMessagePanel.setBackground(Color.gray);
		buttonPanel.setBackground(Color.green);
		centerPanel.setBackground(Color.CYAN);
		windowPanel.setBackground(Color.red);
		announcementPane.setBackground(Color.ORANGE);

		this.setLayout(layout);
		this.add(launchPanel);
		this.add(windowPanel);
		this.add(roomMessagePanel);
		this.add(centerPanel);
		this.add(buttonPanel);
		this.add(announcementPane);

		//初始化控件
		roomMessage = new JLabel("房间列表");
		buttonAnswer = new JButton("答疑区");
		buttonExperience = new JButton("经验交流区");
		buttonFine = new JButton("精品区");
		title = new JLabel("学在工大",titleIcon,JButton.RIGHT);
		inputAreaLeft = new JTextArea(3,3);
		InputAreaRight = new JTextArea(3,3);
		announcement = new JLabel("公告");

		//关联控件
		constraints.fill = GridBagConstraints.BOTH;
		setConstraints(constraints,0,0,4,2,0,0);
		layout.setConstraints(launchPanel,constraints);

		setConstraints(constraints,4,0,0,2,0,0);
		layout.setConstraints(windowPanel,constraints);

		setConstraints(constraints,0,2,4,21,0,0.69);
		layout.setConstraints(roomMessagePanel,constraints);

		setConstraints(constraints,4,2,0,25,0.75,0.84);
		layout.setConstraints(centerPanel,constraints);

		setConstraints(constraints,0,23,4,4,0.15,0.14);
		layout.setConstraints(buttonPanel,constraints);

		setConstraints(constraints,0,27,0,1,1,0.1);
		layout.setConstraints(announcementPane,constraints);
	}

	void setLaunchPanel() {
		GridBagLayout launchLayout = new GridBagLayout();
		GridBagConstraints launchConstraints = new GridBagConstraints();
		launch = new JButton("登录",launchIcon);
		launch.setBorderPainted(false);
		JPanel blankPanel = new JPanel();
		launchPanel.setLayout(launchLayout);
		launchPanel.add(launch);
		launchPanel.add(blankPanel);

		launchConstraints.fill = GridBagConstraints.BOTH;
		setConstraints(launchConstraints,0,0,2,0,0.2,1);
		launchLayout.setConstraints(launch, launchConstraints);

		setConstraints(launchConstraints,2,0,8,0,0.8,1);
		launchLayout.setConstraints(blankPanel, launchConstraints);
	}

	GridBagLayout layout;
	GridBagConstraints constraints;

	JPanel launchPanel;
	JPanel roomMessagePanel;
	JPanel buttonPanel;
	JPanel windowPanel;
	JTabbedPane centerPanel;
	JTextPane announcementPane;

	JButton launch;
	JLabel roomMessage;
	JButton buttonAnswer;
	JButton buttonExperience;
	JButton buttonFine;
	JLabel title;
	JTextArea inputAreaLeft;
	JTextArea InputAreaRight;
	JLabel announcement;

	Icon launchIcon;
	Icon titleIcon;
}
