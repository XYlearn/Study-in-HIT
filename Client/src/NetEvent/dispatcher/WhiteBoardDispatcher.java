package NetEvent.dispatcher;

import NetEvent.eventcom.EnterQuestionEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.WhiteBoardEvent;
import gui.wb.WhiteBoard;
import util.Dispatcher;

/**
 * Created by XHWhy on 2017/7/4.
 */
public class WhiteBoardDispatcher implements Dispatcher{
    public static void dispatch(NetEvent event) {

        if(event.type == NetEvent.EventType.WHITE_BOARD_EVENT) {
            WhiteBoardEvent e = (WhiteBoardEvent) event;
            WhiteBoard.ScribblePanel panel = WhiteBoard.panelList
                    .get(WhiteBoard.questionIDs.indexOf(((WhiteBoardEvent) event).getQuestionID()));

            if (e.isRefresh()) {
                panel.refresh();
            } else if (e.isCls()) {
                panel.clear(e.getX1(), e.getY1(), e.getX2(), e.getY2());
            } else if (e.isACls()) {
                panel.clearAll();
            } else if (e.isReceiveImage()) {
                panel.setCurImage(((WhiteBoardEvent) event).getImage());
                panel.refresh();
            } else {
                panel.draw(e.getColor(), e.getPensize() * panel.getPenfix(),
                        e.getX1(), e.getY1(), e.getX2(), e.getY2());
            }
        }
        //进入问题房间后更新面板
        else if(event.type == NetEvent.EventType.ENTER_QUESTION_EVENT) {
            EnterQuestionEvent e = (EnterQuestionEvent) event;
            if(e.isSuccess()) {
                long questionID = e.getQuestionMessage().getId();
                WhiteBoard whiteBoard = WhiteBoard.whiteBoard;
                if(!WhiteBoard.questionIDs.contains(questionID)) {
                    whiteBoard.addPanel(questionID);
                }
                whiteBoard.choosePanel(questionID);
            }
        }
    }
}
