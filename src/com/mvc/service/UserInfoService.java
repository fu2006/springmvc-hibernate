package com.mvc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvc.dao.EntityDao;
import com.mvc.entity.UserInfo;

@Service
public class UserInfoService {
	@Autowired
	private EntityDao entityDao;
	
	@Transactional
	public List<Object> getUserInfoList(){
		StringBuffer sff = new StringBuffer();
		sff.append("select a from ").append(UserInfo.class.getSimpleName()).append(" a ");
		List<Object> list = entityDao.createQuery(sff.toString());
		return list;
	}
	
	public void save(UserInfo userInfo){
		entityDao.save(userInfo);
	}
	
	public void saveOrUpdate(UserInfo userInfo){
		entityDao.saveOrUpdate(userInfo);
	}
	
	public void delete(Object obj){
		entityDao.delete(obj);
	}
	
	public Object getById(Integer id){
		return entityDao.getById(UserInfo.class.getName(), id);
	}
}
