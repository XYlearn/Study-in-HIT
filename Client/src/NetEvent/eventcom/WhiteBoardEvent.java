package NetEvent.eventcom;

import com.ServerResponseMessage;

/**
 * Created by XHWhy on 2017/6/6.
 */
public class WhiteBoardEvent extends NetEvent{
    private int x1,y1,x2,y2;
    private int color;
    private float stroke;
    private long questionID;
    private boolean isCls;
    private boolean isACls;

    public WhiteBoardEvent(ServerResponseMessage.WhiteBoardMessage message) {
        super(EventType.WHITE_BOARD_EVENT);
        this.x1 = message.getX1();
        this.y1 = message.getY1();
        this.x2 = message.getX2();
        this.y2 = message.getY2();
        this.color = message.getColor();
        this.stroke = message.getStroke();
        this.questionID = message.getQuestionId();
        this.isCls = message.getIsCls();
        this.isACls = message.getIsACls();
    }
    public WhiteBoardEvent() {
        super(EventType.WHITE_BOARD_EVENT);
        this.x1 = 0;
        this.y1 = 0;
        this.x2 = 0;
        this.y2 = 0;
        this.isACls = false;
        this.isCls = false;
        this.questionID = 0;
        this.stroke = 0;
        this.color = 0;
    }

    public int getX1() {return x1;}
    public int getY1() {return y1;}
    public int getX2() {return x2;}
    public int getY2() {return y2;}
    public float getStroke() {return stroke;}
    public int getColor() {return color;}
    public long getQuestionID() {return questionID;}
    public boolean isCls() {return isCls;}
    public boolean isACls() {return isACls;}

    public void setX1(int x1) {this.x1 = x1;}
    public void setX2(int x2) {this.x2 = x2;}
    public void setY1(int y1) {this.y1 = y1;}
    public void setY2(int y2) {this.y2 = y2;}
    public void setColor(int color) {this.color = color;}
    public void setStroke(float stroke) {this.stroke = stroke;}
    public void setCls(boolean isCls) {this.isCls = isCls;}
    public void setACls(boolean isACls) {this.isACls = isACls;}
    public void setQuestionID(long questionID) {this.questionID = questionID;}
}
