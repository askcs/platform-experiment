package com.askcs.platform.entity;

public class Contact {

	protected String title;
	protected String firstName;
	protected String lastName;
	protected String phone;
	protected Address address;
	protected String function;
	public Contact(){}
	
	public Contact(String title, String firstName,String lastName) {
		this.title=title;
		this.firstName=firstName;
		this.lastName=lastName;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
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

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
}
