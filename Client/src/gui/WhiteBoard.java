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
import com.ClientSendMessage;
import gui.dao.LoginFrame;
import util.Dispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.Border;
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
            client.launchRequest("test", "123456");
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
        else if(e.getSource() == colorChoose.jb2)
            sP1.color = Color.ORANGE;
        else if(e.getSource() == colorChoose.jb3)
            sP1.color = Color.YELLOW;
        else if(e.getSource() == colorChoose.jb4)
            sP1.color = (Color.GREEN);
        else if(e.getSource() == colorChoose.jb5)
            sP1.color = (Color.CYAN);
        else if(e.getSource() == colorChoose.jb6)
            sP1.color = (Color.BLUE);
        else if(e.getSource() == colorChoose.jb7)
            sP1.color = (Color.MAGENTA);
        else if(e.getSource() == colorChoose.jb8)
            sP1.color = (Color.WHITE);
        else if(e.getSource() == colorChoose.jb9)
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

    /*handle net event*/
    public static void dispatch(NetEvent event) {
        ScribblePanel panel = (ScribblePanel) boardMap.get(0);
        WhiteBoardEvent e = (WhiteBoardEvent) event;
        if (e.isCls()) {
            panel.clear(e.getX1(), e.getY1(), e.getX2(), e.getY2());
        } else if(e.isACls()) {
            panel.clearAll();
        } else {
            panel.draw(e.getColor(), e.getStroke(), e.getX1(), e.getY1(), e.getX2(), e.getY2());
        }
    }



    /*inner class*/
    class ScribblePanel extends JPanel
            implements MouseListener,MouseMotionListener{
        final int CIRCLESIZE = 20;
        Color color = Color.BLACK;
        private Point pointStart = new Point(0,0);
        public Graphics graphics;
        public float penSize = 3;

        private long questionID = -1;

        private Image curImage = null;
        private boolean isCls = false;
        private boolean textType = true;
        private boolean repaint = true;

        private Client client;

        public ScribblePanel(Client client){
            addMouseListener(this);
            addMouseMotionListener(this);
            this.client = client;
        }

        public ScribblePanel(Client client, long questionID){
            this(client);
            this.questionID = questionID;
        }

        /*Mouse events*/
        public void mouseClicked(MouseEvent e){
            if(e.isAltDown()) {
                clearAll();
                WhiteBoardEvent event = new WhiteBoardEvent();
                event.setACls(true);
                try {
                    client.whiteBoardMessage(event);
                } catch (IOException ex) {}
            }

        }
        public void mouseEntered(MouseEvent e){
            //flush();
        }
        public void mouseExited(MouseEvent e){

        }
        public void mouseReleased(MouseEvent e){
            if(isCls) {
                clear(pointStart.x, pointStart.y, e.getX(), e.getY());

                WhiteBoardEvent event = new WhiteBoardEvent();
                event.setCls(true);
                event.setX1(pointStart.x);
                event.setY1(pointStart.y);
                event.setX2(e.getX());
                event.setY2(e.getY());
                event.setQuestionID(questionID);
                try {
                    client.whiteBoardMessage(event);
                } catch (IOException ex) {

                }
            }
        }
        public void mousePressed(MouseEvent e) {
            if (e.isAltDown()) {
                pointStart.move(e.getX(), e.getY());
                isCls = true;
            } else {
                pointStart.move(e.getX(), e.getY());
            }
        }

        public void mouseDragged(MouseEvent e){
            if(e.isAltDown()) {

            } else {
                isCls = false;
                Color tempColor = color;
                if (e.isMetaDown())
                    color = getBackground();

                draw(color, penSize, pointStart.x, pointStart.y, e.getX(), e.getY());
                try {
                    WhiteBoardEvent event = new WhiteBoardEvent();
                    event.setX1(pointStart.x);
                    event.setY1(pointStart.y);
                    event.setX2(e.getX());
                    event.setY2(e.getY());
                    event.setColor(color.getRGB());
                    event.setStroke(penSize);
                    event.setQuestionID(questionID);
                    event.setCls(false);
                    event.setACls(false);
                    client.whiteBoardMessage(event);
                } catch (IOException ex) {
                    //exception handle
                    ex.printStackTrace();
                }
                pointStart.move(e.getX(), e.getY());
                color = tempColor;
            }
        }
        public void mouseMoved(MouseEvent e){
            flush();
        }


        /*draw on the panel*/
        private void draw(Color color, float stroke, int x1, int y1, int x2, int y2) {
            graphics = getGraphics();
            Graphics2D g2 = (Graphics2D)graphics;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke));
            g2.drawLine(x1, y1, x2, y2);
            graphics.dispose();

            /*save to curImage*/
            graphics = curImage.getGraphics();
            g2 = (Graphics2D)graphics;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke));
            g2.drawLine(x1, y1, x2, y2);
            graphics.dispose();
        }
        private void draw(int color, float stroke, int x1, int y1, int x2, int y2) {
            draw(new Color(color), stroke, x1, y1, x2, y2);
        }

        /*set a rectangle area to background*/
        private void clear(int x1, int y1, int x2, int y2) {
            graphics = getGraphics();
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setColor(getBackground());
            g2.clearRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2, Math.abs(x1-x2), Math.abs(y2-y1));
            graphics.dispose();

            /*update curImage*/
            graphics = curImage.getGraphics();
            g2 = (Graphics2D) graphics;
            g2.setColor(getBackground());
            g2.clearRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2, Math.abs(x1-x2), Math.abs(y2-y1));
            graphics.dispose();
        }

        private void clearAll() {
            graphics = this.getGraphics();
            graphics.setColor(getBackground());
            graphics.clearRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            graphics.dispose();
            graphics = curImage.getGraphics();
            graphics.setColor(getBackground());
            graphics.clearRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            graphics.dispose();
        }

        void setPenSize(float newPenSize){
            penSize = newPenSize;
        }

        void flush() {
            if(curImage == null) {
                /*initialize bufferedImage*/
                curImage = createImage(getWidth(), getHeight());
                Graphics tempGraphics =  curImage.getGraphics();
                tempGraphics.setColor(getBackground());
                tempGraphics.clearRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
                tempGraphics.dispose();
            }
            graphics = getGraphics();
            graphics.drawImage(curImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

}
