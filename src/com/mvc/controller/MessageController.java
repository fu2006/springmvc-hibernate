package com.mvc.controller;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mvc.entity.Message;
import com.mvc.entity.UserInfo;
import com.mvc.service.MessageService;

@Controller
@RequestMapping("/message")
public class MessageController {
	protected final transient Log log = LogFactory
	.getLog(MessageController.class);
	@Autowired
	private MessageService messageService;
	public MessageController(){
		
	}
	
	@RequestMapping
	public String load(ModelMap modelMap){
		List<Object> list = messageService.getMessageList();
		modelMap.put("list", list);
		return "message";
	}
	
	@RequestMapping(value = "/{uid}")
	public String show(@PathVariable String uid, ModelMap modelMap) {
		Message message = messageService.getByUid(uid);
		modelMap.put("message", message);
		return "show";
	}
	
	@RequestMapping(params = "method=push")
	public void push(){
		try{
			UserInfo userInfo = new UserInfo();
			userInfo.setMobile("15620599521");
			userInfo.setLoginname("fuq");
			userInfo.setPassword("fuqiang&ecomail");
			messageService.pushMessages(userInfo);
		}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
}
