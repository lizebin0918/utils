package com.um.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import com.um.exception.DaoException;

public interface IBaseDAO<T> {
	
	public T getBeanByID(String beanName ,String id, boolean lock) throws DaoException;
	
	public <T> Collection<T> getBeansByBean(T bean, MatchMode mode) throws DaoException;
	
	public Collection<T> getBeansByParams(String hqlName, ArrayList<String> paraList) throws DaoException;
	
	public Collection<T> getBeansByParams(String hqlName, Map<String, Object> paraMap) throws DaoException;
	
	public Object[] getObjectsByParams(String hqlName, ArrayList<String> paraList) throws DaoException;
	
	public Object[] getObjectsByParams(String hqlName, Map<String, Object> paraMap) throws DaoException;
	
	public Object getObjectByParams(String hqlName, ArrayList<String> paraList) throws DaoException;
	
	public void makePersistent(T bean, boolean flag) throws DaoException;
	
	public void makeTransient(T bean)throws DaoException;
	
	public void updateBean(T bean) throws DaoException;
	
	public void deleteOrUpdateByParams(String hqlName, ArrayList<String> paraList)throws DaoException;
	
	public void deleteOrUpdateByParams(String hqlName, Map<String, Object> paraList)throws DaoException;
	
	public void batchInsert(Collection<T> beans) throws DaoException;
	
	public void batchUpdate(Collection<T> beans) throws DaoException;
	
	public void batchDelete(Collection<T> beans) throws DaoException;
	
	public void batchUpdateOrInsert(Collection<T> beans) throws DaoException;
	
	public int executeUpdateHql(String hqlName,ArrayList<String> paraList) throws DaoException;
	
	public int executeUpdateHql(String hqlName,Map<String, Object> map) throws DaoException;
	
	public void enableFilter(String filterName,Map<String, Object> map) throws DaoException;
	
	public void disableFilter(String filterName) throws DaoException;
	
	public Vector<Order> getOrderVector();
	
	public void setHbmFlushSize(int hbmFlushSize);
	
	public int getHbmFlushSize();
	
	public String getSequenceByName(String sqlName);

	
	public int getCountsByBean(T bean, MatchMode mode);

	
	public Long getOracleSequence(String seqName);
	
	public void execDllSql(String ddlSql);
	
	public IBaseDAO<T> addOrder(Order order);
	
	public void deleteByIDs(String beanName, String[] id) throws DaoException;

	public void deleteByCollection(Collection<T> list) throws DaoException;
	
	public long getBeansCount(String hqlName, ArrayList<String> paraList)throws DaoException;
	
	public long getBeansCount(String hqlName, Map<String, Object> paraList)throws DaoException;
}
