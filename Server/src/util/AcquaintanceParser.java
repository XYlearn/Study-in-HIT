package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy16 on 17-3-30.
 */
public class AcquaintanceParser {
	public static List<String> parse(String str) {
		List<String> acquaintanceList = new ArrayList<>();
		if(str.equals("")) {
			return acquaintanceList;
		}

		for ( String acq : str.split(";")) {
			acquaintanceList.add(acq);
		}
		return acquaintanceList;
	}
}
