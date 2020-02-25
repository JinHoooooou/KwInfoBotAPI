package com.slack.chatbot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event {
  private String client_msg_id;
  private String type;
  private String text;
  private String user;
  private String ts;
  private Object blocks;
  private String channel;
  private String event_ts;
  private String channel_type;
}
