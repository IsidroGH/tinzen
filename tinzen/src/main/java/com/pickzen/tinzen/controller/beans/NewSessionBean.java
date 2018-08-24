package com.pickzen.tinzen.controller.beans;

public class NewSessionBean {
	private String id;

	public NewSessionBean() {
		
	}
	
	public NewSessionBean(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id=id;
	}
}
