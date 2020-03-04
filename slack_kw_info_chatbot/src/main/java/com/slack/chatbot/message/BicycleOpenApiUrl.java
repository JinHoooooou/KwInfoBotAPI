package com.slack.chatbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BicycleOpenApiUrl {
  BICYCLE_OPEN_API_URL("http://openapi.seoul.go.kr:8088/68554d704a6a696e39336c76785464/json/bikeList/835/1000");

  private String url;
}
