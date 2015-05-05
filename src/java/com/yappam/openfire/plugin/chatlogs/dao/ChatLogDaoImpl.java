package com.yappam.openfire.plugin.chatlogs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yappam.openfire.plugin.chatlogs.entity.ChatLog;
import com.yappam.openfire.plugin.chatlogs.entity.SQLConfig;
import com.yappam.openfire.plugin.chatlogs.utils.Page;
import com.yappam.openfire.plugin.chatlogs.utils.SQLUtils;

public class ChatLogDaoImpl implements ChatLogDao {

	private static final Logger logger = LoggerFactory.getLogger(ChatLogDaoImpl.class);

	@Override
	public void save(ChatLog chatLog) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DbConnectionManager.getConnection();
			ps = connection.prepareStatement(SQLConfig.ChatLogInsert);
			int i = 1;
			ps.setString(i++, chatLog.getSessionJID());
			ps.setString(i++, chatLog.getSender());
			ps.setString(i++, chatLog.getReceiver());
			ps.setTimestamp(i++, chatLog.getCreateDate());
			ps.setInt(i++, chatLog.getLength());
			ps.setString(i++, chatLog.getContent());
			ps.setString(i++, chatLog.getDetail());
			ps.setInt(i++, chatLog.getState());
			ps.setInt(i++, chatLog.getOffline());

