package com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by xy16 on 17-2-14.
 */
public class ServerLogger {
	private File logFile;
	private PrintStream printStream;
	public ServerLogger() {
		try {
			logFile = new File("sihLOG");
			if(!logFile.exists())
				logFile.createNewFile();
			printStream = new PrintStream(new FileOutputStream(logFile, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void write(String log) {
		PrintStream temp = System.out;
		System.out.println(log);
		System.setOut(printStream);
		System.out.println(log);
		System.setOut(temp);
	}
}
