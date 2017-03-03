package util;

import java.util.ArrayList;

public class questionMessage
{
	private final String id; //题号
	private final String stem; //题干
	private final String addition; //补充
	private final String time; //时间
	private final String owner; //提问者
	private final ArrayList<Record> records; //问答记录
	private final boolean solved; //是否解决
	private final int good; //赞数
	
	public questionMessage(
			String id,
			String stem,
			String addition,
			String time,
			String owner,
			ArrayList<Record> records,
			boolean solved,
			int good)
	{
		this.id=id;
		this.stem=stem;
		this.addition=addition;
		this.time=time;
		this.owner=owner;
		this.records=records;
		this.solved=solved;
		this.good=good;
	}
	
	public questionMessage(
			String id,
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
	}
	
	public String getId()
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

	public ArrayList<Record> getRecords()
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
	
	public void addRecord(String user,String content,String time)
	{
		records.add(new Record(user,content,time));
	}
}
