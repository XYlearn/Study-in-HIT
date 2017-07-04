package NetEvent.eventcom;

import com.ServerResponseMessage;

import java.awt.*;

/**
 * Created by XHWhy on 2017/6/13.
 */
public class UpdateEvent extends NetEvent {
    private String username;
    private long questionID;

    public UpdateEvent(ServerResponseMessage.UpdateMessage message) {
        super(EventType.UPDATE_EVENT);
        ServerResponseMessage.UpdateMessage.UserEnter userEnter = message.getUserEnter();
        username = userEnter.getUsername();
        questionID=  userEnter.getQuestionID();
    }

    public long getQuestionID() {return questionID;}
    public String getUsername() {return username;}

}
