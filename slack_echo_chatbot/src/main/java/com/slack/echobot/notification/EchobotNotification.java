package com.slack.echobot.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EchobotNotification {
	
	@Autowired
	private RestTemplate restTemplate;


}
