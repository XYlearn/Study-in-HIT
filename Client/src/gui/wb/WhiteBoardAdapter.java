package gui.wb;

import gui.wb.WhiteBoard;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WhiteBoardAdapter extends ComponentAdapter {

    public WhiteBoardAdapter(JFrame frame) {

    }

    @Override
    public void componentResized(ComponentEvent e) {
        for (WhiteBoard.ScribblePanel panel : WhiteBoard.panelList) {
            panel.repaintThread = new RepaintThread(panel);
            panel.repaintThread.start();
                //panel.refresh();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        for (WhiteBoard.ScribblePanel panel : WhiteBoard.panelList) {
            panel.repaintThread = new RepaintThread(panel);

            panel.repaintThread.start();

            //panel.refresh();
        }
    }


    public class RepaintThread extends Thread {
        private WhiteBoard.ScribblePanel panel;
        public RepaintThread(WhiteBoard.ScribblePanel panel) {
            this.panel = panel;
        }
        public void run() {
            panel.refresh();
        }
    }
}