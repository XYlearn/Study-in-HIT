package NetEvent.messagecom;

import com.ServerResponseMessage;

import java.util.ArrayList;
import java.util.List;

public class QuestionMessage
{
	private final long id; //题号
	private final String stem; //题干
	private final String addition; //补充
	private final String time; //时间
	private final String owner; //提问者
	private final List<Record> records; //问答记录
	private final boolean solved; //是否解决
	private final int good; //
	private final List<String> stempic;
	private final List<String> additionpic;

	public QuestionMessage(
			  long id,
			  String stem,
			  String addition,
			  String time,
			  String owner,
			  List<Record> records,
			  boolean solved,
			  int good,
			  List stempic,
			  List additionpic)
	{
		this.id=id;
		this.stem=stem;
		this.addition=addition;
		this.time=time;
		this.owner=owner;
		this.records=records;
		this.solved=solved;
		this.good=good;
		this.stempic = stempic;
		this.additionpic = additionpic;
	}

	public QuestionMessage(
			  long id,
			  String stem,
			  String addition,
			  String time,
			  String owner,
			  boolean solved,
			  int good)
	{
		this.id=id;
		this.stem=stem;
		this.addition=addition;
		this.time=time;
		this.owner=owner;
		records=new ArrayList<>();
		this.solved=solved;
		this.good=good;
		this.stempic = new ArrayList<>();
		this.additionpic = new ArrayList<>();
	}

	public QuestionMessage(ServerResponseMessage.QuestionMessage questionMessage) {
		this.id = questionMessage.getId();
		this.stem = questionMessage.getStem();
		this.stempic =  new ArrayList<>();
		this.addition = questionMessage.getAddition();
		this.additionpic = new ArrayList<>();
		this.time = questionMessage.getTime();
		this.owner = questionMessage.getOwner();
		this.records = new ArrayList<>();
		this.solved = questionMessage.getSolved();
		this.good = questionMessage.getGood();
		for (ServerResponseMessage.Record record : questionMessage.getRecordList()) {
			this.records.add(new Record(record));
		}
		for(String s : questionMessage.getStempicList()) {
			stempic.add(s);
		}
		for(String s : questionMessage.getAdditionpicList()) {
			additionpic.add(s);
		}
	}

	public long getId()
	{
		return id;
	}

	public String getStem()
	{
		return stem;
	}

	public String getAddition()
	{
		return addition;
	}

	public String getTime()
	{
		return time;
	}

	public String getOwner()
	{
		return owner;
	}

	public List<Record> getRecords()
	{
		return records;
	}

	public boolean isSolved()
	{
		return solved;
	}

	public int getGood()
	{
		return good;
	}

	public Record getRecordAt(int index)
	{
		return records.get(index);
	}

	public void addRecord(Record record)
	{
		records.add(record);
	}

	public List<String> getStempic() {return this.stempic;}

	public List<String> getAdditionpic() {return this.additionpic;}

}
