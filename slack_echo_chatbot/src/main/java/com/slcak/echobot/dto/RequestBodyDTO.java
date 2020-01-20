package com.slcak.echobot.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestBodyDTO {

	private String[] authed_users;
	private String event_id;
	private String api_id;
	private String team_id;
	private String type;
	private String token;
	private Event event;
}
