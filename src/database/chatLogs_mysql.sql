# $Revision$
# $Date$

INSERT INTO ofVersion (name, version) VALUES ('chatLogs', 0);

CREATE TABLE ofChatLogs
(
  id  			INT PRIMARY KEY AUTO_INCREMENT,	
  sessionjid 	VARCHAR(100) COMMENT '会话JID',    
  sender    	VARCHAR(100) COMMENT '发送人',    
  receiver      VARCHAR(100) COMMENT '接收人',  
  createdate 	timestamp COMMENT '创建时间',    
  length    	INT COMMENT '消息长度',            
  content    	VARCHAR(2000) COMMENT '消息内容',  
  detail     	VARCHAR(4000) COMMENT '详细的 XML 节信息',  
  state         INT COMMENT '消息状态标识: 0 正常',
  offline		INT COMMENT '是否属于离线消息: 1 离线消息; 2 正常消息'          
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='聊天记录表';