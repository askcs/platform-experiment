package com.askcs.platform.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Client implements Serializable {

	private static final long serialVersionUID = 562421936543787120L;
	protected String password;
	protected String uuid="";
	protected String firstName="";
	protected String lastName="";
	protected Address address;
	protected String phone="";
	protected String clientGroupUuid="";
	protected Set<Contact> contacts;
	protected long birthDate;
	protected String clientProfileUrl;
	protected Map<String, Object> extraInfo;

	public Client(){
	    this.extraInfo = new HashMap<String, Object>();
	}
	
	public Client(String uuid, String firstName,String lastName) {
		this.uuid = uuid;
		this.firstName=firstName;
		this.lastName=lastName;
		
		this.extraInfo = new HashMap<String, Object>();
	}
	
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}	
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getClientGroupUuid() {
		return clientGroupUuid;
	}

	public void setClientGroupUuid(String clientGroupUuid) {
		this.clientGroupUuid = clientGroupUuid;
	}

	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

	public long getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(long birthDate) {
		this.birthDate = birthDate;
	}

	public String getClientProfileUrl() {
		return clientProfileUrl;
	}

	public void setClientProfileUrl(String clientProfileUrl) {
		this.clientProfileUrl = clientProfileUrl;
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
            if (this.getUuid().equals(((Client) obj).getUuid())) {
                return true;
            }
            else {
                return false;
            }
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + uuid.hashCode();
		return hash;
	}

    public String getPassword() {
    
        return password;
    }
    public void setPassword(String password) {
    
        this.password = password;
    }
}
