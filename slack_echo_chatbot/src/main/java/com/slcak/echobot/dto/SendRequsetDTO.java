package com.slcak.echobot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SendRequsetDTO {
	private String text;
	private String channel;
	private String token;
}
