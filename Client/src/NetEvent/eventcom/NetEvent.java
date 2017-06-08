package NetEvent.eventcom;

public class NetEvent
{
	protected NetEvent(EventType type) {this.type = type;}

	@Override
	public String toString() {
		return type.getName()+"\n";
	}

	public static enum EventType
	{
		REGISTER_EVENT("RegisterEvent"),
		LAUNCH_EVENT("LaunchEvent"),
		CONTENT_MESSAGE_EVENT("ContentMessageEvent"),
		GOOD_USER_EVENT("GoodUserEvent"),
		GOOD_QUESTION_EVENT("GoodQuestionEvent"),
		ENTER_QUESTION_EVENT("EnterQuestionEvent"),
		QUESTION_INFO_EVENT("QuestionInfoEvent"),
		QUESTION_LIST_EVENT("QuestionListEvent"),
		USER_INFO_EVENT("UserInfoEvent"),
		CREATE_QUESTION_EVENT("CreateQuestionEvent"),
		ABANDON_QUESTION_EVENT("AbandonQuestionEvent"),
		SEARCH_QUESTION_EVENT("SearchQuestionEvent"),
		FILE_EVENT("FileEvent"),
		UPDATE_EVENT("UpdateEvent"),
		SOLVED_QUESTION_EVENT("SolvedQuestionEvent"),
		QUESTION_USER_LIST_EVENT("QuestionUserListEvent"),
		ACQUAINTANCE_USER_LIST_EVENT("AquaintanceUserListEvent"),
		WHITE_BOARD_EVENT("WhiteBoardEvent");

		private String name;

		EventType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
	public EventType type;
}
