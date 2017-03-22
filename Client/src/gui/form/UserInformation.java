package gui.form;

import bin.test;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserInformation extends javax.swing.JFrame
{
	private static Image backgroundImg;
	private static Image userHeadImg;
	private JLabel bgLabel;
	/**
	 * Creates new form UserInformation
	 * @param username
	 */
	public UserInformation(String username)
	{
		initComponents();
		setLocationRelativeTo(null);
		//setResizable(false);
		try
		{
			backgroundImg=ImageIO.read(new File(test.IMGPATH+"UserInfoBackground.jpg"));
			//UserInfo.getPicURL(username);
			userHeadImg=ImageIO.read(new File(test.PICTPATH+username+".jpg"));
		} catch (IOException ex)
		{
			System.out.println("pictures not found.");
		}
		((JPanel)this.getContentPane()).setOpaque(false);
		bgLabel=new JLabel(new ImageIcon(test.IMGPATH+"UserInfoBackground.jpg"));
		bgLabel.setBounds(-9, -22, this.getWidth(), this.getHeight());
		this.getRootPane().add(bgLabel);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        userHeadLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setText("昵称：");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(userHeadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 139, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(userHeadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 280, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel userHeadLabel;
    // End of variables declaration//GEN-END:variables
}
