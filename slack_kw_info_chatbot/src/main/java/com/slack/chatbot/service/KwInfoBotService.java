package com.slack.chatbot.service;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.slack.chatbot.dto.RequestBodyDTO;

public interface KwInfoBotService {
	
	public boolean echoMyMessage(RequestBodyDTO request) throws URISyntaxException;

	public boolean sendBusInfo(RequestBodyDTO request) throws URISyntaxException, SAXException, IOException, ParserConfigurationException;

	public boolean sendKwuNotice(RequestBodyDTO request) throws IOException, URISyntaxException;

	public boolean sendKwuStudyRoomSeat(RequestBodyDTO request) throws IOException, URISyntaxException;

}
