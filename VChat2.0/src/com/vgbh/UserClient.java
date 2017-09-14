package com.vgbh;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class UserClient {
	
	/**
	 * 单例模式
	 * 存储客户端的单例表
	 */
	private static UserClient instance;
	private static int id = 0 ;//唯一的id值
	
	HashMap<Integer, Client> clients = new HashMap<Integer, Client>();//不同客户端的对象存储，key：id Value：Client
	
	//屏蔽默认的构造方法
	private UserClient () {
		
	}
	
	/**
	 * 创建并且只有一个clients单例表
	 */
	public synchronized static UserClient getInstance () {
		if (instance == null) {
			instance = new UserClient();
		}
		return instance;
	}
	
	/**
	 * 由Client对象，返回只属于此Client对象的唯一id
	 */
	public synchronized int putClient (Client client) {
		clients.put(id, client);
		return id++;
	}
	
	/**
	 * 删除断开连接的Client
	 */
	public synchronized void removeClient (int id) {
		clients.remove(id);
	}
	
	/**
	 * 由id值获取Client对象
	 */
	public synchronized Client getClient (int id) {
		return clients.get(id);
	}
	
	/**
	 * 是否包含某个Client
	 */
	public synchronized boolean hasClient (int id) {
		return clients.containsKey(id);
	}
	
	/**
	 * 获取所有的Client对象
	 */
	public synchronized Set<Entry<Integer,Client>> getAllClient () {
		return clients.entrySet();
	}
	
}
