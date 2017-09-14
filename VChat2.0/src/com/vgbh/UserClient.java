package com.vgbh;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class UserClient {
	
	/**
	 * ����ģʽ
	 * �洢�ͻ��˵ĵ�����
	 */
	private static UserClient instance;
	private static int id = 0 ;//Ψһ��idֵ
	
	HashMap<Integer, Client> clients = new HashMap<Integer, Client>();//��ͬ�ͻ��˵Ķ���洢��key��id Value��Client
	
	//����Ĭ�ϵĹ��췽��
	private UserClient () {
		
	}
	
	/**
	 * ��������ֻ��һ��clients������
	 */
	public synchronized static UserClient getInstance () {
		if (instance == null) {
			instance = new UserClient();
		}
		return instance;
	}
	
	/**
	 * ��Client���󣬷���ֻ���ڴ�Client�����Ψһid
	 */
	public synchronized int putClient (Client client) {
		clients.put(id, client);
		return id++;
	}
	
	/**
	 * ɾ���Ͽ����ӵ�Client
	 */
	public synchronized void removeClient (int id) {
		clients.remove(id);
	}
	
	/**
	 * ��idֵ��ȡClient����
	 */
	public synchronized Client getClient (int id) {
		return clients.get(id);
	}
	
	/**
	 * �Ƿ����ĳ��Client
	 */
	public synchronized boolean hasClient (int id) {
		return clients.containsKey(id);
	}
	
	/**
	 * ��ȡ���е�Client����
	 */
	public synchronized Set<Entry<Integer,Client>> getAllClient () {
		return clients.entrySet();
	}
	
}
