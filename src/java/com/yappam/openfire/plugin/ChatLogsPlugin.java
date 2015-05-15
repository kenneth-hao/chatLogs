package com.yappam.openfire.plugin;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import org.dom4j.Element;

import com.yappam.openfire.plugin.chatlogs.dao.ChatLogDao;
import com.yappam.openfire.plugin.chatlogs.dao.ChatLogDaoImpl;
import com.yappam.openfire.plugin.chatlogs.entity.ChatLog;

public class ChatLogsPlugin implements Plugin, PacketInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatLogsPlugin.class);
	
	private static final String propKey = "fromUserID";
	
	private static PluginManager pluginManager;
	
	private InterceptorManager interceptorManager = InterceptorManager.getInstance();
	
	private ChatLogDao chatLogDao = new ChatLogDaoImpl();
	
	private PresenceManager presenceManager;  
	
	private UserManager userManager;  

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		XMPPServer server = XMPPServer.getInstance();
		pluginManager = manager;
		presenceManager = server.getPresenceManager(); 
		userManager = server.getUserManager();
		interceptorManager.addInterceptor(this);
	}

	@Override
	public void destroyPlugin() {
		interceptorManager.removeInterceptor(this);
		
	}

	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		if (session != null) {
            debug(packet, incoming, processed, session);
        }
        
        JID recipient = packet.getTo();
        if (recipient != null) {
            String username = recipient.getNode();
            // 广播消息或是不存在/没注册的用户.
            if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {
                return;
            } 
            if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
                // 非当前openfire服务器信息
                return;	
            } 
            if ("".equals(recipient.getResource())) {
            	
            }
        }
        this.doAction(packet, incoming, processed, session);
		
	}

	private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {
		Packet copyPacket = packet.createCopy();

		if (packet instanceof Message) {
			Message message = (Message) copyPacket;

			if (message.getType() == Message.Type.chat || message.getType() == Message.Type.groupchat) {
				if (session == null) {
					return;
				}
				if (processed || !incoming) {
					return;
				}
				
                JID recipient = message.getTo();
                
				try {
	                if (recipient.getNode() == null  
	                		|| !UserManager.getInstance().isRegisteredUser(  
                                recipient.getNode())) {  
                    // Sender is requesting presence information of an  
                    // anonymous user  
	                	throw new UserNotFoundException("Username is null");  
	                }
	                Presence status = presenceManager.getPresence(userManager  
                            .getUser(recipient.getNode()));  
	                ChatLog chatLog = this.get(copyPacket, incoming, session);
	                
	                if (chatLog != null) {
	                
		                if (status != null) {
		                	chatLog.setOffline(ChatLog.NON_OFFLINE_MSG);
		                } else {
		                	chatLog.setOffline(ChatLog.OFFLINE_MSG);
		                }
		                
		                String fromUserID = getFromUser(message, propKey);
	
	    				if (fromUserID != null && fromUserID.equals(chatLog.getReceiver())) {
	    					return;
	    				}
	
	    				chatLogDao.save(chatLog);
	    				logger.debug("Save chatLog[{}]", chatLog);
	                }
				} catch (UserNotFoundException  e) {
					logger.warn("exceptoin " + recipient.getNode() + " not find"  
                            + ",full jid: " + recipient.toFullJID()); 
				}
			}
		}
	}
	
	private ChatLog get(Packet packet, boolean incoming, Session session) {
		Message message = (Message) packet;
		ChatLog chatLog = new ChatLog();
		
		JID jid = session.getAddress();
        if (incoming) {        // 发送者
        	chatLog.setSender(jid.getNode());
            JID recipient = message.getTo();
            chatLog.setReceiver(recipient.getNode());
        } 

		if (session != null) {
			chatLog.setSessionJID(session.getAddress().toString());
		}
		String content = message.getBody();
		if (content == null) {
			return null;
		}
		chatLog.setContent(content);
		chatLog.setCreateDate(new Timestamp(new Date().getTime()));
		chatLog.setDetail(message.toXML());
		if (chatLog.getContent() != null) {
			chatLog.setLength(chatLog.getContent().length());
		}
		chatLog.setState(0);

		return chatLog;
	}
	
	private String getFromUser(Message message, String propKey) {
		Element propElem = message.getElement().element("properties");
		if (propElem == null) {
			return null;
		}
		List<Element> props = propElem.elements();
		if (props == null || props.size() == 0) {
			return null;
		}
		for (Element prop : props) {
			if (propKey.equalsIgnoreCase(prop.element("name").getTextTrim())) {
				return prop.element("value").getTextTrim();
			}
		}
		return null;
	}
	
	 /**
     * <b>function:</b> 调试信息
     * @createDate 2013-3-27 下午04:44:31
     * @param packet 数据包
     * @param incoming 如果为ture就表明是发送者
     * @param processed 执行
     * @param session 当前用户session
     */
    private void debug(Packet packet, boolean incoming, boolean processed, Session session) {
        String info = "[ packetID: " + packet.getID() + ", to: " + packet.getTo() + ", from: " + packet.getFrom() + ", incoming: " + incoming + ", processed: " + processed + " ]";
        
        long timed = System.currentTimeMillis();
        debug("################### start ###################" + timed);
        debug("id:" + session.getStreamID() + ", address: " + session.getAddress());
        debug("info: " + info);
        debug("xml: " + packet.toXML());
        debug("################### end #####################" + timed);
        
        logger.info("id:" + session.getStreamID() + ", address: " + session.getAddress());
        logger.info("info: {}", info);
        logger.info("plugin Name: " + pluginManager.getName(this) + ", xml: " + packet.toXML());
    }
    
    private void debug(Object message) {
        if (true) {
            System.out.println(message);
        }
    }
}
