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
	
	public static int PORT = 8888;//�����˿�
	
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
	
	private boolean flag = true ;//�Ƿ�ֹͣ�����˷�������
	private boolean disconnected = false;//�Ƿ�رտͻ��˵�IO��
	
	private String line = "" ;//�ͻ��˷�����Ϣ���ݵĳ�ʼ���ͻ���

	public static void main(String[] args) {
		ChatClient cc = new ChatClient();
		
		cc.start();//�����ͻ���
	}
	
	/*
	 * ����������
	 */
	public void start () {
		//��������
		tableFrame();
		//���ӷ����
		connection();
		//�ر���
		if (disconnected) {
			close();
		}
	}

	/**
	 * �ͻ��˽���
	 */
	public void tableFrame ()  {
		frame = new JFrame("ChatClient");//�½�һ��Jframe�������
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�����رմ���
		frame.setSize(500, 300);//���ڴ�С
		frame.setLocation(500, 300);//����λ��
		frame.setBackground(Color.WHITE);//���ڱ�����ɫ
		frame.setLayout(new BorderLayout());//Jframe�Ĳ��ַ�ʽ
		frame.setResizable(false);
		
		jpanelTop = new JPanel();//�½��ϲ��м�����
		jpanelBottom = new JPanel();//�½��ײ�����
		jpanelBottom.setLayout(new FlowLayout());//�ײ���������
		
		topText = new JTextArea("chatArea:",12,40);//�ϲ������ڵ��ı���
		topText.setEditable(false);//�ϲ��ı��򲻿�д
		bottomText = new JTextArea("",2,34);//�²��ı���
		//bottomText.setFont(new Font("΢���ź�", bottomText.getFont().getStyle(), 20));
		bottomText.setEditable(true);//�²��ı����д
		button = new JButton("send");//�½����Ͱ�ť
		button.addActionListener(new ActionListener() {//��ť�ļ����¼�
			public void actionPerformed(ActionEvent arg0) {
				send(bottomText.getText());//��������
				bottomText.setText("");//������Ϣ��ɣ���������Ϊ��
			}
		});
		
		jpanelTop.add(new JScrollPane(topText));//����������Ӵ��й��������ı���
		jpanelBottom.add(bottomText);//0
		jpanelBottom.add(button);
		
		frame.add(jpanelTop,BorderLayout.NORTH);
		frame.add(jpanelBottom,BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	/**
	 * ���ӷ����
	 */
	private void connection() {
		try {
			socket = new Socket("localhost", PORT);//���ʷ���ˣ���ַ�Ͷ˿�
			System.out.println("client start! -----socekt" + socket);
			
			Send send = new Send();//��ȡ���Ͷ���
			Receive receive = new Receive();//��ȡ���ն���
			Thread t1 = new Thread(send);//��ȡ�����߳�
			Thread t2 = new Thread(receive);//��ȡ�����߳�
			
			t1.start();//�����߳�����
			t2.start();//�����߳�����
					
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���Ϳͻ��˵�����
	 */
	public void send (String str) {
		flag = true;//ȷ���Ƿ���һ������
		sendMessage(str);//������Ϣ
	}	
	private void sendMessage (String str) {
		try {
			while (flag) {
				//str = str.trim();//ȥ���ַ�������Ŀո�
				//�˿ͻ��������˶Ͽ�����
				if (str.equals("byebye")) {
					writeLine("byebye");
					flag = false;
					disconnected = true;//�Ͽ��߳�
				}
				if (!str.isEmpty())	{//���ⷢ�Ϳ�����
					writeLine(str);
				}
				flag = false;//�����ظ�����ͬһ������
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
	 * ��ȡ����˵�����
	 */
	public void readLine () throws IOException {
		String line = null;
		while ((line = br.readLine()) != null) {
			topText.append("\n\r" + line);//���շ���˷��͵���Ϣ��������ʾ���Ϸ���TextArea��
		}
	}


	/**
	 * �ر���
	 */
	private void close() {
		try {
			socket.close();//�ر���
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 * ������Ϣ�߳�,���������˷�����Ϣ
	 */
	class Send implements Runnable{
		 
		@Override
		public void run() {
			try {
				os = socket.getOutputStream();
				send(line);//������Ϣ
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/*
	 * ������Ϣ�̣߳����ڽ��շ���˵���Ϣ
	 */
	class Receive implements Runnable {
		public void run() {
			try {
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));//���տͻ��˵�����
				readLine();//��ȡ��Ϣ
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	

}