			ps.execute();
		} catch (SQLException e) {
			logger.error("Insert ChatLogs error[{}]", chatLog, e);
		} finally {
			DbConnectionManager.closeConnection(ps, connection);
		}
	}
	
	@Override
	public void remove(Long id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQLConfig.ChatLogDelete);
			pstmt.setLong(1, id);
			pstmt.execute();
		} catch (SQLException e) {
			logger.error("Delete ChatLog error[{}]", id, e);
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}
	
	/*

	@Override
	public int getCount() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = -1;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQLConfig.ChatLogCount);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			} else {
				count = 0;
			}
		} catch (SQLException e) {
			logger.error("Count ChatLogs error[{}]", e);
			return 0;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
		return count;
	}

	@Override
	public int getLastId() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = -1;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQLConfig.ChatLogMaxId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			} else {
				count = 0;
			}
		} catch (SQLException e) {
			logger.error("Get ChatLog Max Id error[{}]", e);
			return 0;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
		return count;
	}

	@Override
	public ChatLog get(Long id) {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DbConnectionManager.getConnection();
			ps = con.prepareStatement(SQLConfig.ChatLogGetById);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			ChatLog chatLog = null;
			while (rs.next()) {
				chatLog = toChatLog(rs);
			}
			return chatLog;
		} catch (SQLException e) {
			logger.error("Get ChatLog by id[{}]", id, e);
			return null;
		} finally {
			DbConnectionManager.closeConnection(ps, con);
		}
	}

	private Long count(ChatLog chatLog) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DbConnectionManager.getConnection();
			StringBuilder coutnSql = buildCondition(chatLog, SQLConfig.ChatLogCount);

			logger.debug("Count ChatLog[{}]", coutnSql);

			ps = con.prepareStatement(coutnSql.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				Long count = rs.getLong(1);
				logger.debug("Count value[{}]", count);

				return count;
			} else {
				return 0L;
			}
		} catch (SQLException e) {
			logger.error("Count ChatLog error[{}]", e);
			return 0L;
		} finally {
			DbConnectionManager.closeConnection(ps, con);
		}
	}

	@Override
	public List<ChatLog> query(ChatLog chatLog, Page page) {
		Connection con = null;
		Statement sm = null;
		List<ChatLog> results = new ArrayList<ChatLog>();

		try {
			con = DbConnectionManager.getConnection();
			sm = con.createStatement();

			StringBuilder sqlBuilder = buildCondition(chatLog, SQLConfig.ChatLogQuery);

			page.setTotalCount(count(chatLog));

			sqlBuilder.append(" order by ").append(page.getOrderBy()).append(" ").append(page.getOrder());
			sqlBuilder.append(" limit ").append(page.getBeginIndex()).append(", ").append(page.getPageSize());

			logger.debug("ChatLog query[{}]", sqlBuilder.toString());

			ResultSet rs = sm.executeQuery(sqlBuilder.toString());
			while (rs.next()) {
				results.add(toChatLog(rs));
			}
			page.setDateList(results);

			logger.debug("Page[{}]", page);
			return results;
		} catch (SQLException e) {
			logger.error("Query ChatLog error[{}]", e);
			return results;
		} finally {
			DbConnectionManager.closeConnection(sm, con);
		}
	}
	
	

	private StringBuilder buildCondition(ChatLog chatLog, String sql) {
		StringBuilder sqlBuilder = new StringBuilder(sql);
		if (chatLog != null) {
			if (StringUtils.isNotEmpty(chatLog.getSender()) && StringUtils.isNotEmpty(chatLog.getReceiver())) {
				sqlBuilder.append(" and");
				sqlBuilder.append(" (");
				sqlBuilder.append(" sender ='").append(chatLog.getSender()).append("'");
				sqlBuilder.append(" and receiver ='").append(chatLog.getReceiver()).append("'");
				sqlBuilder.append(" )");
				sqlBuilder.append(" or");
				sqlBuilder.append(" (");
				sqlBuilder.append(" sender ='").append(chatLog.getReceiver()).append("'");
				sqlBuilder.append(" and receiver ='").append(chatLog.getSender()).append("'");
				sqlBuilder.append(" )");
			} else {
				if (StringUtils.isNotEmpty(chatLog.getSender())) {
					sqlBuilder.append(" and (sender ='").append(chatLog.getSender()).append("'");
					sqlBuilder.append(" or receiver ='").append(chatLog.getSender()).append("')");
				}
				if (StringUtils.isNotEmpty(chatLog.getReceiver())) {
					sqlBuilder.append(" and (sender ='").append(chatLog.getReceiver()).append("'");
					sqlBuilder.append(" or receiver ='").append(chatLog.getReceiver()).append("')");
				}
			}
			if (StringUtils.isNotBlank(chatLog.getContent())) {
				sqlBuilder.append(" and receiver like '%").append(chatLog.getContent()).append("%'");
			}
			if (chatLog.getCreateDate() != null) {
				String crateatDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(chatLog.getCreateDate().getTime()));
				sqlBuilder.append(" and createDate like '%").append(crateatDate).append("%'");
			}
		}
		return sqlBuilder;
	}

	@Override
	public List<ChatLog> findAll() {
		Connection con = null;
		Statement pstmt = null;

		List<ChatLog> result = new ArrayList<ChatLog>();
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.createStatement();

			ResultSet rs = pstmt.executeQuery(SQLConfig.ChatLogFindAll);
			while (rs.next()) {
				result.add(toChatLog(rs));
			}
			return result;
		} catch (SQLException e) {
			logger.error("Query ChatLog error[{}]", e);
			return result;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}

	public List<String> findLastContact(ChatLog chatLog) {
		Connection con = null;
		PreparedStatement pstmt = null;
		List<String> result = new ArrayList<String>();

		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQLConfig.ChatLogContact);
			pstmt.setString(1, chatLog.getSender());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("receiver"));
			}
			return result;
		} catch (SQLException sqle) {
			logger.error("Find last contact error[{}]", chatLog, sqle);
			return result;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}

	public List<String> findAllContact() {
		Connection con = null;
		PreparedStatement pstmt = null;
		List<String> result = new ArrayList<String>();

		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQLConfig.ChatLogContact);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("sessionJID"));
			}
			return result;
		} catch (SQLException e) {
			logger.error("FindAllContact error[{}]", e);
			return result;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}

	*/
	
	

	@Override
	public List<ChatLog> queryOneToOne(ChatLog chatLog, Page page) {
		Connection con = null;
		PreparedStatement psm = null;
		List<ChatLog> results = new ArrayList<ChatLog>();

		try {
			con = DbConnectionManager.getConnection();

			Long count = this.countOneToOne(chatLog);
			page.setTotalCount(count);
			
			StringBuilder sqlBuilder = new StringBuilder(SQLConfig.QueryOneToOne);
			sqlBuilder.append(" ORDER BY ").append(page.getOrderBy()).append(" ").append(page.getOrder());
			sqlBuilder.append(" LIMIT ").append(page.getBeginIndex()).append(", ").append(page.getPageSize());
			
			psm = con.prepareStatement(sqlBuilder.toString());
			psm.setString(1, chatLog.getSender());
			psm.setString(2, chatLog.getReceiver());
			psm.setString(3, chatLog.getReceiver());
			psm.setString(4, chatLog.getSender());

			logger.debug("ChatLog query[{}]", sqlBuilder.toString());

			ResultSet rs = psm.executeQuery();
			while (rs.next()) {
				results.add(toChatLog(rs));
			}
			page.setDateList(results);

			logger.debug("Page[{}]", page);
			return results;
		} catch (SQLException e) {
			logger.error("Query ChatLog error[{}]", e);
			return results;
		} finally {
			DbConnectionManager.closeConnection(psm, con);
		}
	}
	
	@Override
	public void updateOfflineReaded(ChatLog chatLog) {
		Connection connection = null;
		PreparedStatement psm = null;
		try {
			connection = DbConnectionManager.getConnection();
			psm = connection.prepareStatement(SQLConfig.ChatLogOfflineReaded);
			int i = 1;
			psm.setString(i++, chatLog.getSender());
			psm.setString(i++, chatLog.getReceiver());
			psm.setString(i++, chatLog.getReceiver());
			psm.setString(i++, chatLog.getSender());

			psm.execute();
		} catch (SQLException e) {
			logger.error("Insert ChatLogs error[{}]", chatLog, e);
		} finally {
			DbConnectionManager.closeConnection(psm, connection);
		}
	}

	@Override
	public Long countOneToOne(ChatLog chatLog) {
		Connection con = null;
		PreparedStatement psm = null;
		
		Long count = 0L;

		try {
			con = DbConnectionManager.getConnection();

			String countSql = SQLUtils.genCountSql(SQLConfig.QueryOneToOne);
			psm = con.prepareStatement(countSql);
			psm.setString(1, chatLog.getSender());
			psm.setString(2, chatLog.getReceiver());
			psm.setString(3, chatLog.getReceiver());
			psm.setString(4, chatLog.getSender());
			
			ResultSet countRs = psm.executeQuery();
			if (countRs.next()) {
				count = countRs.getLong(1);
				logger.debug("Count value[{}]", count);
			} 
			
			return count;
		} catch (SQLException e) {
			logger.error("Query ChatLog error[{}]", e);
			return 0L;
		} finally {
			DbConnectionManager.closeConnection(psm, con);
		}
	}
	
	private ChatLog toChatLog(ResultSet rs) throws SQLException {
		ChatLog chatLog = new ChatLog();
		chatLog.setId(rs.getLong("id"));
		chatLog.setContent(rs.getString("content"));
		chatLog.setCreateDate(rs.getTimestamp("createDate"));
		chatLog.setLength(rs.getInt("length"));
		chatLog.setSessionJID(rs.getString("sessionJID"));
		chatLog.setSender(rs.getString("sender"));
		chatLog.setReceiver(rs.getString("receiver"));
		return chatLog;
	}

}
