package NetEvent;

import com.ClientSendMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;

/**
 * Created by XHWhy on 2017/7/15.
 */
public class HeartBeat extends Thread{

    Client client;
    private Timer timer;
    private final int t = 15000;
    HeartBeat(Client client) {
        this.client = client;
        this.timer = new Timer(t, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try {
                    client.sendIt(
                            ClientSendMessage.Message.newBuilder()
                                    .setMsgType(ClientSendMessage.MSG.HEART_BEAT)
                                    .setUsername(client.getName())
                                    .setHeartBeat(ClientSendMessage.HeartBeat.newBuilder().setOnline(true))
                                    .build()
                    );
                } catch (IOException ex) {

                }
            }
        });
    }

    public void run() {
        timer.start();
    }
}
