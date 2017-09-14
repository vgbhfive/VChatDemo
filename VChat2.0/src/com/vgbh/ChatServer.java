package com.vgbh;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	
	private ServerSocket ss ;//����˵�socket
	private boolean connected = false ;//�Ƿ���ͻ��˽�������
	private UserClient user ;//�ͻ��˵ĵ�����

	public static void main(String[] args) {
		ChatServer cs = new ChatServer();
		
		cs.start();//���������
	}

	/*
	 * ����������
	 */
	private void start() {
		//���ӿͻ���
		connection();
	}

	/**
	 * ���ܿͻ��˵����ӣ������ڲ�ͬ�ͻ���������ͬ�߳�
	 */
	private void connection() {
		try {
			//�����ȷ��socket���󣬼���ĳ���˿�
			ss = new ServerSocket(ChatClient.PORT);
			System.out.println("ServerSocket start! ----ss��" + ss);
			
			connected = true ;
			while (connected) {
				Socket socket = ss.accept();//�������տͻ��˵�����
				//System.out.println("connected succeed!");
				
				Client client = new Client(socket);//���ݲ�ͬ��socket��ȡ�ͻ��˶���
				Thread thread = new Thread(client);//�����ͻ��˵��߳�
				user = UserClient.getInstance();//��ȡ�ͻ��˵ĵ�����
				client.setId(user.putClient(client));//���������Client�û�
				thread.start();//�ͻ����߳�����
				
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();//�ر���
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
		

}