package com.um.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanMap;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.StringType;

import com.um.dao.IBaseDAO;
import com.um.exception.DaoException;

public class BaseDAOImpl<T> implements IBaseDAO<T> {

	//private Class entityClass = GenericSuperClassUtil.getActualTypeClass(this.getClass());
	
	private SessionFactory sessionFactory;
		
	@Resource(name="sessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected Session getCurrentSession() {
		return this.sessionFactory.getCurrentSession();
	}


	public BaseDAOImpl() {
		super();
	}

	/*
	 * 批量操作时的批量数
	 */
	@SuppressWarnings("rawtypes")
	private static final ThreadLocal threadFlushSize = new ThreadLocal();

	@SuppressWarnings("rawtypes")
	private static final ThreadLocal threadOrder = new ThreadLocal();

	/**
	 * 根据主键查找记录
	 * 
	 * @param beanName
	 * @param abstractID
	 * @param lock
	 * @return
	 * @throws DaoException
	 */
	protected T getBeanById(String beanName, Serializable id,
			boolean lock) throws DaoException {

		Object bean = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			if (lock) {
				//SQLSERVER不能这样用
				bean = session.get(beanName, id,
						LockOptions.UPGRADE);
			} else {
				bean = session.get(beanName, id);
			}
		} catch (Exception ex) {
			throw new DaoException("Get bean by id fail!" + ex.getMessage(), ex);
		}
		return (T)bean;
	}

	/**
	 * 根据sqlName得到相应流水号
	 * 
	 * @param sqlName
	 * @return String
	 * @throws DaoException
	 */
	public String getSequenceByName(String sqlName) throws DaoException {

		Session session = sessionFactory.getCurrentSession();
		String seq = null;
		try {
			seq = (String) session.getNamedQuery(sqlName).uniqueResult();

		} catch (Exception ex) {
			throw new DaoException("Get sequence by hqlName fail!"
					+ ex.getMessage());
		}
		return seq;
	}

	/**
	 * 返回所有记录
	 * 
	 * @param beanName
	 * @return Collection
	 * @throws DaoException
	 */
	protected Collection<T> getAll(String beanName) throws DaoException {

		Collection<T> beans;
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
					beanName);

			Vector<Order> orderVector = getOrderVector();
			Iterator<Order> it = orderVector.iterator();
			while (it.hasNext()) {
				criteria.addOrder((Order) it.next());
			}
			orderVector.clear();
			beans = criteria.list();
		} catch (Exception ex) {
			throw new DaoException("Get all records fail!" + ex.getMessage(), ex);
		}
		return beans;
	}

	/**
	 * 根据条件bean查找所有符合条件的记录
	 * 
	 * @param bean
	 * @param mode
	 * @return
	 * @throws DaoException
	 */
	public <T> Collection<T> getBeansByBean(T bean, MatchMode mode)
			throws DaoException {
		Collection<T> collection = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			Example example = Example.create(bean).excludeNone()
					.excludeZeroes();
			if (!mode.toString().equals(MatchMode.EXACT.toString())) {
				example.enableLike(mode);
			}
			
			Criteria criteria = session.createCriteria(bean.getClass()).add(
					example);
			appendIdToQuery(bean, criteria, mode);

			Vector<Order> orderVector = getOrderVector();
			Iterator<Order> it = orderVector.iterator();
			while (it.hasNext()) {
				criteria.addOrder(it.next());
			}
			orderVector.clear();
			collection = criteria.list();
		} catch (Exception ex) {
			throw new DaoException("Get match records by bean fail!"
					+ ex.getMessage());
		}
		return collection;
	}

	/**
	 * 根据条件bean查询，返回符合条件的第一条记录
	 * 
	 * @param bean
	 * @param mode
	 * @return
	 * @throws DaoException
	 */
	protected T getProtoBeanByBean(T bean, MatchMode mode)
			throws DaoException {
		T resultBean = null;
		try {
			Collection<T> collection = getBeansByBean(bean, mode);
			if (collection != null) {
				Iterator<T> it = collection.iterator();
				if (it.hasNext()) {
					resultBean = it.next();
				}
			}
		} catch (Exception ex) {
			throw new DaoException("Get the first match record by bean fail!"
					+ ex.getMessage(), ex);
		}
		return resultBean;
	}

	/**
	 * 根据命名hql语句查询符合条件的记录
	 * 
	 * @param hqlName
	 * @param paraList
	 * @return Collection
	 * @throws DaoException
	 */
	public Collection<T> getBeansByParams(String hqlName, ArrayList<String> paraList)
			throws DaoException {
		Collection<T> collection = null;
		int paraSize = paraList.size();
		try {
			Query q = sessionFactory.getCurrentSession().getNamedQuery(hqlName);
			for (int i = 0; i < paraSize; i++)
				q.setParameter(i, paraList.get(i));
			collection = q.list();
		} catch (Exception ex) {
			throw new DaoException("Get match records by hqlName fail!"
					+ ex.getMessage());
		}
		return collection;
	}

	/**
	 * 根据命名hql语句查询符合条件的记录
	 * 
	 * @param hqlName
	 * @param paraList
	 * @return
	 * @throws DaoException
	 */
	protected T getProtoBeanByParams(String hqlName,
			ArrayList<String> paraList) throws DaoException {
		T bean = null;
		try {
			Collection<T> collection = getBeansByParams(hqlName, paraList);
			if (collection != null) {
				Iterator<T> it = collection.iterator();
				if (it.hasNext()) {
					bean = (T) it.next();
				}
			}

		} catch (Exception ex) {
			throw new DaoException("Get the first match bean by hqlName fail!"
					+ ex.getMessage());
		}
		return bean;
	}

	/**
	 * 根据命名hql语句查询符合条件的object
	 * 
	 * @param hqlName
	 * @param paraList
	 * @return Object
	 * @throws DaoException
	 */
	public Object getObjectByParams(String hqlName, ArrayList<String> paraList)
			throws DaoException {
		Object obj = null;
		try {
			Collection<T> collection = getBeansByParams(hqlName, paraList);
			Iterator<T> it = collection.iterator();
			if (it.hasNext()) {
				obj = (Object) it.next();
			}
		} catch (Exception ex) {
			throw new DaoException(
					"Get the first match object by hqlName fail!"
							+ ex.getMessage());
		}
		return obj;
	}

	/**
	 * 保存记录
	 * 
	 * @param bean
	 * @param flag
	 * @throws DaoException
	 */
	public void makePersistent(T bean, boolean flag)
			throws DaoException {

		try {
			if (flag)
			{
				sessionFactory.getCurrentSession().saveOrUpdate(bean);
			}
			else
			{
				sessionFactory.getCurrentSession().save(bean);
			}
		} catch (Exception ex) {
			throw new DaoException("Save the bean fail!" + ex.getMessage(), ex);
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param bean
	 * @throws DaoException
	 */
	public void makeTransient(T bean) throws DaoException {

		try {
			sessionFactory.getCurrentSession().delete(bean);
		} catch (Exception ex) {
			throw new DaoException("Delete the bean fail!" + ex.getMessage(), ex);
		}
	}

	/**
	 * 更新记录
	 * 
	 * @param bean
	 * @throws DaoException
	 */
	public void updateBean(T bean) throws DaoException {
		try {
			sessionFactory.getCurrentSession().update(bean);
		} catch (Exception ex) {
			throw new DaoException("Update the bean fail!" + ex.getMessage(), ex);
		}
	}

	/**
	 * 根据命名hql语句删除或更新相应数据库表记录
	 * 
	 * @param hqlName
	 * @param paraList
	 * @throws DaoException
	 */
	public void deleteOrUpdateByParams(String hqlName, ArrayList<String> paraList)
			throws DaoException {
		int paraSize = paraList.size();
		try {
			Query q = sessionFactory.getCurrentSession().getNamedQuery(hqlName);
			for (int i = 0; i < paraSize; i++)
				q.setParameter(i, paraList.get(i));

			q.executeUpdate();
			sessionFactory.getCurrentSession().flush();
		} catch (Exception ex) {
			throw new DaoException(
					"Delete or update the match records by hqlName fail!"
							+ ex.getMessage(), ex);
		}
	}
	
	/**
	 * 根据命名hql语句删除或更新相应数据库表记录
	 * 
	 * @param hqlName
	 * @param paraList
	 * @throws DaoException
	 */
	public void deleteOrUpdateByParams(String hqlName, Map<String, Object> paraMap)
			throws DaoException {
		try {
			Query q = sessionFactory.getCurrentSession().getNamedQuery(hqlName);
			setParameter(q, paraMap);
			q.executeUpdate();
			sessionFactory.getCurrentSession().flush();
		} catch (Exception ex) {
			throw new DaoException(
					"Delete or update the match records by hqlName fail!"
							+ ex.getMessage(), ex);
		}
	}

	/**
	 * 批量插入
	 * 
	 * @param beans
	 * @throws DaoException
	 */
	public void batchInsert(Collection<T> beans) throws DaoException {
		int count = 0;
		if (beans != null) {
			try {
				Iterator<T> it = beans.iterator();
				while (it.hasNext()) {
					count++;
					this.makePersistent(it.next(), false);
					if ((count % getHbmFlushSize()) == 0) {
						sessionFactory.getCurrentSession().flush();
						sessionFactory.getCurrentSession().clear();
					}
				}
				sessionFactory.getCurrentSession().flush();
				sessionFactory.getCurrentSession().clear();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new DaoException("Batch insert records fail!"
						+ ex.getMessage() + "count：" + count);
			}
		}
	}

	/**
	 * 批量更新
	 * 
	 * @param beans
	 * @throws DaoException
	 */
	public void batchUpdate(Collection<T> beans) throws DaoException {
		int count = 0;
		if (beans != null) {
			try {
				Iterator<T> it = beans.iterator();
				while (it.hasNext()) {
					count++;
					this.updateBean( it.next());
					if ((count % getHbmFlushSize()) == 0) {
						sessionFactory.getCurrentSession().flush();
						sessionFactory.getCurrentSession().clear();
					}
				}
				sessionFactory.getCurrentSession().flush();
				sessionFactory.getCurrentSession().clear();
			} catch (Exception ex) {
				throw new DaoException("Batch update records fail!"
						+ ex.getMessage(), ex);
			}
		}
	}

	/**
	 * 批量删除
	 * 
	 * @param beans
	 * @throws DaoException
	 */
	public void batchDelete(Collection<T> beans) throws DaoException {
		int count = 0;
		if (beans != null) {
			try {
				Iterator<T> it = beans.iterator();
				while (it.hasNext()) {
					count++;
					this.makeTransient(it.next());
					if ((count++ % getHbmFlushSize()) == 0) {
						sessionFactory.getCurrentSession().flush();
						sessionFactory.getCurrentSession().clear();
					}
				}
				sessionFactory.getCurrentSession().flush();
				sessionFactory.getCurrentSession().clear();
			} catch (Exception ex) {
				throw new DaoException("Batch delete records fail!"
						+ ex.getMessage());
			}
		}
	}

	/**
	 * batch operation.update if the record has already in database,if not then
	 * insert
	 * 
	 * @param beans
	 * @throws DaoException
	 */
	public void batchUpdateOrInsert(Collection<T> beans) throws DaoException {
		int count = 0;
		if (beans != null) {
			try {
				Iterator<T> it = beans.iterator();
				while (it.hasNext()) {
					count++;
					this.makePersistent(it.next(), true);
					if ((count % getHbmFlushSize()) == 0) {
						sessionFactory.getCurrentSession().flush();
						sessionFactory.getCurrentSession().clear();
					}
				}
				sessionFactory.getCurrentSession().flush();
				sessionFactory.getCurrentSession().clear();
			} catch (Exception ex) {
				throw new DaoException("Batch update or insert records fail!"
						+ ex.getMessage());
			}
		}
	}

	/**
	 * 执行更新操作的hql语句
	 * 
	 * @param hqlName,paraList
	 * @throws DaoException
	 */
	public int executeUpdateHql(String hqlName, ArrayList<String> paraList)
			throws DaoException {
		int paraSize = paraList.size();
		try {
			Query q = sessionFactory.getCurrentSession().getNamedQuery(hqlName);
			for (int i = 0; i < paraSize; i++)
				q.setParameter(i, paraList.get(i));
			return q.executeUpdate();
		} catch (Exception ex) {
			throw new DaoException("Execute update hql fail!" + ex.getMessage());
		}
	}

	/**
	 * 执行更新操作的hql语句
	 * 
	 * @param hqlName
	 * @param table
	 * @throws DaoException
	 */
	public int executeUpdateHql(String hqlName, Map<String, Object> map)
			throws DaoException {
		try {
			Query q = sessionFactory.getCurrentSession().getNamedQuery(hqlName);

			if (map != null) {
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String paramName = it.next().toString();
					Object paramValue = map.get(paramName);
					if (paramValue instanceof Object[])
						q.setParameterList(paramName, (Object[]) paramValue);
					else if (paramValue instanceof Collection)
						q.setParameterList(paramName, (Collection<Object>) paramValue);
					else
						q.setParameter(paramName, paramValue);
				}
			}
			return q.executeUpdate();
		} catch (Exception ex) {
			throw new DaoException("Execute update hql fail!" + ex.getMessage());
		}
	}

	/**
	 * 开启过滤器
	 * 
	 * @param filterName,table
	 * @throws DaoException
	 */
	public void enableFilter(String filterName, Map<String, Object> map)
			throws DaoException {
		try {
			Filter filter = sessionFactory.getCurrentSession().enableFilter(filterName);
			if (map != null) {
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String paramName = it.next().toString();
					Object paramValue = map.get(paramName);
					if (paramValue instanceof Object[])
						filter.setParameterList(paramName,
								(Object[]) paramValue);
					else if (paramValue instanceof Collection)
						filter.setParameterList(paramName,
								(Collection) paramValue);
					else
						filter.setParameter(paramName, paramValue);
				}
			}
		} catch (Exception ex) {
			throw new DaoException("Enable filter by filter name fail!"
					+ ex.getMessage());
		}

	}

	/**
	 * 关闭过滤器
	 * 
	 * @param filterName
	 * @throws DaoException
	 * @return void
	 */
	public void disableFilter(String filterName) throws DaoException {
		try {
			sessionFactory.getCurrentSession().disableFilter(filterName);
		} catch (Exception ex) {
			throw new DaoException("Disable filter by filter name fail!"
					+ ex.getMessage());
		}
	}

	/**
	 * 设定批量操作的批量数
	 * 
	 * @param hbmFlushSize
	 */
	@SuppressWarnings("unchecked")
	public void setHbmFlushSize(int hbmFlushSize) {
		threadFlushSize.set(new Integer(hbmFlushSize));
	}

	/**
	 * 返回批量操作的批量数
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int getHbmFlushSize() {
		Integer hbmFlushSize = (Integer) threadFlushSize.get();

		if (hbmFlushSize == null) {
			hbmFlushSize = new Integer(30);
			threadFlushSize.set(hbmFlushSize);
		}

		return hbmFlushSize.intValue();
	}

	/**
	 * 增加排序条件到排序集合
	 * 
	 * @param order
	 */
	public IBaseDAO<T> addOrder(Order order) {
		getOrderVector().add(order);
		return this;
	}

	/**
	 * 返回排序条件集合
	 * 
	 * @return
	 */
	public Vector<Order> getOrderVector() {
		Vector<Order> orderVector = (Vector<Order>) threadOrder.get();

		if (orderVector == null) {
			orderVector = new Vector<Order>();
			threadOrder.set(orderVector);
		}

		return orderVector;
	}

	/**
	 * 如果传入bean的id的值不为空，则添加到查询语句里作为查询条件
	 * 
	 * @param bean
	 * @param criteria
	 * @param mode
	 * @throws Exception
	 */
	private <T> void appendIdToQuery(T bean, Criteria criteria,
			MatchMode mode) throws Exception {
		ClassMetadata cm = sessionFactory.getClassMetadata(
				bean.getClass());
		String idName = cm.getIdentifierPropertyName();
		Object idType = (Object) cm.getIdentifierType();

		BeanMap beanMap = new BeanMap(bean);
		Object idValue = beanMap.get(idName);

		if (idValue != null) {

			Class idClass = idValue.getClass();
			/* 符合主键 */
			if (idType instanceof ComponentType) {
				Method[] methods = idClass.getMethods();
				try {
					for (int i = 0; i < methods.length; i++) {
						if (methods[i].getName().toUpperCase().indexOf("GET") != -1
								&& methods[i].invoke(idValue, null) != null
								&& !methods[i].getName().equals("getClass")) {
							String propertyName = methods[i].getName()
									.substring(3);
							propertyName = propertyName.toLowerCase()
									.substring(0, 1)
									+ propertyName.substring(1);
							if (methods[i].getReturnType().getName().equals(
									"java.lang.String"))
								if (mode.toString().equals(
										MatchMode.EXACT.toString())) {
									criteria.add(Restrictions.eq(idName + "."
											+ propertyName, methods[i].invoke(
											idValue, null).toString()));
								} else {
									criteria.add(Restrictions.like(idName + "."
											+ propertyName, methods[i].invoke(
											idValue, null).toString(), mode));
								}
							else {
								criteria.add(Restrictions.eq(idName + "."
										+ propertyName, methods[i].invoke(
										idValue, null)));
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					throw ex;
				}
			}
			/* String型主键 */
			else if (idType instanceof StringType) {
				if (mode.toString().equals(MatchMode.EXACT.toString())) {
					criteria.add(Restrictions.eq(idName, idValue.toString()));
				} else {
					criteria.add(Restrictions.like(idName, idValue.toString(),
							mode));
				}
			}
			/* 其它类型 */
			else {
				criteria.add(Restrictions.eq(idName, idValue));
			}
		}
	}

	/**
	 * 如果传入bean的值不为空，则添加到查询语句里作为查询条件,id除外
	 * 
	 * @param bean
	 * @param criteria
	 * @param mode
	 * @throws Exception
	 */
	private void appendBeanToQuery(T bean, Criteria criteria,
			MatchMode mode) throws Exception {
		ClassMetadata cm = sessionFactory.getClassMetadata(
				bean.getClass());
		String[] propertyNames = cm.getPropertyNames();

		BeanMap beanMap = new BeanMap(bean);

		for (int i = 0; i < propertyNames.length; i++) {
			Object propertyValue = beanMap.get(propertyNames[i]);
			Object propertyType = cm.getPropertyType(propertyNames[i]);
			if (propertyValue != null) {
				/* String型主键 */
				if (propertyType instanceof StringType) {
					if (mode.toString().equals(MatchMode.EXACT.toString())) {
						criteria.add(Restrictions.eq(propertyNames[i],
								propertyValue.toString()));
					} else {
						criteria.add(Restrictions.like(propertyNames[i],
								propertyValue.toString(), mode));
					}
				}
				/* 其它类型 */
				else {
					criteria.add(Restrictions.eq(propertyNames[i], propertyValue));
				}
			}

		}
	}

	/**
	 * 获取结果集的数量
	 * 
	 * @param bean
	 * @param mode
	 * @return
	 * @throws DaoException
	 */
	public int getCountsByBean(T bean, MatchMode mode)
			throws DaoException {
		int count = 0;
		Session session = sessionFactory.getCurrentSession();
		try {
			Example example = Example.create(bean).excludeNone()
					.excludeZeroes();
			if (!mode.toString().equals(MatchMode.EXACT.toString())) {
				example.enableLike(mode);
			}

			Criteria criteria = session.createCriteria(bean.getClass())
									.setProjection( Projections.rowCount() )
									.add(example);
			appendIdToQuery(bean, criteria, mode);

			count = ((Integer) criteria.uniqueResult()).intValue();

		} catch (Exception ex) {
			throw new DaoException("Get count(*) by bean fail!"
					+ ex.getMessage());
		}

		return count;
	}

	/**
	 * 获取oracle的sequence
	 */
	public Long getOracleSequence(String seqName) {
		String sql = "select " + seqName + ".nextval from dual";
		PreparedStatement ps = null;
		Long seq = null;
		try {
			ps = (((SessionImplementor)sessionFactory.getCurrentSession()).connection()).prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				seq = new Long(rs.getLong(1));
			}
			
			return seq;
		} catch (Exception e) {
			throw new HibernateException("Get oracle sequence fail"
					+ e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				throw new HibernateException("Close preparedStatement fail!"
						+ e.getMessage());
			}
		}
	}

	/**
	 * 执行本地 ddl sql
	 * 
	 * @param ddlSql
	 */
	public void execDllSql(String ddlSql) {
		PreparedStatement ps = null;
		try {
			ps = (((SessionImplementor)sessionFactory.getCurrentSession()).connection()).prepareStatement(ddlSql);
			ps.execute();
		} catch (Exception e) {
			throw new HibernateException("Exec ddl sql fail" + e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				throw new HibernateException("Close preparedStatement fail!"
						+ e.getMessage());
			}
		}
	}

	/**
	 * 通过sqlName获取本地ddl sql并执行
	 * 
	 * @param sqlName
	 */
	public void execDllSqlBySqlName(String sqlName) {

		PreparedStatement ps = null;
		try {
			String sql = sessionFactory.getCurrentSession().getNamedQuery(sqlName)
					.getQueryString();
			ps = (((SessionImplementor)sessionFactory.getCurrentSession()).connection()).prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			throw new HibernateException("Exec ddl sql fail" + e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				throw new HibernateException("Close preparedStatement fail!"
						+ e.getMessage());
			}
		}
	}

	@Override
	public T getBeanByID(String beanName ,String id, boolean lock) throws DaoException {
		T bean = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			if (lock) {
				bean =  (T) session.get(beanName, id,
						LockOptions.UPGRADE);
			} else {
				bean =  (T) session.get(beanName, id);
			}
		} catch (Exception ex) {
			throw new DaoException("Get bean by id fail!" + ex.getMessage(), ex);
		}
		return bean;
	}
	
	@Override
	public void deleteByIDs(String beanName, String[] ids) {
		// TODO Auto-generated method stub
		for(Serializable id : ids) {
			Object entity = this.getCurrentSession().get(beanName, id);
			this.getCurrentSession().delete(entity);
		}
	}

	@Override
	public void deleteByCollection(Collection<T> list) {
		// TODO Auto-generated method stub
		for(T entity : list) {
			this.getCurrentSession().delete(entity);
		}
	}
	
	/**
	 * 用于Map类型传参
	 * */
	protected Query setParameter(Query query, Map<String, Object> map) {  
        if (map != null) {  
            Set<String> keySet = map.keySet();  
            for (String string : keySet) {  
                Object obj = map.get(string);  
                //这里考虑传入的参数是什么类型，不同类型使用的方法不同  
                if(obj instanceof Collection<?>){  
                    query.setParameterList(string, (Collection<?>)obj);  
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }  
        }  
        return query;  
    } 
	
	public long getBeansCount(String hqlName, ArrayList<String> paraList) {
		int paraSize = paraList.size();
		long totalCount = 0;
		try {
			Query q = this.getSessionFactory().getCurrentSession().getNamedQuery(hqlName);
			for (int i = 0; i < paraSize; i++)
				q.setParameter(i, paraList.get(i));
			totalCount =Long.parseLong(""+q.list().get(0));  
		} catch (Exception ex) {
			throw new DaoException("Get match records by hqlName fail!", ex);
		}
		return totalCount;
	}

	@Override
	public long getBeansCount(String hqlName, Map<String, Object> paraMap)
			throws DaoException {
		long totalCount = 0;
		try {
			Query q = this.getSessionFactory().getCurrentSession().getNamedQuery(hqlName);
			setParameter(q, paraMap);
			totalCount =Long.parseLong(""+q.list().get(0));  
		} catch (Exception ex) {
			throw new DaoException("Get match records by hqlName fail!", ex);
		}
		return totalCount;
	}

	@Override
	public Collection<T> getBeansByParams(String hqlName,
			Map<String, Object> paraMap) throws DaoException {
		Collection<T> collection = null;
		try {
			Query q = sessionFactory.getCurrentSession().getNamedQuery(hqlName);
			setParameter(q, paraMap);
			collection = q.list();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DaoException("Get match records by hqlName fail!"
					+ ex.getMessage());
		}
		return collection;
	}

	@Override
	public Object[] getObjectsByParams(String hqlName,
			Map<String, Object> paraMap) throws DaoException {
		Object[] ts = null;
		try {
			Collection<T> collection = getBeansByParams(hqlName, paraMap);
			if (collection != null) {
				collection.toArray();
			}

		} catch (Exception ex) {
			throw new DaoException(
					"Get the first match object array by hqlName fail!"
							+ ex.getMessage());
		}
		return null;
	}
	
	/**
	 * Object[] objects = user_dao.getObjectsByParams("testGetObjectsByParams", userId);
	 * 1.testGetObjectsByParams = from UMUser as u,UMRole as r,UMRoleUMUser as ru
		where u.id = ru.user.id and r.id = ru.role.id and u.validate = 1
		and u.id = ?
		这个方法将会返回的是一行数据返回的是三个对象:
		UMUser u = (UMUser)objects[0][1];
		UMRole r = (UMRole)objects[0][2];
		UMRoleUMUser r = (UMRoleUMUser)objects[0][3];
	 * 
	 * */
	public Object[] getObjectsByParams(String hqlName, ArrayList<String> paraList)
			throws DaoException {
		try {
			Collection<T> collection = getBeansByParams(hqlName, paraList);
			if (collection != null) {
				return collection.toArray();
			}
		} catch (Exception ex) {
			throw new DaoException(
					"Get the first match object array by hqlName fail!"
							+ ex.getMessage());
		}

		return null;
	}
}