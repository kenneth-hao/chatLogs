package com.yappam.openfire.plugin.chatlogs.utils;

public final class SQLUtils {
	
	private SQLUtils() { }
	
	public static String genCountSql(String sql) {
		if (sql == null || "".equals(sql.trim())) {
			throw new RuntimeException("需要转换的 SQL 不允许为空!");
		} else {
			int ix = sql.toLowerCase().indexOf(" from ");
			if (ix == -1) {
				throw new RuntimeException("请确认SQL的格式是否正确!");
			} else {
				String sqlSuffix = sql.substring(ix);
				String countSql = "SELECT count(1)" + sqlSuffix;
				return countSql;
			}
		}
	}
	

}
