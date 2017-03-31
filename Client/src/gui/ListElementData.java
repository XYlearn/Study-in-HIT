package gui;

public class ListElementData
{
	public long questionID;
	public String askTime;
	public String username;
	public String pictname;
	public String stem;
	public String addition;
	public int good;
	public int usernum;
	public String lastTime;
	
	public ListElementData(long questionID, String askTime, String username, String pictname,String stem, String addition, int good, int usernum, String lastTime)
	{
		this.questionID=questionID;
		this.askTime=askTime;
		this.username=username;
		this.pictname=pictname;
		this.stem=stem;
		this.addition=addition;
		this.good=good;
		this.usernum=usernum;
		this.lastTime=lastTime;
	}
}
