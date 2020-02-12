package com.slack.chatbot.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.slack.chatbot.dto.RequestBodyDTO;

class KwInfoBotServiceImplTest {

	@Test
	void test() throws Exception {
		KwInfoBotServiceImpl test = new KwInfoBotServiceImpl();
		RequestBodyDTO request = new RequestBodyDTO();
		//assertEquals(true, test.sendStudyRoomSeatInfo(request));
		assertEquals(true, test.sendNoticeKwInfo(request));
	}
}
