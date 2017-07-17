package com;

import com.ServerHandler;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.mina.core.session.IoSession;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XHWhy on 2017/7/15.
 */
public class HeartBeatHandler extends Thread {
    public static Map<IoSession, Boolean> session_online_map = new HashMap<>();
    private Timer timer;

    HeartBeatHandler() {
        int delay = 30000;
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<IoSession> sessions_to_remove = new ArrayList<>();
                for (Map.Entry<IoSession, Boolean> entry : session_online_map.entrySet()) {
                    if(entry.getValue() == false) {
                        sessions_to_remove.add(entry.getKey());
                        ServerHandler.sessionShut(entry.getKey());
                    }
                    entry.setValue(false);
                }
                for(IoSession session : sessions_to_remove) {
                    session_online_map.remove(session);
                }
            }
        });
    }

    public void run() {
        timer.start();
    }
}
