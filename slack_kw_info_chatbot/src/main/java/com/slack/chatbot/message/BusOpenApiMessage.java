package com.slack.chatbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusOpenApiMessage {
	
	BUS_API_MESSAGE("번 버스 도착 정보 입니다\n"),
	
	BUS_API_FIRST_BUS("첫번째 버스 : "),
	
	BUS_API_SECOND_BUS("두번째 버스 : ");

	private String Message;
}
