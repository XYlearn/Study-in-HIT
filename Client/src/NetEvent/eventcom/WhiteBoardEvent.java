package NetEvent.eventcom;

import com.ServerResponseMessage;
import gui.wb.GraphicPoints;

import java.awt.*;

/**
 * Created by XHWhy on 2017/6/6.
 */
public class WhiteBoardEvent extends NetEvent{
    private int x1,y1,x2,y2;
    private int color;
    private float pensize;
    private long questionID;
    private boolean isCls;
    private boolean isACls;
    private boolean isRefresh;
    private boolean isReceiveImage;
    private GraphicPoints image;

    public WhiteBoardEvent(ServerResponseMessage.WhiteBoardMessage message) {
        super(EventType.WHITE_BOARD_EVENT);
        this.x1 = message.getX1();
        this.y1 = message.getY1();
        this.x2 = message.getX2();
        this.y2 = message.getY2();
        this.color = message.getColor();
        this.pensize = message.getPensize();
        this.questionID = message.getQuestionId();
        this.isCls = message.getIsCls();
        this.isACls = message.getIsACls();
        this.isRefresh = message.getIsRefresh();
        this.isReceiveImage = message.getIsReceiveImage();

        //covert the image to local class
        GraphicPoints graphicPoints = new GraphicPoints();
        for(ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicPoint point : message.getImage().getPointsList()) {
            graphicPoints.addPoint(point.getX1(), point.getY1(), point.getX2(), point.getY2(), point.getPensize(), new Color(point.getColor()));
        }
        for(ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicRect rect : message.getImage().getRectsList()) {
            graphicPoints.addRect(rect.getX1(), rect.getY1(), rect.getX2(), rect.getY2());
        }
        this.image = graphicPoints;
    }
    public WhiteBoardEvent() {
        super(EventType.WHITE_BOARD_EVENT);
        this.x1 = 0;
        this.y1 = 0;
        this.x2 = 0;
        this.y2 = 0;
        this.isACls = false;
        this.isCls = false;
        this.isRefresh = false;
        this.isReceiveImage = false;
        this.questionID = -1;
        this.pensize = 0;
        this.color = 0;
        this.image = null;
    }

    public int getX1() {return x1;}
    public int getY1() {return y1;}
    public int getX2() {return x2;}
    public int getY2() {return y2;}
    public float getPensize() {return pensize;}
    public int getColor() {return color;}
    public long getQuestionID() {return questionID;}
    public boolean isCls() {return isCls;}
    public boolean isACls() {return isACls;}
    public boolean isRefresh() {return isRefresh;}
    public boolean isReceiveImage() {return isReceiveImage;}
    public GraphicPoints getImage() {return image;}

    public void setX1(int x1) {this.x1 = x1;}
    public void setX2(int x2) {this.x2 = x2;}
    public void setY1(int y1) {this.y1 = y1;}
    public void setY2(int y2) {this.y2 = y2;}
    public void setColor(int color) {this.color = color;}
    public void setPensize(float pensize) {this.pensize = pensize;}
    public void setCls(boolean isCls) {this.isCls = isCls;}
    public void setACls(boolean isACls) {this.isACls = isACls;}
    public void setQuestionID(long questionID) {this.questionID = questionID;}
    public void setRefresh(boolean isRefresh) {this.isRefresh = isRefresh;}
    public void setReceiveImage(boolean isReceiveImage) {this.isReceiveImage = isReceiveImage;}
    public void setImage(GraphicPoints image) {this.image = image;}
}
