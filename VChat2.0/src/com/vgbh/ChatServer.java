package com.vgbh;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	
	private ServerSocket ss ;//服务端的socket
	private boolean connected = false ;//是否与客户端进行连接
	private UserClient user ;//客户端的单例表

	public static void main(String[] args) {
		ChatServer cs = new ChatServer();
		
		cs.start();//启动服务端
	}

	/*
	 * 主方法启动
	 */
	private void start() {
		//连接客户端
		connection();
	}

	/**
	 * 接受客户端的连接，并对于不同客户端启动不同线程
	 */
	private void connection() {
		try {
			//服务端确定socket对象，监听某个端口
			ss = new ServerSocket(ChatClient.PORT);
			System.out.println("ServerSocket start! ----ss：" + ss);
			
			connected = true ;
			while (connected) {
				Socket socket = ss.accept();//持续接收客户端的连接
				//System.out.println("connected succeed!");
				
				Client client = new Client(socket);//根据不同的socket获取客户端对象
				Thread thread = new Thread(client);//启动客户端的线程
				user = UserClient.getInstance();//获取客户端的单例表
				client.setId(user.putClient(client));//单例表添加Client用户
				thread.start();//客户端线程启动
				
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();//关闭流
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
		

}