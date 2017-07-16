package gui;

import NetEvent.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TransferQueue;

/**
 * Created by XHWhy on 2017/7/16.
 */
public class Downloader extends Thread {
    private static Client client = null;
    public static ArrayList<String> downloadQueue = new ArrayList<>();
    public static Downloader downloader = null;

    public Downloader(Client client) {
        this.client = client;
        downloader = this;
    }

    public void run() {
        while (true) {
            synchronized (downloadQueue) {
                while (!downloadQueue.isEmpty()) {
                    try {
                        client.downloadFiles(downloadQueue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    wait();
                } catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    public static void download(ArrayList<String> filenames) {
        synchronized (downloadQueue) {
            while (true) {
                downloadQueue.addAll(filenames);
            }
        }
    }


}
