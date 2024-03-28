package com.hj.study.spring.dto;

public class TopicPathRequest {
	
	String username;
	String vhost;
	String resource;
	String name;
	String permission;
	String routing_key;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVhost() {
		return this.vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}

	public String getResource() {
		return this.resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getRouting_key() {
		return this.routing_key;
	}

	public void setRouting_key(String routing_key) {
		this.routing_key = routing_key;
	}

}
