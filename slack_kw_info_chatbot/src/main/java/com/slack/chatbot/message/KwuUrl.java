package com.slack.chatbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KwuUrl {
	KWU_NOTICE_URL("https://www.kw.ac.kr/ko/life/notice.jsp"),
	KWU_STUDY_ROOM_URL("http://mobileid.kw.ac.kr/seatweb/domian5.asp");
	
	private String url;
}
