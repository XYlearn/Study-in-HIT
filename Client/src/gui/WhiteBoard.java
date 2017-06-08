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
import NetEvent.Client;
import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.WhiteBoardEvent;
import gui.dao.LoginFrame;
import util.Dispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.*;
/**
 *
 * @author zhaowei
 */
public class WhiteBoard extends JPanel
        implements ActionListener,ChangeListener, Dispatcher{

    /*test function*/
    public static void main(String[] args) {
        Client client = new Client();
        client.start();

        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame();
        frame.add(new WhiteBoard(client));
        frame.setTitle("White Board");
        frame.setSize(screenSize.width/2, screenSize.height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocation(screenSize.width/4, screenSize.height/4);

        try {
            client.launchRequest("xy16", "123456");
        } catch (Exception e) {}
    }


    ColorPanel colorChoose = new ColorPanel();
    ScribblePanel sP1;
    private JSlider jsldVert = new JSlider(JSlider.VERTICAL,0,20,4);
    private Client client = null;
    private static Map boardMap = new HashMap();
    private static int num = 0;
    
       public WhiteBoard(Client client){
           this.setLayout(new BorderLayout());
           sP1 = new ScribblePanel(client);
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
           this.add(sP1,BorderLayout.CENTER);
           this.add(colorChoose,BorderLayout.EAST);
           this.add(jsldVert,BorderLayout.WEST);
           jsldVert.addChangeListener(this);
           boardMap.put(num++, this.sP1);
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

    public static void dispatch(NetEvent event) {
        ScribblePanel panel = (ScribblePanel) boardMap.get(0);
        WhiteBoardEvent e = (WhiteBoardEvent) event;
        panel.draw(e.getColor(), e.getStroke(), e.getX1(), e.getY1(), e.getX2(), e.getY2());
    }



    /*inner class*/
    class ScribblePanel extends JPanel
            implements MouseListener,MouseMotionListener{
        final int CIRCLESIZE = 20;
        Color color = Color.BLACK;
        private Point lineStart = new Point(0,0);
        public Graphics g;
        public Graphics2D g2;
        public float penSize = 3;
        private Client client;

        public ScribblePanel(Client client){
            addMouseListener(this);
            addMouseMotionListener(this);
            this.client = client;
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
            Color tempColor = color;
            if(e.isMetaDown())
                color = getBackground();

            draw(color, penSize, lineStart.x, lineStart.y, e.getX(), e.getY());
            try {
                client.whiteBoardMessage(lineStart.x, lineStart.y, e.getX(), e.getY(), color.getRGB(), penSize, 30);
            } catch (IOException ex) {
                //exception handle
                ex.printStackTrace();
            }
            lineStart.move(e.getX(), e.getY());
            color = tempColor;
        }
        public void mouseMoved(MouseEvent e){

        }

        private void draw(Color color, float stroke, int x1, int y1, int x2, int y2) {
            g = getGraphics();
            g2 = (Graphics2D)g;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke));
            g2.drawLine(x1, y1, x2, y2);
            g.dispose();
        }
        private void draw(int color, float stroke, int x1, int y1, int x2, int y2) {
            draw(new Color(color), stroke, x1, y1, x2, y2);
        }

        void setPenSize(float newPenSize){
            penSize = newPenSize;
        }


    }

}
