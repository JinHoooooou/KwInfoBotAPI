package com.slack.chatbot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.slack.chatbot.dto.RequestBodyDTO;

class KwInfoBotServiceImplTest {

	KwInfoBotServiceImpl testObject = new KwInfoBotServiceImpl();
	RequestBodyDTO testParameter = new RequestBodyDTO();
	@Test
	void SendBusInfoTest() throws Exception {
		assertTrue(testObject.SendBusInfo(testParameter));
	}
	
	@Test
	void SendKwuNoticeTest() throws Exception{
		assertTrue(testObject.SendKwuNotice(testParameter));
	}
	
	@Test
	void SendKwuStudyRoomSeatTest() throws Exception{
		assertTrue(testObject.SendKwuStudyRoomSeat(testParameter));
	}
}
