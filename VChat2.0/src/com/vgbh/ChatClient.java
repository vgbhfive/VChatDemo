package com.vgbh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatClient {
	
	public static int PORT = 8888;//监听端口
	
	private Socket socket;
	private BufferedReader br;
	private InputStream is;
	private OutputStream os ;
	
	private JFrame frame ;
	private JPanel jpanelTop ;
	private JPanel jpanelBottom ;
	private JTextArea topText ;
	private JTextArea bottomText ;
	private JButton button ;
	
	private boolean flag = true ;//是否停止向服务端发送数据
	private boolean disconnected = false;//是否关闭客户端的IO流
	
	private String line = "" ;//客户端发送消息内容的初始化和缓存

	public static void main(String[] args) {
		ChatClient cc = new ChatClient();
		
		cc.start();//启动客户端
	}
	
	/*
	 * 主方法启动
	 */
	public void start () {
		//建立界面
		tableFrame();
		//连接服务端
		connection();
		//关闭流
		if (disconnected) {
			close();
		}
	}

	/**
	 * 客户端界面
	 */
	public void tableFrame ()  {
		frame = new JFrame("ChatClient");//新建一个Jframe顶层对象
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//正常关闭窗口
		frame.setSize(500, 300);//窗口大小
		frame.setLocation(500, 300);//窗口位置
		frame.setBackground(Color.WHITE);//窗口背景颜色
		frame.setLayout(new BorderLayout());//Jframe的布局方式
		frame.setResizable(false);
		
		jpanelTop = new JPanel();//新建上部中间容器
		jpanelBottom = new JPanel();//新建底部容器
		jpanelBottom.setLayout(new FlowLayout());//底部容器布局
		
		topText = new JTextArea("chatArea:",12,40);//上部容器内的文本域
		topText.setEditable(false);//上部文本域不可写
		bottomText = new JTextArea("",2,34);//下部文本域
		//bottomText.setFont(new Font("微软雅黑", bottomText.getFont().getStyle(), 20));
		bottomText.setEditable(true);//下部文本域可写
		button = new JButton("send");//新建发送按钮
		button.addActionListener(new ActionListener() {//按钮的监听事件
			public void actionPerformed(ActionEvent arg0) {
				send(bottomText.getText());//发送数据
				bottomText.setText("");//发送消息完成，接着设置为空
			}
		});
		
		jpanelTop.add(new JScrollPane(topText));//顶部容器添加带有滚动条的文本域
		jpanelBottom.add(bottomText);//0
		jpanelBottom.add(button);
		
		frame.add(jpanelTop,BorderLayout.NORTH);
		frame.add(jpanelBottom,BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	/**
	 * 连接服务端
	 */
	private void connection() {
		try {
			socket = new Socket("localhost", PORT);//访问服务端，地址和端口
			System.out.println("client start! -----socekt" + socket);
			
			Send send = new Send();//获取发送对象
			Receive receive = new Receive();//获取接收对象
			Thread t1 = new Thread(send);//获取发送线程
			Thread t2 = new Thread(receive);//获取接收线程
			
			t1.start();//发送线程启动
			t2.start();//接收线程启动
					
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送客户端的数据
	 */
	public void send (String str) {
		flag = true;//确定是否发送一条数据
		sendMessage(str);//发送消息
	}	
	private void sendMessage (String str) {
		try {
			while (flag) {
				//str = str.trim();//去除字符串多余的空格
				//此客户端与服务端断开连接
				if (str.equals("byebye")) {
					writeLine("byebye");
					flag = false;
					disconnected = true;//断开线程
				}
				if (!str.isEmpty())	{//避免发送空内容
					writeLine(str);
				}
				flag = false;//避免重复发送同一条数据
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private void writeLine(String line) throws IOException {
		if (os != null) {
			os.write((line + "\r\n").getBytes());
			os.flush();
		}
	}
	
	/**
	 * 读取服务端的数据
	 */
	public void readLine () throws IOException {
		String line = null;
		while ((line = br.readLine()) != null) {
			topText.append("\n\r" + line);//接收服务端发送的消息并将其显示在上方的TextArea中
		}
	}


	/**
	 * 关闭流
	 */
	private void close() {
		try {
			socket.close();//关闭流
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 * 发送消息线程,用于向服务端发送消息
	 */
	class Send implements Runnable{
		 
		@Override
		public void run() {
			try {
				os = socket.getOutputStream();
				send(line);//发送消息
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/*
	 * 接收消息线程，用于接收服务端的消息
	 */
	class Receive implements Runnable {
		public void run() {
			try {
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));//接收客户端的数据
				readLine();//读取消息
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	

}
