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
	
	private int id ;//�ͻ���Ψһid
	private UserClient clients ;//�ͻ��˵ĵ�����
	
	public Client(Socket socket) {
		this.socket = socket;
		clients = UserClient.getInstance();//��ȡ�ͻ��˵ĵ�����
		
		System.out.println("Client" + id++ + " connected successfully!");	
	}

	@Override
	public void run() {
		Send send = new Send();//��ȡ������Ϣ����
		Receive receive = new Receive();//��ȡ������Ϣ����
		Thread t1 = new Thread(send);//��ȡ�����߳�
		Thread t2 = new Thread(receive);//��ȡ�����߳�
		
		t1.start();//�����߳�����
		t2.start();	//�����߳�����
		
	}

	private String line = "" ;//�������Կͻ��˵���Ϣ����
	
	/**
	 * ��ȡ�ͻ��˷��͵���Ϣ
	 */
	public void sendToAllClient () {
		try {
			while (true) {
				//line = line.trim();//ȥ���ַ�������Ŀո�
				if (line.equals("byebye")) {
					System.out.println(line);
					break;
				}
				line = br.readLine();
				if (line != null) {
					//�������ͬ�ͻ��˷ַ���Ϣ
					for (Map.Entry<Integer, Client> item : clients.getAllClient()) {
						item.getValue().writeLine("Client" + id + " saied : " + line);
					}
					System.out.println("Client" + id + " saied : " + line);
				}
				
			}
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			close();//�ر���
		}
	}
	
	/**
	 * ��ͻ��˷�����Ϣ
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
	 * �ر���
	 */
	public void close () {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡ��ǰ�û���idֵ
	 */
	public int getId() {
		return id;
	}

	/**
	 * ���õ�ǰ�û���idֵ
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
	/*
	 * ����˷��͸��ͻ��˵���Ϣ�߳�
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
	 * ����˽������Կͻ��˵���Ϣ�߳�
	 */
	class Receive implements Runnable {

		@Override
		public void run() {
			try {
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));//���տͻ��˵�����
				sendToAllClient();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
}
