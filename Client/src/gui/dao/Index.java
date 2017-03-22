/*
 * Index.java
 *
 * Created on __DATE__, __TIME__
 */
package gui.dao;

import bin.test;
import java.awt.Toolkit;

import gui.form.fankui;
import gui.form.fenxiang;
import gui.form.jinghua;
import gui.form.souti;
import gui.form.yinan;

/**
 *
 * @author __USER__
 */
public class Index extends javax.swing.JFrame
{

	main ma=new main();

	/**
	 * Creates new form Index
	 */
	public Index()
	{
		initComponents();
		setLocationRelativeTo(null);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(test.IMGPATH+"texs.jpg"));
		//setExtendedState(this.MAXIMIZED_BOTH);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jButton3 = new javax.swing.JButton();
		jButton4 = new javax.swing.JButton();
		jButton5 = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("\u5b66\u5728\u5de5\u5927\u7ba1\u7406\u4e3b\u9875");

		jTable1.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { "1", "ѧ������" }, { "2", "ѧ������" },
						{ "3", "ѧ������" }, { "4", "ѧ������" }, { "5", "ѧ������" } },
				new String[] { "�����", "��������" }));
		jScrollPane1.setViewportView(jTable1);

		jButton1.setText("\u8fdb\u5165\u7591\u95ee\u533a");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText("\u8fdb\u5165\u641c\u9898\u533a");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		jButton3.setText("\u8fdb\u5165\u5206\u4eab\u533a");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton3ActionPerformed(evt);
			}
		});

		jButton4.setText("\u8fdb\u5165\u7cbe\u534e\u533a");
		jButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton4ActionPerformed(evt);
			}
		});

		jButton5.setText("\u95ee\u9898\u53cd\u9988\u533a");
		jButton5.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton5ActionPerformed(evt);
			}
		});

		jLabel1.setText("\u4e0b\u5348\u597d\u3002");

		jLabel2.setText("\u4eb2\u7231\u7684\u5f20\u4e09\uff0c");

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup().add(230, 230, 230)
						.add(jButton1).add(69, 69, 69).add(jButton2)
						.add(53, 53, 53).add(jButton3).add(48, 48, 48)
						.add(jButton4).add(49, 49, 49).add(jButton5)
						.addContainerGap(314, Short.MAX_VALUE))
				.add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1228,
						Short.MAX_VALUE)
				.add(layout
						.createSequentialGroup()
						.add(59, 59, 59)
						.add(jLabel2)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED)
						.add(jLabel1,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								50,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(1040, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.add(28, 28, 28)
						.add(layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.BASELINE)
								.add(jLabel2).add(jLabel1))
						.add(26, 26, 26)
						.add(layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.BASELINE)
								.add(jButton3).add(jButton4).add(jButton5)
								.add(jButton2).add(jButton1))
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED, 57,
								Short.MAX_VALUE)
						.add(jScrollPane1,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								320,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.add(93, 93, 93)));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents
	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt)
	{
		fankui yi=new fankui();
		yi.setVisible(true);
		//ma.jDesktopPane5.add(yi);
		ma.show();
	}

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt)
	{
		jinghua yi=new jinghua();
		yi.setVisible(true);
		//ma.jDesktopPane4.add(yi);
		ma.show();
	}

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt)
	{
		fenxiang yi=new fenxiang();
		yi.setVisible(true);
		//ma.jDesktopPane3.add(yi);
		ma.show();
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)
	{
		souti yi=new souti();
		yi.setVisible(true);
		//ma.jDesktopPane2.add(yi);
		ma.show();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)
	{
		yinan yi=new yinan();
		yi.setVisible(true);
		//ma.jDesktopPane1.add(yi);
		ma.show();

	}

	/**
	 * @param args the command line arguments
	 */
	/*public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Index().setVisible(true);
			}
		});
	}*/

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	private javax.swing.JButton jButton4;
	private javax.swing.JButton jButton5;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;
	// End of variables declaration//GEN-END:variables

}