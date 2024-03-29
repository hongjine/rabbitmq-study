package com.hj.study.spring.boot.controller;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hj.study.spring.dto.ResourcePathRequest;
import com.hj.study.spring.dto.TopicPathRequest;
import com.hj.study.spring.dto.UserPathRequest;
import com.hj.study.spring.dto.VhostPathRequest;

@RestController
@RequestMapping("/rabbit/auth")
public class RabbitMQAuthController {

	@Autowired
	ObjectMapper objectMapper;
	
	List<String> allowUserIdList = Arrays.asList("user", "user1", "user2");

	@GetMapping
	public String index() {
		return "ok";
	}
	
	@GetMapping(path = "/ping")
	public String ping() {
			return "ping";
	}

	@PostMapping(path = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String postUser(UserPathRequest request) throws JsonProcessingException {

		System.out.println("postUser " + objectMapper.writeValueAsString(request));

		if (allowUserIdList.stream().anyMatch(request.getUsername()::equals)
				&& "pass".equals(request.getPassword())) {
			return "allow administrator";
		} else {
			return "deny";
		}
	}

	@PostMapping("/vhost")
	public String postVhost(VhostPathRequest request) throws JsonProcessingException {

		System.out.println("postVhost " + objectMapper.writeValueAsString(request));

		//3rd hw : only allow vhost 'chat'
		if (allowUserIdList.stream().anyMatch(request.getUsername()::equals)
				&& "chat".equals(request.getVhost())) {
			return "allow";
		} else {
			return "deny";
		}
	}

	@PostMapping("/resource")
	public String postResource(ResourcePathRequest request) throws JsonProcessingException {

		System.out.println("postResource " + objectMapper.writeValueAsString(request));

		//3rd hw : only allow vhost 'chat'
		if (allowUserIdList.stream().anyMatch(request.getUsername()::equals)
				&& "chat".equals(request.getVhost())) {
				
			// exchange perm setting
			if ("exchange".equals(request.getResource())) {
				
				// 3rd hw : allow publish in 'request' exchange 
				// 			require perm in allow publish : exchange write
				if ("request".equals(request.getName())
						&& Arrays.asList("write").stream().anyMatch(request.getPermission()::equals)
					) {
						
					return "allow";
				
				// 3rd hw : allow binding - 'user.{userId}' queue to 'user' exchange
				// 			source('user' exchange) required perm is read.
				} else if ("user".equals(request.getName())
						&& Arrays.asList("configure", "read").stream().anyMatch(request.getPermission()::equals)
					) {
					return "allow";
				}
				
			// 3rd hw : allow declare queue - queue name : user.{userId}
			//			declare queue required perm : configure 
			// 3rd hw : allow binding - 'user.{userId}' queue to 'user' exchange. destination(queue)
			//			required perm : write
			// 3rd hw : allow read - 'user.{userId}' queue
			} else if ("queue".equals(request.getResource())
					&& ("user." + request.getUsername()).equals(request.getName())
					&& Arrays.asList("configure", "write", "read").stream().anyMatch(request.getPermission()::equals)) {
				return "allow";
			}
		}

		System.out.println("deny");
		return "deny";
		
	}

	// allow binding / publish message
	@PostMapping("/topic")
	public String postTopic(TopicPathRequest request) throws JsonProcessingException {

		System.out.println("postTopic " + objectMapper.writeValueAsString(request));

		Pattern pattern = Pattern.compile("^(chat|command)\\.\\w+");
		
		// 3rd hw : allow vhost 'chat'
		if (!"chat".equals(request.getVhost())) {
			return "deny";
		}
		
		// 3rd hw : allow publish message topic('request')
		if ("topic".equals(request.getResource())
				&&"request".equals(request.getName())
				&& "write".equals(request.getPermission())
				&& ("chat.user." + request.getUsername()).equals(request.getRouting_key())
				) {
			return "allow";
		}
		
		// 3rd hw :  allow binding : user.{userId} queue to 'user' exchange with chat.user.{userid} routing key
		if (allowUserIdList.stream().anyMatch(request.getUsername()::equals)
				&& "topic".equals(request.getResource())
				&& "user".equals(request.getName())
				&& "read".equals(request.getPermission())
				&& ( ("chat.user." + request.getUsername()).equals(request.getRouting_key()))
				) {
			return "allow";
		} 
		
		System.out.println("deny");
		return "deny";
	}
}
