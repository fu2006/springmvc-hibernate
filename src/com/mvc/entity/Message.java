package com.mvc.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	@Column(name = "uid")
	private String uid;
	@Column(name = "subject")
	private String subject;
	@Column(name = "fromname")
	private String fromname;
	@Column(name = "senddate")
	private String senddate;
	@Column(name = "iscontainattachment")
	private boolean iscontainattachment;
	@Column(name = "replysign")
	private boolean replysign;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;
	@Column(name = "attachmentpath")
	private String attachmentpath;
	@Column(name = "ispush")
	private boolean ispush;
	@ManyToOne
	@JoinColumn(name = "userInfoId", updatable = false)
	private UserInfo userInfo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFromname() {
		return fromname;
	}

	public void setFromname(String fromname) {
		this.fromname = fromname;
	}

	public void setIspush(boolean ispush) {
		this.ispush = ispush;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttachmentpath() {
		return attachmentpath;
	}

	public void setAttachmentpath(String attachmentpath) {
		this.attachmentpath = attachmentpath;
	}

	public boolean isIscontainattachment() {
		return iscontainattachment;
	}

	public void setIscontainattachment(boolean iscontainattachment) {
		this.iscontainattachment = iscontainattachment;
	}

	public boolean isReplysign() {
		return replysign;
	}

	public void setReplysign(boolean replysign) {
		this.replysign = replysign;
	}

	public boolean isIspush() {
		return ispush;
	}

	public String getSenddate() {
		return senddate;
	}

	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	

}
