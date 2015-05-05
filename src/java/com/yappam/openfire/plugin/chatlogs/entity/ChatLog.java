package com.yappam.openfire.plugin.chatlogs.entity;

import java.sql.Timestamp;

public class ChatLog {
	
	public static final int OFFLINE_MSG = 1;
	
	public static final int NON_OFFLINE_MSG = 0;

	private Long id;
	private String sessionJID;
	private String sender;
	private String receiver;
	private Timestamp createDate;
	private String content;
	private String detail;
	private int length;
	private int state;
	/**
	 * 是否是离线消息:
	 * 1. 离线消息;
	 * 0. 非离线消息
	 */
	private int offline;
	
	public int getOffline() {
		return offline;
	}

	public void setOffline(int offline) {
		this.offline = offline;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSessionJID() {
		return sessionJID;
	}

	public void setSessionJID(String sessionJID) {
		this.sessionJID = sessionJID;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "ChatLog [content=" + content + ", createDate=" + createDate + ", detail=" + detail + ", id=" + id + ", length=" + length
				+ ", receiver=" + receiver + ", sender=" + sender + ", sessionJID=" + sessionJID + ", state=" + state + "]";
	}

}
