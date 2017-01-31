import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by xy16 on 17-1-22.
 */
public class Test {
	public static void main(String[] args) {
		Socket socket = null;
		ProjectClientProto proto = null;
		try {
			socket = new Socket("127.0.0.1", 6666);
			proto = new ProjectClientProto(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scanner scanner = new Scanner(System.in);
		boolean islaunched = false;

		ProjectClientProto.LaunchResponse launchResponse = null;
		int choose;

		String user, questionID, text, stem, addition;
		ProjectClientProto.QuestionMessage questionMessage = null;

			//登录
			System.out.print("请输入用户名：");
			String username = scanner.nextLine();
			System.out.print("请输入密码：");
			String password = scanner.nextLine();
			launchResponse = proto.launch(username, password);
			if (launchResponse == null) {
				System.out.println("出错！");
				return;
			}
			System.out.println(launchResponse.information);
			if (launchResponse.status) {
				islaunched = true;
				System.out.println("你好" + username);
				System.out.println("签名：\t" + launchResponse.signature);
				System.out.println("点数：\t" + launchResponse.bonus);
				System.out.println("赞：\t" + launchResponse.bonus);
				System.out.println("问题数：\t" + launchResponse.questionNum);
				System.out.println("已解决问题数：\t" + launchResponse.solvedQuestionNum);
				System.out.println("邮箱：\t" + launchResponse.mail_address);
			}


			while (islaunched) {
				try {
					System.out.println("1.请求用户信息：");
					System.out.println("2.登出");
					System.out.println("3.发送消息");
					System.out.println("4.赞用户");
					System.out.println("5.赞问题");
					System.out.println("6.进入房间");
					System.out.println("7.请求房间信息");
					System.out.println("8.创建问题");
					System.out.println("输入请求：");
					choose = scanner.nextInt();
					scanner.nextLine();

					switch (choose) {
						case 1:
							System.out.print("请输入用户名：");
							user = scanner.nextLine();
							ProjectClientProto.UserMessage userMessage = proto.requestUserInfo(user);
							System.out.println("用户名：" + user);
							System.out.println("签名：\t" + userMessage.signature);
							System.out.println("点数：\t" + userMessage.bonus);
							System.out.println("赞：\t" + userMessage.bonus);
							System.out.println("问题数：\t" + userMessage.questionNum);
							System.out.println("已解决问题数：\t" + userMessage.solvedQuestionNum);
							System.out.println("邮箱：\t" + userMessage.mail_address);
							break;
						case 2:
							proto.logout();
							System.out.println("再见");
							System.exit(0);
							break;
						case 3:
							System.out.print("请输入房间号");
							questionID = scanner.nextLine();
							System.out.print("请输入要发送的消息：");
							text = scanner.nextLine();
							proto.send(text, questionID);
							break;
						case 4:
							System.out.print("请输入要赞的用户名：");
							user = scanner.nextLine();
							if (proto.goodUser(user)) {
								System.out.println("成功");
							} else {
								System.out.println("用户不存在");
							}
							break;
						case 5:
							System.out.print("请输入要赞的问题号：");
							questionID = scanner.nextLine();
							if (proto.goodQuestion(questionID)) {
								System.out.println("成功");
							} else {
								System.out.println("问题不存在");
							}
							break;
						case 6:
							System.out.print("请输入要进入的房间号：");
							questionID = scanner.nextLine();
							questionMessage = proto.enterQuestion(questionID);
							if (questionMessage == null) {
								System.out.println("房间不存在");
							} else {
								System.out.println("问题号：" + questionMessage.id);
								System.out.println("拥有者：" + questionMessage.owner);
								System.out.println("题干：" + questionMessage.stem);
								System.out.println("补充：" + questionMessage.addition);
								System.out.println("已解决：" + (questionMessage.solved ? "是" : "否"));
								System.out.println("发布时间：" + questionMessage.time);
								System.out.println("赞：" + questionMessage.good + "次");
							}
							break;
						case 7:
							System.out.print("请输入要进入的房间号：");
							questionID = scanner.nextLine();
							questionMessage = proto.requestQuestionInfo(questionID);
							if (questionMessage == null) {
								System.out.println("房间不存在");
							} else {
								System.out.println("问题号：" + questionMessage.id);
								System.out.println("拥有者：" + questionMessage.owner);
								System.out.println("题干：" + questionMessage.stem);
								System.out.println("补充：" + questionMessage.addition);
								System.out.println("已解决：" + (questionMessage.solved ? "是" : "否"));
								System.out.println("发布时间：" + questionMessage.time);
								System.out.println("赞：" + questionMessage.good + "次");
							}
							break;
						case 8:
							System.out.print("题干：");
							stem = scanner.nextLine();
							System.out.print("补充信息：");
							addition = scanner.nextLine();
							if (proto.createQuestion(stem, addition)) {
								System.out.println("成功");
							} else {
								System.out.println("失败");
							}
							break;
						default:
							System.out.println("未知命令");
					}
				} catch(Exception e ){
				e.printStackTrace();
			}
		}
	}
}
