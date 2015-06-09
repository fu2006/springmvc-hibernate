package com.mvc.dao;

import java.util.List;

public interface EntityDao {
	public List<Object> createQuery(final String queryString);
	public Object save(final Object model);
	public void update(final Object model);
	public void saveOrUpdate(final Object model);
	public void delete(final Object model);
	public Object getById(final String clazz,final Integer id);
}
