package com.bazra.usermanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//import com.bazra.usermanagement.model.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@SequenceGenerator(name = "seq", initialValue = 1000, allocationSize = 100)
@Entity
@Table(name = "accountinfo")
@EntityListeners(AuditingEntityListener.class)
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@ApiModelProperty(value = "This Variable Holds Account Number Or Phone Number Of The User ")
	private String accountNumber;

	

	@ApiModelProperty(value = "This Variable Holds User's Daily Transaction")
	private BigDecimal daily = BigDecimal.ZERO;

	@ApiModelProperty(value = "This Variable Holds User's Daily Transaction")
	private BigDecimal commission = new BigDecimal("0");

	@ManyToOne
	@JoinColumn(name = "user")
	private UserInfo user;
	private LocalDate creationDate;
	@ManyToOne
	@JoinColumn(name = "admin")
	private AdminInfo admin;

	@ManyToOne
	@JoinColumn(name = "agent")
	private AgentInfo agent;

	@ManyToOne
	@JoinColumn(name = "merchant")
	private MerchantInfo merchant;
	@ManyToOne
	@JoinColumn(name = "type")
	private AccountType type;
	private boolean status;

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

//	public Account() {
//		super();
//		// TODO Auto-generated constructor stub
//	}

	public AgentInfo getAgent() {
		return agent;
	}

	public void setAgent(AgentInfo agent) {
		this.agent = agent;
	}

	public MerchantInfo getMerchant() {
		return merchant;
	}

	public void setMerchant(MerchantInfo merchant) {
		this.merchant = merchant;
	}

	public AdminInfo getAdmin() {
		return admin;
	}

	public void setAdmin(AdminInfo admin) {
		this.admin = admin;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Account(String accountNumber, String username) {
		this.accountNumber = accountNumber;
		

	}

	public BigDecimal getDaily() {
		return daily;
	}

	public void setDaily(BigDecimal daily) {
		this.daily = daily;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String id) {
		this.accountNumber = id;
	}


}

//  public Date getLastAccessed() {
//        return lastAccessed;
//    }
//    public void setLastAccessed(Date lastAccessed) {
//        this.lastAccessed = lastAccessed;
//    }
//    public Date getCreatedat() {
//      return createdat;
//  }
//  public void setCreatedat(Date createdat) {
//      this.createdat = createdat;
//  }
//  public Date getUpdatedat() {
//      return updatedat;
//  }
//  public void setUpdatedat(Date updatedat) {
//      this.updatedat = updatedat;
//  }
//  
//  public AccountTypes getAccounttype() {
//      return accounttype;
//  }
//  public AccountTypes setAccounttype(AccountTypes accounttype) {
//      return  accounttype;
//  }
//  
//    public Status getStatus() {
//        return status;
//    }
//    public void setStatus(Status status) {
//        this.status = status;
//    }
//    public AccountDetail getAccountDetail() {
//        return accountDetail;
//    }
//    public void setAccountDetail(AccountDetail accountDetail) {
//        this.accountDetail = accountDetail;
//    }
//  
