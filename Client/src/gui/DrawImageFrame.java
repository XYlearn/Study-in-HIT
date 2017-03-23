/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;
import static bin.test.PICTPATH;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.io.*;
/**
 *
 * @author zhaowei
 */
//存留问题1.按键选中状态 2.图片外画矩形需要clear
//model == 0 旋转
//model == 1 截图
//model == 2 绘画
//model == 3 撤回
public class DrawImageFrame extends JFrame 
    implements ActionListener{
    //数据域
    JPanel mypanel = new JPanel();
    ColorPanel colorChoose = new ColorPanel();
    JButton antiClockWise = new JButton("逆时针"),clockWise = new JButton("顺时针"),
            jbScreenCapture = new JButton("裁剪"),withDraw = new JButton("撤回"),
            jbPen = new JButton("画图");
    BufferedImage image;
    Dimension frameSize = Toolkit.getDefaultToolkit().getScreenSize();
    MyCanvas mc = new MyCanvas(frameSize);
    //方法
    //构造方法
    public DrawImageFrame() throws IOException{
        this.image = ImageIO.read(new File(PICTPATH+"智障.jpg"));
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
        jbPen.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(mc.model != 2)
                mc.model = 2;
                else if(mc.model == 2)
                    mc.model = 0;
            }
        });
        withDraw.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mc.model = 3;
                mc.repaint();
         }
      
        });
        jbScreenCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mc.model != 1)
                    mc.model = 1;
                else if(mc.model == 1)
                    mc.model = 0;
            }
        });
        antiClockWise.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent e){
                if(mc.model ==0){
                mc.setRadian(-1);
                mc.radian %= 4;
                mc.repaint();
                }
            }
        });
        clockWise.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent e){
               if(mc.model ==0){
               mc.setRadian(1);
               mc.radian %= 4;
               mc.repaint();
               }
            }
        });
        addMouseWheelListener(new MouseWheelListener(){
            public void mouseWheelMoved(MouseWheelEvent e){
                if(mc.model != 1){
                if(e.getWheelRotation() < 0){
                    mc.enlargeImg();
                     }
                else{
                mc.reduceImg();
                    }
                }
            }
        });
        mc.addMouseListener(mc);
        mc.addMouseMotionListener(mc);
        mypanel.setLayout(new GridLayout(0,1,0,0));
        mypanel.add(antiClockWise);
        mypanel.add(clockWise);
        mypanel.add(withDraw);
        mypanel.add(jbScreenCapture);
        mypanel.add(jbPen);
        this.setLayout(new BorderLayout());
        this.add(mc,BorderLayout.CENTER);
        this.add(mypanel,BorderLayout.WEST);
        this.add(colorChoose,BorderLayout.EAST);
        mc.setImage(image);
        mc.setWithDrawIn(image);
    }
    public void actionPerformed(ActionEvent e) {
         if(e.getSource() == colorChoose.jb1)
           mc.color = Color.RED;
        if(e.getSource() == colorChoose.jb2)
            mc.color = Color.ORANGE;
        if(e.getSource() == colorChoose.jb3)
            mc.color = Color.YELLOW;
        if(e.getSource() == colorChoose.jb4)
            mc.color = (Color.GREEN);
        if(e.getSource() == colorChoose.jb5)
            mc.color = (Color.CYAN);
        if(e.getSource() == colorChoose.jb6)
            mc.color = (Color.BLUE);
        if(e.getSource() == colorChoose.jb7)
            mc.color = (Color.MAGENTA);
        if(e.getSource() == colorChoose.jb8)
            mc.color = (Color.WHITE);
        if(e.getSource() == colorChoose.jb9)
            mc.color = (Color.BLACK);
        
    }
}
class MyCanvas extends Canvas 
                implements MouseListener ,MouseMotionListener{
    //数据域
    final int CIRCLESIZE = 20;
    Color color = Color.BLACK;
    private Point lineStart = new Point(0,0);
    public Graphics g;
    public float penSize = 3;
     public ImageIcon ii = new ImageIcon(PICTPATH+"智障.jpg");
     public int radian = 0;    
     public int width = ii.getIconWidth(),height = ii.getIconHeight();
     public int x,y;
     public double absoluteWidth = ii.getIconWidth(),absoluteHeight = ii.getIconHeight();
     public int model = 0;
     private Dimension  frameSize;
     public int pressX,pressY,releaseX,releaseY,draggedX,draggedY;
     public int pressXR,pressYR,releaseXR,releaseYR;
     public BufferedImage buffImage,in,withDrawIn;
     public Graphics2D g2;
     public Rectangle rectangle = new Rectangle();
     public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     float[] arr = {5.0f};
     public BasicStroke stroke = new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,arr,0);
     //函数区域
     //1构造方法
      public MyCanvas(Dimension fs){
         frameSize = fs;
     }
     //2设置撤回图片
     public void setWithDrawIn(BufferedImage widthDrawIn){
         this.withDrawIn = widthDrawIn;
     }
     //设置图片绘画位置
     public void setImgpos(){
         x = (int)(frameSize.getWidth()-width)/2;
         y = (int)(frameSize.getHeight()-height)/2;
     }
     //设置图片
    public void setImage(BufferedImage in){
        this.in=in;
        this.buffImage = in;
     }
    //返回旋转角度
     public double getRadian(){
         return radian;
     }
     //设置旋转角度
     public void setRadian(double radian){
         this.radian += radian;
     }
     //绘画
     public void paint(Graphics g){

         g2 = (Graphics2D)g;
         g2.rotate(radian*Math.PI/2);
         if(radian == -1 || radian ==3)
         g2.translate(-width,0);
         else if(radian == -2 ||radian ==2)
         g2.translate(-width, -height);
         else if(radian == -3 || radian == 1)
         g2.translate(0, -height);
         if(model == 1){
             g2.drawImage(in,x,y,width,height,this);
             rectangle = new Rectangle(Math.min(releaseX, pressX),Math.min(releaseY, pressY),Math.abs(releaseX - pressX),Math.abs(releaseY - pressY));
             try {
                 buffImage = (new Robot().createScreenCapture(rectangle));
             } catch (AWTException ex) {
                 Logger.getLogger(MyCanvas.class.getName()).log(Level.SEVERE, null, ex);
             }
             int p = 1;
             if(Math.abs(releaseX - pressX)< 0.5*width ||Math.abs(releaseY - pressY)< 0.5*height)
             p = 2;
             absoluteWidth = width = Math.abs(releaseX - pressX)*p;
             absoluteHeight = height = Math.abs(releaseY - pressY)*p;
             withDrawIn = new BufferedImage(in.getWidth(),in.getHeight(),in.getType());
             withDrawIn.setData(in.getData());
             in  = buffImage;
             g2.clearRect(0, 0, (int)screenSize.getWidth()/2,(int)screenSize.getHeight()*3/4);
         }
         if(model == 3){
         in = new BufferedImage(withDrawIn.getWidth(),withDrawIn.getWidth(),withDrawIn.getType());
             in.setData(withDrawIn.getData());
             absoluteWidth = width = withDrawIn.getWidth();
             absoluteHeight = height = withDrawIn.getWidth();
             g2.drawImage(in,x,y,width,height,this);
             model = 1;
         }
         g2.drawImage(in,x,y,width,height,this);
     }    
     //缩小图片
     public void reduceImg(){
         if(width <= absoluteWidth / 5){
             return;
         }
         if(height <= absoluteWidth / 5){
             return;
         }
         width -= absoluteWidth/10;
         height -= absoluteWidth/10;
         repaint();
     }
     //放大图片
     public void enlargeImg(){
         if(width >= absoluteHeight*2){
             return;
         }
         if(height >= absoluteHeight*2){
             return;
         }
         width += absoluteWidth / 10;
         height += absoluteWidth / 10;
         repaint();
     }
     //鼠标监听器抽象方法
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(model == 1){
        pressXR = e.getX();
        pressYR = e.getY();
        pressX = e.getXOnScreen();
        pressY = e.getYOnScreen();
        }
        else if(model == 2){
            lineStart.move(e.getX(),e.getY());
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(model ==1){
        pressXR = e.getX();
        releaseYR = e.getY();
        releaseX = e.getXOnScreen();
        releaseY = e.getYOnScreen();
        repaint();
        }
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(model == 1){
        Graphics g = getGraphics();
  
        g2 = (Graphics2D)g;
        g2.setStroke(stroke);
        g2.setColor(getBackground());
        draggedX = e.getX();
        draggedY = e.getY();
        g2.drawImage(in,x,y,(int)width,(int)height,this);
        g2.drawRect(Math.min(pressXR,draggedX), Math.min(pressYR,draggedY), Math.abs(draggedX - pressXR), Math.abs(draggedY - pressYR));
        }
        else if(model == 2){
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
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
class ColorPanel extends JPanel{
    
     JButton jb1 = new JButton("red"),jb2 = new JButton("orange"),jb3 = new JButton("yellow"),
                   jb4 = new JButton("green"),jb5 = new JButton("cyan"),jb6 = new JButton("blue"),
                   jb7 = new JButton("purple"),jb8 = new JButton("white"),jb9 = new JButton("black");
     ColorPanel(){
         jb1.setBackground(Color.red);
           jb2.setBackground(Color.ORANGE);
           jb3.setBackground(Color.YELLOW);
           jb4.setBackground(Color.GREEN);
           jb5.setBackground(Color.CYAN);
           jb6.setBackground(Color.BLUE);
           jb7.setBackground(Color.MAGENTA);
           jb8.setBackground(Color.WHITE);
           jb9.setBackground(Color.BLACK);
           jb1.setForeground(Color.WHITE);
           jb2.setForeground(Color.WHITE);
           jb3.setForeground(Color.WHITE);
           jb4.setForeground(Color.WHITE);
           jb5.setForeground(Color.WHITE);
           jb6.setForeground(Color.WHITE);
           jb7.setForeground(Color.WHITE);
           jb8.setForeground(Color.BLACK);
           jb9.setForeground(Color.WHITE);
           this.add(jb1);
           this.add(jb2);
           this.add(jb3);
           this.add(jb4);
           this.add(jb5);
           this.add(jb6);
           this.add(jb7);
           this.add(jb8);
           this.add(jb9);
     }
}
