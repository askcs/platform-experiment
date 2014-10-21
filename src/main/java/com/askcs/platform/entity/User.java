package com.askcs.platform.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User implements Serializable {

	private static final long serialVersionUID = 1398346704013832436L;
	String uuid;
	String userName;
	String passwordHash;
	String firstName;
	String lastName;
	String phone;
	Set<String> teamUuids;
	String role;
	String function;
	long birthDate;
    Address address;
    String APNSKey;
    String GCMKey;
    protected Map<String, Object> extraInfo;
	
	public User(){}
	
	public User(String uuid){
		this.uuid = uuid; 
		extraInfo = new HashMap<String, Object>();
	}
	
	public User(String username, String password) {
		this.uuid = username;
		this.userName = username;
		this.passwordHash = password;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Set<String> getTeamUuids() {
		return teamUuids;
	}

	public void setTeamUuids(Set<String> teamUuids) {
		this.teamUuids = teamUuids;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public long getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(long birthDate) {
		this.birthDate = birthDate;
	}

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    
    public String getAPNSKey() {
        return APNSKey;
    }
    
    
    public void setAPNSKey(String aPNSKey) {
        APNSKey = aPNSKey;
    }
    
    
    public String getGCMKey() {
        return GCMKey;
    }
    
    public void setGCMKey(String gCMKey) {
        GCMKey = gCMKey;
    }
    
    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }
    
    
    public void setExtraInfo(Map<String, Object> extraInfo) {
        this.extraInfo = extraInfo;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if(this.getUuid().toLowerCase().compareTo(((User)obj).getUuid().toLowerCase()) == 0){
        	return true;
        }else{
        	return false;
        }
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + uuid.hashCode();
		return hash;
	}
}
