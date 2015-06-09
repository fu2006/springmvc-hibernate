package com.mvc.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.sun.mail.pop3.POP3Folder;

public class ReceiveMail {
	private MimeMessage msg = null;
	private String saveAttchPath = "";
	private StringBuffer bodytext = new StringBuffer();
	private String dateformate = "yyyy-MM-dd HH:mm";
	
	private static final List<String> ADDRESS_TYPE = Arrays.asList(new String[]{"TO", "CC", "BCC"});

	public ReceiveMail(MimeMessage msg) {
		super();
		this.msg = msg;
	}

	/**
	 * 获取发送邮件人信息
	 * @return
	 * @throws MessagingException
	 */
	public String getFrom() throws MessagingException{
		InternetAddress[] address = (InternetAddress[]) msg.getFrom();
		String from = address[0].getAddress() == null ? "" : address[0].getAddress();
		String personal = address[0].getPersonal() == null ? "" : address[0].getPersonal();
		return personal + "<" + from + ">";
	}
	
	/**
	 * 获取邮件收件人，抄送，密送的地址和信息。根据所传递的参数不同 "to"-->收件人,"cc"-->抄送人地址,"bcc"-->密送地址
	 * @param type
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public String getMailAddress(String type) throws MessagingException, UnsupportedEncodingException{
		String mailaddr = "";
		String addrType = type.toUpperCase();
		InternetAddress[] address = null;
		if(ADDRESS_TYPE.contains(addrType)) {
			if("TO".equals(addrType)) {
				address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.TO);
			}else if("CC".equals(addrType)) {
				address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.CC);
			}else if("BCC".equals(addrType)) {
				address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.BCC);
			}
			
			if(address != null) {
				for (int i = 0; i < address.length; i++) {
					String mail = address[i].getAddress() == null ? "" : MimeUtility.decodeText(address[i].getAddress());
					String personal = address[i].getPersonal() == null ? "" : MimeUtility.decodeText(address[i].getPersonal());
					String compositeto = personal + "<" + mail + ">";
					mailaddr += "," + compositeto;
				}
			}
		} else {
			throw new RuntimeException("Error email type!");
		}
		return mailaddr;
	}

	/**
	 * 获取邮件主题
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public String getSubject() throws MessagingException, UnsupportedEncodingException {
		String subject = MimeUtility.decodeText(msg.getSubject());
		return subject == null ? "" : subject;
	}
	
	/**
	 * 获取邮件发送日期
	 * @return
	 * @throws MessagingException
	 */
	public String getSendDate() throws MessagingException {
		return new SimpleDateFormat(dateformate).format(msg.getSentDate());
	}
	
	/**
	 * 获取邮件正文内容
	 * @return
	 */
	public String getBodyText() {
		return bodytext.toString();
	}
	
	/**
	 * 解析邮件，将得到的邮件内容保存到一个stringBuffer对象中，解析邮件 主要根据MimeType的不同执行不同的操作，一步一步的解析
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void getMailContent(Part part) throws MessagingException, IOException {
		boolean conName = part.getContentType().indexOf("name") == -1 ? false : true;
		if(!conName && (part.isMimeType("text/plain") || part.isMimeType("text/html"))) {
			bodytext.append((String) part.getContent());
		} else if(part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if(part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		}
	}
	
	/**
	 * 判断邮件是否需要回执，如需回执返回true，否则返回false
	 * @return
	 * @throws MessagingException
	 */
	public boolean getReplySign() throws MessagingException {
		return msg.getHeader("Disposition-Notification-TO") == null ? false : true;
	}
	
	/**
	 * 获取此邮件的message-id
	 * @return
	 * @throws MessagingException
	 */
	public String getMessageId() throws MessagingException {
		return msg.getMessageID();
	}
	
