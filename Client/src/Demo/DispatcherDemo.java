package Demo;

import NetEvent.eventcom.ContentMessageEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.SolvedQuestionEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import util.Dispatcher;

//具有事件收发功能的类应实现Dispatcher接口，即实现一个dispatch(NetEvent)方法
public class DispatcherDemo implements Dispatcher
{
	//监听者列表
	//例如聊天框在服务器发来消息时接受之
	//map作为一种对应关系，用来在事件分发时根据事件内容准确找到应该接收事件的对象
	//这种对应关系依靠绑定（下面的bind()函数）来完成
	//ConcurrentHashMap是一个线程安全的实现了Map接口的类，
	//可以在多个请求同时到达时也不会出现数据出错的情况
	private static final Map<Long,DispatcherDemo> map=new ConcurrentHashMap<Long,DispatcherDemo>();
	
	//绑定所得id，不一定是long类型，此处仅为举例
	private long id=-1;
	
	//类中的某个能发出数据请求的方法
	public void requestSomething()
	{
		//此函数用于向服务器请求数据
		//由于client中实际不存在requestSomething()方法，故为了能够编译，此处注释
		//test.client.requestSomething();
	}
	
	//绑定方法，这个方法将当前对象与所给的某个值进行绑定，储存在map里
	public void bind(long id)
	{
		map.put(id, this);
		//储存所绑定ID
		this.id=id;
	}
	
	public void unbind(long id)
	{
		//取消绑定
		map.remove(id);
	}
	
	//外部对象可能会需要得到当前对象的id，因此设置getter方法
	public long getID()
	{
		return id;
	}
	
	//事件分发，参数为接收到的事件
	public static void dispatch(NetEvent e)
	{
		//判断事件类型
		switch(e.type)
		{
			case SOLVED_QUESTION_EVENT:
			{
				//向下转换
				SolvedQuestionEvent ex=(SolvedQuestionEvent)e;
				//根据收到的事件内容，选取适当的监听器对象进行操作
				//此处为根据绑定的问题ID选取对应的类进行操作
				map.get(ex.getQuestionID()).doSomething();
				break;
			}
		}
	}
	
	//类中的其他方法，此处为示例
	public void doSomething(){}
}
