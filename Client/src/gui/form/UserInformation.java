package gui.form;

import bin.test;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import util.UserInfo;

public class UserInformation extends javax.swing.JFrame
{
	private static Image backgroundImg;
	private static Image userHeadImg;
	private JLabel bgLabel;
	private JTextPane content;
	private HTMLDocument doc;
	private HTMLEditorKit kit;
	/**
	 * Creates new form UserInformation
	 * @param username
	 */
	public UserInformation(String username)
	{
		initComponents();
		setLocationRelativeTo(null);
		setResizable(false);
		try
		{
			backgroundImg=ImageIO.read(new File(test.IMGPATH+"UserInfoBackground.jpg"));
		} catch (IOException ex)
		{
			System.out.println("UserInfoBackground.jpg not found.");
		}
		kit=new HTMLEditorKit();
		content=new JTextPane();
		content.setContentType("text/html");
		kit.install(content);
		content.setEditorKit(kit);
		doc=(HTMLDocument)content.getStyledDocument();
		try
		{
			doc.setBase(new URL("file:"+test.IMGPATH));
		} catch (MalformedURLException ex)
		{
			Logger.getLogger(UserInformation.class.getName()).log(Level.SEVERE, null, ex);
		}
		content.setEditable(false);
		try
		{
			doc.insertAfterStart(doc.getRootElements()[0].getElement(0),
					"<body align='center' style='background-image:url(UserInfoBackground.jpg)'>"
					+"<p><img src='"+username+".jpg' width='50',height='50'></p>"
					+"<div style='font-size:16px;margin:0 0 0 100px;width:100%'>"
					+"<p align='left'>用户名："+username+"</p>"
					+"<p align='left'>签名："+UserInfo.getSignature(username)+"</p>"
					+"<p align='left'>邮箱："+UserInfo.getMailAddress(username)+"</p>"
					+"<p align='left'>点数："+UserInfo.getBonus(username)+"点</p>"
					+"<p align='left'>被赞次数："+UserInfo.getGood(username)+"次</p>"
					+"<p align='left'>提问次数："+UserInfo.getQuestionNum(username)+"次</p>"
					+"<p align='left'>解决次数："+UserInfo.getSolvedQuestionNum(username)+"次</p>"
					+"</div></body>");
			System.out.println(doc.getText(0, doc.getLength()));
			System.out.println(content.getText());
		} catch (IOException ex)
		{
			System.out.println(ex);
		} catch (BadLocationException ex)
		{
			Logger.getLogger(UserInformation.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		content.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.getContentPane().add(content);
		/*try
		{
			backgroundImg=ImageIO.read(new File(test.IMGPATH+"UserInfoBackground.jpg"));
			info.setOpaque(false);
			info.setText("用户名："+username+"</p>"
					+"签名："+UserInfo.getSignature(username)+"</p>"
					+"邮箱："+UserInfo.getMailAddress(username)+"</p>"
					+"点数："+UserInfo.getBonus(username)+"点</p>"
					+"被赞次数："+UserInfo.getGood(username)+"次</p>"
					+"提问次数："+UserInfo.getQuestionNum(username)+"</p>"
					+"解决次数："+UserInfo.getSolvedQuestionNum(username));
			//UserInfo.getPicURL(username);
			userHeadImg=ImageIO.read(new File(test.PICTPATH+username+".jpg"));
		} catch (IOException ex)
		{
			System.out.println("pictures not found.");
		}
		((JPanel)this.getContentPane()).setOpaque(false);
		bgLabel=new JLabel(new ImageIcon(test.IMGPATH+"UserInfoBackground.jpg"));
		bgLabel.setBounds(-9, -22, this.getWidth(), this.getHeight());
		this.getRootPane().add(bgLabel);*/
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
