package com.slack.chatbot.controller;


import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.slack.chatbot.dto.RequestBodyDTO;
import com.slack.chatbot.dto.SendRequsetDTO;

@RestController
public class KwInfoBotController {
	
	@RequestMapping(value = "/kwinfobot", method = RequestMethod.POST)
	public String ReceiveMessage(@RequestBody RequestBodyDTO request) throws URISyntaxException {
		
		if(request.getChallenge()!=null)
			return request.getChallenge();
		else {
			Event event = request.getEvent();				//RequestBody의 Event
			if(event.getType().equals("app_mention")) {
				String my_text = event.getText();			//Slack channel에 보낸 메세지
				String split_text[] = my_text.split(" ");	
				for(String slack_bot_name : split_text) {	//@slackbottest2 추출
					if(slack_bot_name.equals("<@USJBNQ36D>")) {
						int start_index = my_text.indexOf(slack_bot_name);
						String echo_bot_text = my_text.substring(start_index+(slack_bot_name.length())+1);	// @slackbottest2 이후의 text
						
						MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
	                    parameters.add("text", echo_bot_text);
	                    parameters.add("channel", event.getChannel());
	                    parameters.add("token", "install_app_token 입력");
						
	                    System.out.println(parameters);
	                    
						RestTemplate restTemplate = new RestTemplate();
						final String baseUrl = "https://slack.com/api/chat.postMessage";
						URI uri = new URI(baseUrl);
						
						ResponseEntity<String> result = restTemplate.postForEntity(uri, parameters, String.class);				
					}
				}
			}
		}		
		
		return "";
	}
	
}
