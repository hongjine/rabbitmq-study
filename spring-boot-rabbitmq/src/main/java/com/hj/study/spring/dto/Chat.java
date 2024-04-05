package com.hj.study.spring.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

//4th hw
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Chat {

	String body;
	String userName;

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
