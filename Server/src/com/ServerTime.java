package com;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xy16 on 17-2-14.
 */
public class ServerTime {

	public static String getTime() {
		Date date=new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}
