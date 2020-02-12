package com.slack.chatbot.message;

import lombok.Getter;

@Getter
public enum ChatBotMessage {
	BUS_API_MESSAGE("번 버스 도착 정보 입니다\n"),
	
	BUS_API_FIRST_BUS("첫번째 버스 : "),
	
	BUS_API_SECOND_BUS("두번째 버스 : "),
	
	KWU_NOTICE_MESSAGE("공지사항 : https://www.kw.ac.kr/ko/life/notice.jsp\n"),
	
	KWU_STUDYROOM_INFO_MESSAGE("도서관 열람실 정보(전체/사용/잔여)\n");
	
	
	private String message;
	
	private ChatBotMessage(String message) {
		this.message=message;
	}

}
