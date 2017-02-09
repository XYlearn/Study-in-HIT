package Demo;

import NetEvent.Client;

/**
 * Created by xy16 on 17-2-7.
 */
public class Main {
	public static void main(String[] args) {
		Client client = new Client();
		Thread clientThread = new Thread(client);
		clientThread.start();
	}
}
