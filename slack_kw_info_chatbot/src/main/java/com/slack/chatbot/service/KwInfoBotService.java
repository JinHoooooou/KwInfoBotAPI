package com.slack.chatbot.service;

import java.net.URISyntaxException;

import com.slack.chatbot.dto.RequestBodyDTO;

public interface KwInfoBotService {
	
	public void echoMyMessage(RequestBodyDTO request) throws URISyntaxException;

}
