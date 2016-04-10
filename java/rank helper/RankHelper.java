package com.lzb.spring.jdbctemplate;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author lizebin
 * 用于排名的帮助类
 */
public class RankHelper {

	private static Logger logger = LoggerFactory.getLogger(RankHelper.class);

	private JdbcTemplate jdbcTemplate;

	public RankHelper(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * @param dbTblName 表名
	 * @param dbTblUniqueFieldName 唯一字段名
	 * @param dbTblUniqueFieldVal 唯一字段值
	 * @param dbTblRankFieldName 用于排名的字段名你
	 * @param targetRank 目标排名,必须大于0
	 * @param isRanked 若否，将所有数据会对初始化排名
	 * @return
	 */
	public boolean setRank(String dbTblName, String dbTblUniqueField, Object dbTblUniqueFieldVal, String dbTblRankFieldName, int targetRank, boolean isRanked) {
		try {
			if(targetRank <= 0) {
				throw new IllegalArgumentException("targetRank参数不合法");
			}
			final int count = jdbcTemplate.queryForObject("select count(" + dbTblUniqueField + ") from " + dbTblName, Integer.class);
			//是否对数据进行初始化排名
			if(!isRanked) {
				final String[] sqlForBatch = new String[count];
				for(int i=0; i<count; i++) {
					String updateSql = "update " + dbTblName + " inner join " + "(select " + dbTblUniqueField + " from " + dbTblName + " order by " + dbTblUniqueField + " desc limit " + String.valueOf(i) + ",1) as t on rank.id = t.id " + " set " + dbTblRankFieldName + " = " + String.valueOf(i + 1);
					sqlForBatch[i] = updateSql;
				}
				jdbcTemplate.batchUpdate(sqlForBatch);
			}

			String sql = "select " + dbTblRankFieldName + " from " + dbTblName + " where " + dbTblUniqueField + " = ?";
			int sourceRank = jdbcTemplate.queryForObject(sql, new Object[]{dbTblUniqueFieldVal}, Integer.class);
			if(sourceRank == targetRank) {
				return true;
			}
			//如果源排名小于等于0，则会自动变为最大排名，再重排
			if(sourceRank <= 0) {
				sql = "select max(" + dbTblRankFieldName + ") as m, " + dbTblUniqueField + " from " + dbTblName;
				List<Map<String, Object>> _l = jdbcTemplate.queryForList(sql);
				if(!_l.isEmpty()) {
					Map<String, Object> _m = _l.get(0);
					int m = (Integer)_m.get("m");
					sql = "update " + dbTblName + " set " + dbTblRankFieldName + " = " + (m + 1) + " where " + dbTblUniqueField + "=?";
					jdbcTemplate.update(sql, new Object[]{dbTblUniqueFieldVal});
					sourceRank = m + 1;
					if(sourceRank == targetRank) {
						return true;
					}
				}
			}
			if(sourceRank > targetRank) {
				//source > target : target <= {} < source --> {} + 1
				sql = "update " + dbTblName + " set " + dbTblRankFieldName + " = " + dbTblRankFieldName + " + 1 where " + dbTblRankFieldName + " >= " + targetRank + " and " + dbTblRankFieldName + " < " + sourceRank;
			} else {
				//source < target : target >= {} > source --> {} - 1
				sql = "update " + dbTblName + " set " + dbTblRankFieldName + " = " + dbTblRankFieldName + " - 1 where " + dbTblRankFieldName + " <= " + targetRank + " and " + dbTblRankFieldName + " > " + sourceRank;
			}
			jdbcTemplate.update(sql);
			jdbcTemplate.update("update " + dbTblName + " set " + dbTblRankFieldName + " = " + targetRank + " where " + dbTblUniqueField + " = ? ", new Object[]{dbTblUniqueFieldVal});
		} catch(Exception e) {
			logger.error("设置排名报错", e);
			return false;
		}
		return true;
	}
}
