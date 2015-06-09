package com.mvc.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.panda.httputil.i.HTTPFactory;
import cn.panda.httputil.impl.HTTPFactoryImpl;

import com.mvc.dao.EntityDao;
import com.mvc.entity.Message;
import com.mvc.entity.UserInfo;
import com.mvc.util.ReceiveMail;

@Service
public class EmailTaskManager {
	@Autowired
	private EntityDao entityDao;
	
	private Properties props = null;
	
	public EmailTaskManager() {
		props = new Properties();
		InputStream in = MessageService.class.getResourceAsStream("/email.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//@Scheduled(fixedDelay = 30000)
	public void doSomething() {
		List<Object> userInfos = getUserInfoList();
		for (Object obj : userInfos) {
			UserInfo userInfo = (UserInfo) obj;
			try {
				pushMessages(userInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Transactional
	public List<Object> getUserInfoList(){
		StringBuffer sff = new StringBuffer();
		sff.append("select a from ").append(UserInfo.class.getSimpleName()).append(" a ");
		List<Object> list = entityDao.createQuery(sff.toString());
		return list;
	}
	
	@Transactional
	public List<Object> getMessageList(){
		StringBuffer sff = new StringBuffer();
		sff.append("select a from ").append(Message.class.getSimpleName()).append(" a ");
		List<Object> list = entityDao.createQuery(sff.toString());
		return list;
	}
	
	@Transactional
	public List<Object> getMessageList2push(UserInfo userInfo){
		StringBuffer sff = new StringBuffer();
		sff.append("select a from ").append(Message.class.getSimpleName()).append(" a where ispush = 0 and userInfoId = ").append(userInfo.getUserid()).append(" order by senddate desc limit 50");
		List<Object> list = entityDao.createQuery(sff.toString());
		return list;
	}
	
	public void save(Message message){
		entityDao.save(message);
	}
	public void delete(Object obj){
		entityDao.delete(obj);
	}
	
	public void update(Message message) {
		entityDao.update(message);
	}
	
	/**
	 * 推送邮件
	 * @param userInfo
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void pushMessages(UserInfo userInfo) throws NoSuchAlgorithmException, IOException, MessagingException {
		receiveMessages(userInfo);
		List<Object> msgs = getMessageList2push(userInfo);
		if(msgs.size() == 0) {
			return;
		}
		StringBuffer msg_json = new StringBuffer();
		msg_json.append("MailSchema@{\"MailList\":[");
		for (Object object : msgs) {
			if(object != null) {
				Message msg = (Message) object;
				msg_json.append("{ \"MailID\": \"");
				msg_json.append(msg.getUid());
				msg_json.append("\", \"Title\": \"");
				msg_json.append(msg.getSubject());
				msg_json.append("\", \"PhoneTel\": \"");
				msg_json.append(userInfo.getMobile());
				msg_json.append("\", \"Url\": \"");
				msg_json.append(props.getProperty("show.url")).append(msg.getUid());
				msg_json.append("\", \"UserName\": \"");
				String username = msg.getFromname();
				username = username.indexOf("<") > -1 ? username.substring(0, username.indexOf("<")) : username;
				username = username.indexOf("@") > -1 ? username.substring(0, username.indexOf("@")) : username;
				msg_json.append(username);
				msg_json.append("\", \"SendDate\": \"");
				msg_json.append(msg.getSenddate());
				msg_json.append("\"},");
			}
		}
		if(msg_json.toString().endsWith(",")) {
			msg_json.deleteCharAt(msg_json.length() - 1);
		}
		msg_json.append("]}");
		HTTPFactory httpFactory = new HTTPFactoryImpl();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("User-ID", userInfo.getMobile());
		headers.put("Content-Type", "data");
		headers.put("Charset", "UTF-8");
		headers.put("Connection", "Keep-Alive");
		headers.put("FR", userInfo.getMobile());
		boolean bool = httpFactory.postSend(props.getProperty("app.url"), headers, msg_json.toString());
		if(bool) {
			changeIsPush(msgs);
		}
	}
	
	/**
	 * 接收用户的新邮件
	 * @param userInfo
	 * @throws IOException
	 * @throws MessagingException
	 * @throws NoSuchAlgorithmException
	 */
	public void receiveMessages(UserInfo userInfo) throws IOException, MessagingException, NoSuchAlgorithmException {
		Session session = Session.getDefaultInstance(props);
		URLName urlName = new URLName(props.getProperty("protocol"), props.getProperty("mail.stmp.host"), Integer.parseInt(props.getProperty("port")), null, userInfo.getLoginname(), userInfo.getPassword());
		Store store = session.getStore(urlName);
		store.connect();
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		javax.mail.Message[] msgs = folder.getMessages();
		ReceiveMail rm = null;
		int length = msgs.length;
		int reloadcount = Integer.parseInt(props.getProperty("reloadcount"));
		List<Object> messages_db =  getMessageList();
		for (int i = length - 1; i > ( length >= reloadcount ? reloadcount : 0); i--) {
			rm = new ReceiveMail((MimeMessage) msgs[i]);
			String uid = rm.getUID(folder, msgs[i]);
			if(isNewMessage(messages_db, uid)) {
				Message msg = new Message();
				/**
				 * 添加筛选语句，只读取今天的邮件。
				 */
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String sendDate = rm.getSendDate().substring(0, 10);
				if(!sendDate.equals(sdf.format(new Date()))) {
					continue;
				}
				
				Part part = (Part) msgs[i];
				msg.setUserInfo(userInfo);
				msg.setUid(uid);
				msg.setSubject(rm.getSubject());
				msg.setSenddate(rm.getSendDate());
				msg.setFromname(rm.getFrom());
				msg.setIscontainattachment(rm.isContainAttch(part));
				msg.setReplysign(rm.getReplySign());
				rm.getMailContent(part);
				msg.setContent(rm.getBodyText());
				
				/*取消附件的接收
				 * if(msg.isIscontainattachment()) {
					String attachmentpath = props.getProperty("attachmentpath") + userInfo.getLoginname() + "//" + msg.getUid();
					rm.setSaveAttchPath(attachmentpath);
					rm.saveAttchMent(part);
					msg.setAttachmentpath(attachmentpath);
				} else {
					msg.setAttachmentpath("");
				}*/
				msg.setAttachmentpath("");
				msg.setIspush(false);
				save(msg);
			}
		}
	}
	
	private boolean isNewMessage(List<Object> msgs, String uid) {
		for(Object obj : msgs) 
			if(((Message) obj).getUid().equals(uid)) 
				return false;
		return true;
	}
	
	private void changeIsPush(List<Object> msgs) {
		for (Object object : msgs) {
			if(object != null) {
				Message msg = (Message) object;
				msg.setIspush(true);
				update(msg);
			}
		}
	}
	
}
