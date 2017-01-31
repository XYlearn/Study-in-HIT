/**
 * Created by xy16 on 17-1-26.
 */
public class Content {
	enum Type {
		TEXT, FILE
	}
	String str;
	Type type;

	Content(String str, Type type) {
		this.str = str;
		this.type = type;
	}
}
