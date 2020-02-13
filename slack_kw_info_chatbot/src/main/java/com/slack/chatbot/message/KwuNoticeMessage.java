package com.slack.chatbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KwuNoticeMessage {

	KWU_NOTICE_MESSAGE("공지사항 : https://www.kw.ac.kr/ko/life/notice.jsp\n");
	
	private String message; 
}
