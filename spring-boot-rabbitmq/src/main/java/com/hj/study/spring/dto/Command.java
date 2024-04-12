package com.hj.study.spring.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Command {
	
	String body;
	String command;
	String[] arguments;

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}


	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getArguments() {
		return this.arguments;
	}

	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}
}
