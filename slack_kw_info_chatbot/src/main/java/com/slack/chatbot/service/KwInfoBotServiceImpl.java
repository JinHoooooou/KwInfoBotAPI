package com.slack.chatbot.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.slack.chatbot.dto.Event;
import com.slack.chatbot.dto.RequestBodyDTO;

@Service("kwInfoBotService")
public class KwInfoBotServiceImpl implements KwInfoBotService {

	@Override
	public void echoMyMessage(RequestBodyDTO request) throws URISyntaxException {
		Event event = request.getEvent();
		String userMessage = event.getText();
		String splitMessage[] = userMessage.split(" ");
		for (String findSlackBotName : splitMessage) {
			if (findSlackBotName.equals("<@USJBNQ36D>")) {
				int indexOfBotName = userMessage.indexOf(findSlackBotName);
				if (indexOfBotName + findSlackBotName.length() < userMessage.length()) {
					String echoMessage = userMessage.substring(indexOfBotName + findSlackBotName.length() + 1);

					MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
					parameters.add("text", echoMessage);
					parameters.add("channel", event.getChannel());
					parameters.add("token", "xoxb-892170117313-902396819217-clcBR4o6Iq7biOAEdPEIFoCZ");

					System.out.println(parameters);

					RestTemplate restTemplate = new RestTemplate();
					final String baseUrl = "https://slack.com/api/chat.postMessage";
					URI uri = new URI(baseUrl);

					ResponseEntity<String> result = restTemplate.postForEntity(uri, parameters, String.class);
				}
			}
		}
	}
}
