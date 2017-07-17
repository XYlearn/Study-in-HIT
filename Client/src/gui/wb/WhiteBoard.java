/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.wb;

/**
 *
 * @author zhaowei
 */
import NetEvent.Client;
import NetEvent.eventcom.EnterQuestionEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.WhiteBoardEvent;
import util.Dispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.swing.event.*;
/**
 *
 * @author zhaowei
 */
public class WhiteBoard extends JPanel
        implements ActionListener,ChangeListener{

    ColorPanel colorChoose = new ColorPanel();
    ScribblePanel sP1;
    JPanel canvasPanel;
    CardLayout card;
    private JSlider jsldVert = new JSlider(JSlider.VERTICAL,0,20,4);
    private Client client = null;
    public static WhiteBoard whiteBoard;
    public static ArrayList<ScribblePanel> panelList = new ArrayList<>();
    public static ArrayList<Long> questionIDs = new ArrayList<>();
    private static int num = 0;
    
    public WhiteBoard(Client client){
        this.client = client;
        this.setLayout(new BorderLayout());
        /*设置画布*/
        card = new CardLayout(0,0);
        canvasPanel = new JPanel(card);

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
        addPanel(0);
        sP1 = panelList.get(0);
        this.add(canvasPanel,BorderLayout.CENTER);
        this.add(colorChoose,BorderLayout.EAST);
        this.add(jsldVert,BorderLayout.WEST);
        jsldVert.addChangeListener(this);

        whiteBoard = this;
       }

    public void choosePanel(long questionID) {
        if(!questionIDs.contains(questionID)) {
            sP1.refresh();
            return;
        }
        card.show(canvasPanel, String.valueOf(questionID));
        sP1 = panelList.get(questionIDs.indexOf(questionID));
        sP1.refresh();
    }

    public ScribblePanel addPanel(long questionID) {
        if(questionIDs.contains(questionID)) {
            return panelList.get(questionIDs.indexOf(questionID));
        }
        ScribblePanel panel = new ScribblePanel(client, questionID);
        panelList.add(panel);
        questionIDs.add(questionID);
        canvasPanel.add(panel, String.valueOf(questionID));

        return panel;
    }

    public void removePanel(long questionID) {
        if(questionID == 0) return;
        if(questionIDs.contains(questionID)) {
            ScribblePanel panel = panelList.get(questionIDs.indexOf(questionID));
            canvasPanel.remove(panel);
            panelList.remove(panel);
            questionIDs.remove(questionIDs.indexOf(questionID));
        }
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

    @Override
    public void stateChanged(ChangeEvent e) {
        double value = jsldVert.getValue();
        double maximumValue = jsldVert.getMaximum();
        float newPenSize = (float)(15*value/maximumValue);
        sP1.setPenSize(newPenSize);
    }

    /*画板*/
    public class ScribblePanel extends JPanel
            implements MouseListener,MouseMotionListener{
        final int CIRCLESIZE = 20;
        Color color = Color.BLACK;
        private Point pointStart = new Point(0,0);
        public Graphics graphics;
        public float penSize = 3;
        private long questionID = -1;

        public Thread repaintThread = null;

        private GraphicPoints curImage = new GraphicPoints(this);
        private boolean isCls = false;
        private float penfix = 1;  //适应窗口的画笔粗细变化系数

        private Client client;

        public ScribblePanel(Client client, Long questionID){
            addMouseListener(this);
            addMouseMotionListener(this);
            this.client = client;
            this.questionID = questionID;
        }

        public void setCurImage(GraphicPoints image) {this.curImage = image;}
        public float getPenfix() {return this.penfix;}

        /*Mouse events*/
        public void mouseClicked(MouseEvent e){
            if(e.isAltDown()) {
                clearAll();
                WhiteBoardEvent event = new WhiteBoardEvent();
                event.setACls(true);
                event.setQuestionID(questionID);
                /*reset the curImage to blank*/
                try {
                    client.whiteBoardMessage(event);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
        public void mouseEntered(MouseEvent e){
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

                draw(color, penSize*penfix, pointStart.x, pointStart.y, e.getX(), e.getY());
                try {
                    //network message
                    WhiteBoardEvent event = new WhiteBoardEvent();
                    event.setX1(pointStart.x);
                    event.setY1(pointStart.y);
                    event.setX2(e.getX());
                    event.setY2(e.getY());
                    event.setColor(color.getRGB());
                    event.setPensize(penSize);
                    event.setQuestionID(questionID);
                    event.setCls(false);
                    event.setACls(false);
                    event.setRefresh(false);
                    event.setReceiveImage(false);
                    event.setImage(new GraphicPoints());
                    client.whiteBoardMessage(event);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                pointStart.move(e.getX(), e.getY());
                color = tempColor;
            }
        }
        public void mouseMoved(MouseEvent e){
            //refresh();
        }

        /*画线、点*/
        public void draw(Color color, float stroke, int x1, int y1, int x2, int y2) {
            graphics = getGraphics();
            Graphics2D g2 = (Graphics2D)graphics;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke));
            g2.drawLine(x1, y1, x2, y2);
            graphics.dispose();

            /*save to curImage*/
            curImage.addPoint(x1, y1, x2, y2, penSize, color);
        }
        public void draw(int color, float stroke, int x1, int y1, int x2, int y2) {
            draw(new Color(color), stroke, x1, y1, x2, y2);
        }

        /*清除区域内容*/
        public void clear(int x1, int y1, int x2, int y2) {
            graphics = getGraphics();
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setColor(getBackground());
            g2.clearRect(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2, Math.abs(x1-x2), Math.abs(y2-y1));
            graphics.dispose();

            /*update curImage*/
            curImage.clear(x1, y1, x2, y2);
        }

        /*清除整张图片内容*/
        public void clearAll() {
            graphics = this.getGraphics();
            graphics.setColor(getBackground());
            graphics.clearRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            graphics.dispose();

            curImage.clearAll();
        }

        /*设置画笔粗细*/
        public void setPenSize(float newPenSize){
            penSize = newPenSize;
        }

        /*刷新*/
        public void refresh() {
            curImage.draw((Graphics2D) getGraphics(), penfix);
        }

    }

}
