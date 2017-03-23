/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author zhaowei
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
/**
 *
 * @author zhaowei
 */
public class WhiteBoard extends JFrame
        implements ActionListener,ChangeListener{
    ColorPanel colorChoose = new ColorPanel();
    ScribblePanel sP1 = new ScribblePanel();
    private JSlider jsldVert = new JSlider(JSlider.VERTICAL,0,20,4);
    
       public WhiteBoard(){
           getContentPane().setLayout(new BorderLayout());
           colorChoose.setLayout(new GridLayout(0,1));
           colorChoose.jb1.addActionListener(this);
           colorChoose.jb2.addActionListener(this);
           colorChoose.jb3.addActionListener(this);
           colorChoose.jb4.addActionListener(this);
           colorChoose.jb5.addActionListener(this);
           colorChoose.jb6.addActionListener(this);
           colorChoose.jb7.addActionListener(this);
           colorChoose.jb8.addActionListener(this);
           colorChoose.jb9.addActionListener(this);
           jsldVert.setPaintLabels(true);
           jsldVert.setPaintTicks(true);
           jsldVert.setMajorTickSpacing(4);
           jsldVert.setMinorTickSpacing(1);
           jsldVert.setPaintTrack(false);
           getContentPane().add(sP1,BorderLayout.CENTER);
           getContentPane().add(colorChoose,BorderLayout.EAST);
           getContentPane().add(jsldVert,BorderLayout.WEST);
           jsldVert.addChangeListener(this);
       }
       
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == colorChoose.jb1)
           sP1.color = Color.RED;
        if(e.getSource() == colorChoose.jb2)
            sP1.color = Color.ORANGE;
        if(e.getSource() == colorChoose.jb3)
            sP1.color = Color.YELLOW;
        if(e.getSource() == colorChoose.jb4)
            sP1.color = (Color.GREEN);
        if(e.getSource() == colorChoose.jb5)
            sP1.color = (Color.CYAN);
        if(e.getSource() == colorChoose.jb6)
            sP1.color = (Color.BLUE);
        if(e.getSource() == colorChoose.jb7)
            sP1.color = (Color.MAGENTA);
        if(e.getSource() == colorChoose.jb8)
            sP1.color = (Color.WHITE);
        if(e.getSource() == colorChoose.jb9)
            sP1.color = (Color.BLACK);
        
    }
    public void setWhiteColor(Graphics g) {
        g.setColor(Color.WHITE); 
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        double value = jsldVert.getValue();
        double maximumValue = jsldVert.getMaximum();
        float newPenSize = (float)(15*value/maximumValue);
        sP1.setPenSize(newPenSize);
    }
}
  class ScribblePanel extends JPanel
        implements MouseListener,MouseMotionListener{
    final int CIRCLESIZE = 20;
    Color color = Color.BLACK;
    private Point lineStart = new Point(0,0);
    public Graphics g;
    public Graphics2D g2;
    public float penSize = 3;
    public ScribblePanel(){
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    public void mouseClicked(MouseEvent e){
        
    }
    public void mouseEntered(MouseEvent e){
        
    }
    public void mouseExited(MouseEvent e){
        
    }
    public void mouseReleased(MouseEvent e){
        
    }
    public void mousePressed(MouseEvent e){
        lineStart.move(e.getX(),e.getY());
    }
    public void mouseDragged(MouseEvent e){
        g = getGraphics();
        g2 = (Graphics2D)g;
        if(e.isMetaDown()){
            g2.setColor(getBackground());
            g2.setStroke(new BasicStroke(6));
            g2.drawLine(lineStart.x,lineStart.y, e.getX(),e.getY());
    }
        else{
            g2.setColor(color);
            g2.setStroke(new BasicStroke(penSize));
            g2.drawLine(lineStart.x,lineStart.y,e.getX(), e.getY());
        }
        lineStart.move(e.getX(), e.getY());
        g.dispose();
}
    public void mouseMoved(MouseEvent e){
        
    }
    public void setPenSize(float newPenSize){
        penSize = newPenSize;
    }
    
    public static void main(String[] args) {
       ScribblePanel s = new ScribblePanel(); 
    }
}
 
