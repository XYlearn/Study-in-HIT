package com;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xy16 on 17-2-14.
 */
public class ServerTime {
	private Date date;
	ServerTime() {
		date = new Date();
	}

	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}
