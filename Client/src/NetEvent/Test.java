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
			System.out.println("username:");
			username = scanner.nextLine();
			System.out.println("password:");
			password = scanner.nextLine();
			client.registerRequest(username, password, "没有", "装傻");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
