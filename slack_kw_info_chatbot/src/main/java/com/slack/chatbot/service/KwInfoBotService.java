package com.slack.chatbot.service;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.slack.chatbot.dto.RequestBodyDTO;

public interface KwInfoBotService {
	
	public void echoMyMessage(RequestBodyDTO request) throws URISyntaxException;

	public void sendBusInfo(RequestBodyDTO request) throws URISyntaxException, SAXException, IOException, ParserConfigurationException;

	public void sendNoticeKwInfo(RequestBodyDTO request) throws Exception;

	public void sendStudyRoomSeatInfo(RequestBodyDTO request) throws IOException, URISyntaxException;

}
