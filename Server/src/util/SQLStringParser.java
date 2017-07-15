package util;

/**
 * Created by xy16 on 17-4-4.
 */
public class SQLStringParser {
	public static String parse(String str) {
		return str.replaceAll("'","\\'");
	}
}
