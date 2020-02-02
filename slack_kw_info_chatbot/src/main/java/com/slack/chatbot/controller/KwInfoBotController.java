package com.slack.chatbot.controller;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.slack.chatbot.dto.Event;
import com.slack.chatbot.dto.RequestBodyDTO;
import com.slack.chatbot.dto.SendRequsetDTO;

@RestController
public class KwInfoBotController {
	
	/* Challenge 파라미터 갱신용
	 * 
	 * 
	@RequestMapping(value = "/echobot", method=RequestMethod.POST)
	public String ChallengeParameterCheck(@RequestBody HashMap<String, Object> requestURL) {
		System.out.println(requestURL);
		String challenge_parameter = requestURL.get("challenge").toString();
		System.out.println(challenge_parameter);
		return challenge_parameter;
	}
	*/
	
	
	@RequestMapping(value = "/echobot", method = RequestMethod.POST)
	public String ReceiveMessage(@RequestBody RequestBodyDTO request) throws URISyntaxException {
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
					
					System.out.println(result);
					
				}
			}
		}
		return "";
	}
	
}
