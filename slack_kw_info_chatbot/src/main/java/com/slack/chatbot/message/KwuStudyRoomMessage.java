package com.slack.chatbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KwuStudyRoomMessage {
	KWU_STUDY_ROOM_SEAT_INFO_MESSAGE("도서관 열람실 정보(전체/사용/잔여)\n");
	
	private String message;
}
