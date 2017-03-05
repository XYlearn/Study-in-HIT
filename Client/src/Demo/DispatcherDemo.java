package Demo;

import NetEvent.eventcom.ContentMessageEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.SolvedQuestionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import util.Dispatcher;

//具有事件收发功能的类应实现Dispatcher接口，即实现一个dispatch(NetEvent)方法
public class DispatcherDemo implements Dispatcher
{
	//监听队列，用于发送请求后接受反馈信息
	//例如搜索框A发送搜索请求后将自身加入队列，等待搜索结果到达
	private static final Queue<DispatcherDemo> listenerQueue=new LinkedList<DispatcherDemo>();
	
	//监听者列表，用于无请求直接接受服务器发来的消息
	//例如聊天框在服务器发来新消息时接受之
	//map作为一种对应关系，用来在事件分发时根据事件内容准确找到应该接收事件的对象
	//这种对应关系依靠绑定（下面的bind()函数）来完成
	private static final Map<Long,DispatcherDemo> map=new HashMap<Long,DispatcherDemo>();
	
	//绑定所得id，不一定是long类型，此处仅为举例
	private long id=-1;
	
	//类中的某个能发出数据请求的方法
	public void requestSomething()
	{
		//由于监听队列要求收发事件顺序与队列中元素顺序完全相同
		//因此此处利用synchronized获得listenerQueue的对象锁
		//保证同一时间只有一个线程在向监听队列添加监听器
		//防止几个对象同时添加监听器造成顺序混乱
		synchronized(listenerQueue)
		{
			//将自身添加至监听队列，以便在收到反馈后进行下一步行为
			listenerQueue.add(this);
			
			//向服务器请求数据
			//由于client中实际不存在requestSomething()方法，故为了能够编译，此处注释
			//test.client.requestSomething();
		}
	}
	
	//绑定方法，这个方法将当前对象与所给的某个值进行绑定，储存在map里
	public void bind(long id)
	{
		map.put(id, this);
		//储存所绑定ID
		this.id=id;
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
			//假设下面的事件为请求所得
			case CONTENT_MESSAGE_EVENT:
			{
				//判断出事件类型后，将收到的事件对象e强制向下转换为其实际类型
				ContentMessageEvent ex=(ContentMessageEvent)e;
				//根据收到的事件，取出监听队列的队首对象进行操作
				listenerQueue.peek().doSomething();
				//处理完事件后使监听器出队
				listenerQueue.poll();
				break;
			}
			//假设下面的事件为服务器发送得来，没有请求
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
