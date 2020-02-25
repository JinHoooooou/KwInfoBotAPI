package com.slack.chatbot.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class EchobotNotification {

  @Autowired
  private RestTemplate restTemplate;


}
