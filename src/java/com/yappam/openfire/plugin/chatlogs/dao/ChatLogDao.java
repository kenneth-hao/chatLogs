package com.yappam.openfire.plugin.chatlogs.dao;

import java.util.List;

import com.yappam.openfire.plugin.chatlogs.entity.ChatLog;
import com.yappam.openfire.plugin.chatlogs.utils.Page;


public interface ChatLogDao {

	void save(ChatLog chatLog);
	
	void remove(Long id);
	
	/*

	int getCount();

	int getLastId();

	ChatLog get(Long id);

	List<ChatLog> query(ChatLog chatLog, Page page);
	
	List<ChatLog> findAll();

	List<String> findLastContact(ChatLog chatLog);

	List<String> findAllContact();

	
	
	*/
	
	void updateOfflineReaded(ChatLog chatLog);
	
	
	List<ChatLog> queryOneToOne(ChatLog chatLog, Page page);

	Long countOneToOne(ChatLog chatLog);

}