	/**
	 * 判断此邮件是否已读，如果未读则返回false，已读返回true
	 * @return
	 * @throws MessagingException
	 */
	public boolean isNew() throws MessagingException {
		boolean isnew = false;
		Flags flags = ((Message) msg).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags length : " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if(flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				break;
			}
		}
		return isnew;
	}
	
	/**
	 * 判断是否包含附件
	 * @param part
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean isContainAttch(Part part) throws MessagingException, IOException{
		boolean flag = false;
		if(part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String dispostion = bodyPart.getDisposition();
				if(dispostion != null && (dispostion.equals(Part.ATTACHMENT) || dispostion.equals(Part.INLINE))) {
					flag = true;
				} else if(bodyPart.isMimeType("multipart/*")) {
					flag = isContainAttch(bodyPart);
				} else if(bodyPart.getContentType().toLowerCase().indexOf("application") != -1 
						|| bodyPart.getContentType().toLowerCase().indexOf("name") != -1) {
					flag = true;
				}
			}
		} else if(part.isMimeType("message/rfc822")) {
			flag = isContainAttch((Part) part.getContent());
		}
		return flag;
	}
	
	/**
	 * 保存附件
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void saveAttchMent(Part part) throws MessagingException, IOException {
		String filename = "";
		if(part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String dispostion = bodyPart.getDisposition();
				if(dispostion != null && (dispostion.equals(Part.ATTACHMENT) || dispostion.equals(Part.INLINE))) {
					filename = bodyPart.getFileName();
					if(filename.toLowerCase().indexOf("gb2312") != -1) {
						filename = MimeUtility.decodeText(filename);
					}
					saveFile(filename, bodyPart.getInputStream());
				} else if(bodyPart.isMimeType("multipart/*")) {
					saveAttchMent(bodyPart);
				} else {
					filename = bodyPart.getFileName();
					if(filename != null && filename.toLowerCase().indexOf("gb2312") != -1) {
						filename = MimeUtility.decodeText(filename);
						saveFile(filename, bodyPart.getInputStream());
					}
				}
			}
		} else if(part.isMimeType("message/rfc822")) {
			saveAttchMent((Part) part.getContent());
		}
	}
	
	/**
	 * 获取保存附件的地址
	 * @return
	 */
	public String getSaveAttchPath() {
		return saveAttchPath;
	}

	/**
	 * 设置保存附件的地址
	 * @param saveAttchPath
	 */
	public void setSaveAttchPath(String saveAttchPath) {
		this.saveAttchPath = saveAttchPath;
	}
	
	/**
	 * 设置日期格式
	 * @param dateformate
	 */
	public void setDateformate(String dateformate) {
		this.dateformate = dateformate;
	}
	
	/**
	 * 保存文件内容
	 * @param filename
	 * @param inputStream
	 * @throws IOException
	 */
	private void saveFile(String filename, InputStream inputStream) throws IOException {
		String osname = System.getProperty("os.name") == null ? "" : System.getProperty("os.name");
		String storedir = getSaveAttchPath();
		String sepatror = "";
		if(osname.toLowerCase().indexOf("win") != -1){
			sepatror = "//";
			storedir = storedir == null || "".equals(storedir) ? "D://temp" : storedir;
		} else {
			sepatror = "/";
			storedir = "/temp";
		}
		File filePath = new File(storedir + sepatror);
		File storefile = new File(storedir + sepatror + filename);
		System.out.println("storefile path : " + storefile.getPath());
		if(!filePath.exists()){
			filePath.mkdirs();
		}
		
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(inputStream);
			int r;
			while((r = bis.read()) != -1) {
				bos.write(r);
				bos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bos.close();
			bis.close();
		}
	}
	
	public void receive(Part part, String uname, String uid) throws MessagingException,IOException  {
		boolean flag = isContainAttch(part);
		if(flag) {
			System.out.println("--------------------------start-----------------------------------");
			System.out.println("Message " + uid + " subject : " + getSubject());
			System.out.println("Message " + uid + " from : " + getFrom());
			System.out.println("Message " + uid + " isNew : " + isNew());
			System.out.println("Message " + uid + " isContainAttch : " + flag);
			System.out.println("Message " + uid + " replySign : " + getReplySign());
			getMailContent(part);
			System.out.println("Message " + uid + " content : " + getBodyText());
			setSaveAttchPath("D://MAILTEST//" + uname + "//" + uid);
			if(flag) {
				//saveAttchMent(part);
			}
			System.out.println("---------------------------end------------------------------------");
		}
	}
	
	public static void main(String[] args) throws MessagingException, IOException, NoSuchAlgorithmException {
		Properties props = new Properties();
		props.setProperty("mail.stmp.host", "mail.eco-city.gov.cn");
		props.setProperty("mail.stmp.auth", "true");
		Session session = Session.getDefaultInstance(props, null);
		URLName urlName = new URLName("pop3", "mail.eco-city.gov.cn", 110, null, "fuq", "fuqiang&ecomail");
		Store store = session.getStore(urlName);
		store.connect();
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message[] msgs = folder.getMessages();
		ReceiveMail rm = null;
		for (int i = 0; i < msgs.length; i++) {
			rm = new ReceiveMail((MimeMessage) msgs[i]);
			rm.receive(msgs[i], "fuq", rm.getUID(folder, msgs[i]));
		}
	}
	
	/**
	 * 获取邮件messageid
	 * @param folder
	 * @param msg
	 * @return
	 * @throws MessagingException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String getUID(Folder folder, Message msg) throws MessagingException, NoSuchAlgorithmException, UnsupportedEncodingException {
		final String hexString = "0123456789ABCDEF";
		POP3Folder in = (POP3Folder) folder;
		byte[] bytes = in.getUID(msg).getBytes();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			stringBuffer.append(hexString.charAt((bytes[i]&0xf0)>>4));
			stringBuffer.append(hexString.charAt((bytes[i]&0x0f)>>0));
		}
		return stringBuffer.toString();
	}
}
