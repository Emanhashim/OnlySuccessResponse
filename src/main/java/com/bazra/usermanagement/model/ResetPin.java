package com.bazra.usermanagement.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
@Entity
@Table(name = "ResetPin")
public class ResetPin {
	
		@Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE)
	    private int id;
		
		private String pin;
		
		private LocalDateTime sentTime;
		private LocalDateTime expirationTime;
		private boolean isUsed;
		@OneToOne
		@JoinColumn(name = "userid")
		private UserInfo userInfo;
		@ManyToOne
		@JoinColumn(name = "sendingmedium_id")
		private CommunicationMedium sendingmedium;
		
		public UserInfo getUserInfo() {
			return userInfo;
		}

		public void setUserInfo(UserInfo userInfo) {
			this.userInfo = userInfo;
		}


		public CommunicationMedium getSendingmedium() {
			return sendingmedium;
		}

		public void setSendingmedium(CommunicationMedium sendingmedium) {
			this.sendingmedium = sendingmedium;
		}

		public void setId(int id) {
			this.id = id;
		}

		
		
		public boolean isUsed() {
			return isUsed;
		}

		public ResetPin() {
			super();
			// TODO Auto-generated constructor stub
		}

		public void setUsed(boolean isUsed) {
			this.isUsed = isUsed;
		}

		public int getId() {
			return id;
		}
		
	

		public String getPin() {
			return pin;
		}
		public void setPin(String pin) {
			this.pin = pin;
		}
		public LocalDateTime getSentTime() {
			return sentTime;
		}
		public void setSentTime(LocalDateTime sentTime) {
			this.sentTime = sentTime;
		}
		public LocalDateTime getExpirationTime() {
			return expirationTime;
		}
		public void setExpirationTime(LocalDateTime expirationTime) {
			this.expirationTime = expirationTime;
		}
		public ResetPin(String pin, LocalDateTime sentTime, LocalDateTime expirationTime, Boolean isUsed, int userId,
				int sendingmediumId) {
			super();
			this.pin = pin;
			this.sentTime = sentTime;
			this.expirationTime = expirationTime;
			this.isUsed = isUsed;
			
		}

		public ResetPin(String pin) {
			this.pin=pin;
		}
		
		
		
		
		
		
}
