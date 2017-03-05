package NetEvent;

import Cos.CosHttpClient;
import Cos.FileOP;
import com.ClientSendMessage;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.request.StatFileRequest;
import util.MD5Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by xy16 on 17-2-12.
 */
public class Test {
	public static void main(String[] args) {
		String username;
		String password;
		Scanner scanner = new Scanner(System.in);

		Client client = new Client();
		client.start();

		try {

			System.out.println("用户名:");
			username = "xy16";
			System.out.println("密码");
			password = scanner.nextLine();
			client.launchRequest(username, password);
			ArrayList<String> pics = new ArrayList<>();
			pics.add("/home/xy16/JavaCode.d/ProjectClientProto/out/produc" +
					  "tion/ProjectClientProto/pictures/D784FA8B6D98D27699781BD9A7CF19F0");

			client.createQuestion("first question", "lala", new ArrayList<>(), pics, new ArrayList<>());
			client.enterQuestion("1");
			client.sendContent("%hello", pics, 1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
