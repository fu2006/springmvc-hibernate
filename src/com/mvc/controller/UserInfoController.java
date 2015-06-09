package com.mvc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mvc.entity.UserInfo;
import com.mvc.service.UserInfoService;

@Controller
@RequestMapping("/user")
public class UserInfoController {
	protected final transient Log log = LogFactory.getLog(UserInfoController.class);
	@Autowired
	private UserInfoService userInfoService;
	
	@RequestMapping
	public String load(ModelMap modelMap) {
		List<Object> list = userInfoService.getUserInfoList();
		modelMap.put("list", list);
		return "userinfo";
	}
	
	@RequestMapping(value = "add")
	public String add(HttpServletRequest request, ModelMap value) throws Exception{
		return "user_add";
	}
	
	@RequestMapping(value = "/save")
	public String save(@ModelAttribute UserInfo userInfo, HttpServletRequest request, ModelMap modelMap){
		try{
			userInfoService.save(userInfo);
			modelMap.put("addstate", "添加成功");
		}
		catch(Exception e){
			log.error(e.getMessage());
			modelMap.put("addstate", "添加失败");
		}
		return "user_add";
	}
	
	@RequestMapping(value = "/del/{uid}")
	public void show(@PathVariable String uid, HttpServletResponse response) {
		try{
			UserInfo userInfo = (UserInfo) userInfoService.getById(Integer.parseInt(uid));
			userInfoService.delete(userInfo);
			response.getWriter().print("{\"del\":\"true\"}");
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
