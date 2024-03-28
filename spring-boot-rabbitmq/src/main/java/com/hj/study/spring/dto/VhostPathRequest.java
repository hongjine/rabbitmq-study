package com.hj.study.spring.dto;

public class VhostPathRequest {
	
	String username;
	String vhost;
	String ip;

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

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
