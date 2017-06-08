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

    public WhiteBoardEvent(ServerResponseMessage.WhiteBoardMessage message) {
        super(EventType.WHITE_BOARD_EVENT);
        this.x1 = message.getX1();
        this.y1 = message.getY1();
        this.x2 = message.getX2();
        this.y2 = message.getY2();
        this.color = message.getColor();
        this.stroke = message.getStroke();
        this.questionID = message.getQuestionId();
    }

    public int getX1() {return x1;}
    public int getY1() {return y1;}
    public int getX2() {return x2;}
    public int getY2() {return y2;}
    public float getStroke() {return stroke;}
    public int getColor() {return color;}
    public long getQuestionID() {return questionID;}
}
