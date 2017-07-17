package NetEvent.dispatcher;

import NetEvent.eventcom.FileEvent;
import NetEvent.eventcom.NetEvent;
import gui.ChattingBox;
import util.Dispatcher;

/**
 * Created by XHWhy on 2017/7/9.
 */
public class FileTransDispatcher implements Dispatcher {

	public static void dispatch(NetEvent e) {
		FileEvent event = (FileEvent) e;
		System.out.println("dispatched");
		System.out.println(event.getAllFileName());
		System.out.println(event.getAllSuccess());
		System.out.println(event.getAllRetCode());
		ChattingBox.dispatch(e);
	}
}
