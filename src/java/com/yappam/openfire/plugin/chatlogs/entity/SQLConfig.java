package com.yappam.openfire.plugin.chatlogs.entity;

public interface SQLConfig {

	/*
	String ChatLogMaxId = "SELECT max(id) FROM ofChatLogs";
	String ChatLogGetById = "SELECT * FROM ofChatLogs where id = ?";
	String ChatLogFindAll = "SELECT * FROM ofChatLogs";
	
	String ChatLogCount = "SELECT count(1) FROM ofChatLogs where state = 0";
	String ChatLogQuery = "SELECT * FROM ofChatLogs where state = 0";
	String ChatLogLastReceiver = "SELECT distinct receiver FROM ofChatLogs where state = 0 and sender = ?";
	String ChatLogContact = "SELECT distinct sessionJID FROM ofChatLogs where state = 0";
	*/
	String ChatLogOfflineReaded = "UPDATE ofChatLogs set offline = 0 where (sender=? AND receiver=?) OR (sender=? AND receiver=?) AND offline=1";
	String ChatLogDelete = "UPDATE ofChatLogs set state = 1 where id = ?";
	String ChatLogInsert = "INSERT INTO ofChatLogs(sessionJID, sender, receiver, createDate, length, content, detail, state, offline) VALUES(?,?,?,?,?,?,?,?,?)";
	String QueryOneToOne = "SELECT id, sessionjid, sender, receiver, createdate, length, content, detail, state FROM ofChatLogs WHERE (sender=? AND receiver=?) OR (sender=? AND receiver=?) AND offline=0";
}
