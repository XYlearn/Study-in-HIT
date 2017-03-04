package NetEvent;

public class NetEvent
{
	public static enum EventType
	{
		REGISTER_RESPONSE,
		LAUNCH_RESPONSE,
		SEND_CONTENT_RESPONSE,
		GOOD_USER_RESPONSE,
		GOOD_QUESTION_RESPONSE,
		ENTER_QUESTION_RESPONSE,
		QUESTION_INFO_RESPONSE,
		QUESTION_LIST_RESPONSE,
		CREATE_QUESTION_RESPONSE,
		FILE_RESPONSE,
		UPDATE_MESSAGE,
		SOLVED_QUESTION_RESPONSE
	}
	public EventType type;
}
