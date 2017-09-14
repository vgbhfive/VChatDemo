package com.vgbh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class Client implements Runnable{

	private Socket socket;
	private InputStream is ;
	private OutputStream os ;
	private BufferedReader br ;
	
	private int id ;//客户端唯一id
	private UserClient clients ;//客户端的单例表
	
	public Client(Socket socket) {
		this.socket = socket;
		clients = UserClient.getInstance();//获取客户端的单例表
		
		System.out.println("Client" + id++ + " connected successfully!");	
	}

	@Override
	public void run() {
		Send send = new Send();//获取发送消息对象
		Receive receive = new Receive();//获取接收消息对象
		Thread t1 = new Thread(send);//获取发送线程
		Thread t2 = new Thread(receive);//获取接收线程
		
		t1.start();//发送线程启动
		t2.start();	//接收线程启动
		
	}

	private String line = "" ;//接收来自客户端的消息缓存
	
	/**
	 * 读取客户端发送的消息
	 */
	public void sendToAllClient () {
		try {
			while (true) {
				//line = line.trim();//去除字符串多余的空格
				if (line.equals("byebye")) {
					System.out.println(line);
					break;
				}
				line = br.readLine();
				if (line != null) {
					//服务端向不同客户端分发消息
					for (Map.Entry<Integer, Client> item : clients.getAllClient()) {
						item.getValue().writeLine("Client" + id + " saied : " + line);
					}
					System.out.println("Client" + id + " saied : " + line);
				}
				
			}
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			close();//关闭流
		}
	}
	
	/**
	 * 向客户端发送消息
	 */
	public void writeLine (String line) {
		try {
			if (os != null) {
				os.write((line + "\r\n").getBytes());
				os.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 关闭流
	 */
	public void close () {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取当前用户的id值
	 */
	public int getId() {
		return id;
	}

	/**
	 * 设置当前用户的id值
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
	/*
	 * 服务端发送给客户端的消息线程
	 */
	class Send implements Runnable{	
		
		@Override
		public void run() {
			try {
				os = socket.getOutputStream();
				if (!line.isEmpty()) writeLine(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	/*
	 * 服务端接收来自客户端的消息线程
	 */
	class Receive implements Runnable {

		@Override
		public void run() {
			try {
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));//接收客户端的数据
				sendToAllClient();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
}
