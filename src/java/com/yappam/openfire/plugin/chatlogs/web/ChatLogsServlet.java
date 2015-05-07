package com.yappam.openfire.plugin.chatlogs.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.admin.AuthCheckFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yappam.openfire.plugin.chatlogs.dao.ChatLogDao;
import com.yappam.openfire.plugin.chatlogs.dao.ChatLogDaoImpl;
import com.yappam.openfire.plugin.chatlogs.entity.ChatLog;
import com.yappam.openfire.plugin.chatlogs.utils.Page;

public class ChatLogsServlet extends HttpServlet {

	private static final long serialVersionUID = -7408636229554177957L;

	private Logger logger = LoggerFactory.getLogger(ChatLogsServlet.class);

	private ChatLogDao chatLogDao;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		chatLogDao = new ChatLogDaoImpl();

		AuthCheckFilter.addExclude("chatlogs");
		AuthCheckFilter.addExclude("chatlogs/chat-log");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doPost(request, response);
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		
		String action = request.getParameter("action");
		if (action != null) {
			action = action.trim();
		}
		
		if (action == null || "".equals(action)) {
			this.queryOneByOne(request, response);
		} else if ("d".equals(action)) {
			// TODO : del
			// chatLogDao.remove(Long.parseLong(request.getParameter("id")));
			// response.sendRedirect("/plugins/chatlog/chat-log");
		} else if ("c".equals(action)) {
			this.countOneByOne(request, response);
		} else if ("p".equals(action)) {
			// request.setAttribute("chatLog", chatLog);
			// request.setAttribute("page", page);
			// request.getRequestDispatcher("/plugins/chatLogs/chat-log-list.jsp").forward(request, response);
		} else {
			
		}
	}
	
	private void out(HttpServletResponse response, String str) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(str);
		out.flush();
	}
	
	private void queryOneByOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ChatLog chatLog = getChatLog(request);

		Page page = getPage(request);
		List<ChatLog> chatLogs = chatLogDao.queryOneToOne(chatLog, page);
		chatLogDao.updateOfflineReaded(chatLog);
		logger.debug("Find chatLog size[{}]", chatLogs.size());
		
		String res = "";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", chatLogs);
		map.put("more", page.isMore());
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, map);
		
		String type = request.getParameter("type");
		
		if (StringUtils.isEmpty(type) || "json".equalsIgnoreCase(type)) {
			res = writer.toString();
 			response.setContentType("application/json");

 			out(response, res);
		} else if ("jsonp".equalsIgnoreCase(type)) {			
			res = "load_chatlogs_success(" + writer.toString() + ")";
 			response.setContentType("application/json");

 			out(response, res);
		}
	}
	
	private void countOneByOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ChatLog chatLog = getChatLog(request);

		Long count = chatLogDao.countOneToOne(chatLog);
		logger.debug("Find chatLog size[{}]", count);
		
		String ret = "";
		
		if ("json".equalsIgnoreCase(request.getParameter("type"))) {
			response.setContentType("application/json");
			
			StringWriter writer = new StringWriter();
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("count", count);
			mapper.writeValue(writer, map);
			
			ret = "foo(" + writer.toString() + ")";
		} else {
			response.setContentType("text/plain");
			
			ret = count.toString();
		}
		
		out(response, ret);
	}

	private ChatLog getChatLog(HttpServletRequest request) {
		ChatLog chatLog = new ChatLog();
		if (StringUtils.isNotEmpty(request.getParameter("sender"))) {
			chatLog.setSender(request.getParameter("sender"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("receiver"))) {
			chatLog.setReceiver(request.getParameter("receiver"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("content"))) {
			chatLog.setContent(request.getParameter("content"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("createDate"))) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				chatLog.setCreateDate(new Timestamp(df.parse(request.getParameter("createDate")).getTime()));
			} catch (ParseException e) {
				logger.error("Format date error", e);
			}
		}
		return chatLog;
	}

	private Page getPage(HttpServletRequest request) {
		Page page = new Page();
		if (StringUtils.isNotEmpty(request.getParameter("pageSize"))) {
			page.setPageSize(Integer.valueOf(request.getParameter("pageSize")));
		}
		if (StringUtils.isNotEmpty(request.getParameter("pageNum"))) {
			page.setPageNum(Integer.valueOf(request.getParameter("pageNum")));
		}
		return page;
	}

	@Override
	public void destroy() {
		super.destroy();
		// Release the excluded URL
		AuthCheckFilter.removeExclude("chatlog/chat-log");
		AuthCheckFilter.removeExclude("chatlog");
	}

}