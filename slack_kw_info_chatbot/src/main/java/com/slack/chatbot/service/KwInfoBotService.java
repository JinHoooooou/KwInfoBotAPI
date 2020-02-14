package com.slack.chatbot.service;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.slack.chatbot.dto.RequestBodyDTO;

public interface KwInfoBotService {
	
	public boolean EchoMyMessage(RequestBodyDTO request) throws URISyntaxException;

	public boolean SendBusInfo(RequestBodyDTO request) throws URISyntaxException, SAXException, IOException, ParserConfigurationException;

	public boolean SendKwuNotice(RequestBodyDTO request) throws IOException, URISyntaxException;

	public boolean SendKwuStudyRoomSeat(RequestBodyDTO request) throws IOException, URISyntaxException;

}
