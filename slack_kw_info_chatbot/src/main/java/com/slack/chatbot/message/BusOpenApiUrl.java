package com.slack.chatbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusOpenApiUrl {
  BUS_OPEN_API_BASE_URL("http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid"),
  BUS_OPEN_API_KWU_ARSID("&arsId=11335"),
  BUS_OPEN_API_HYUNDAI_APT_ARSID("&arsId=11281"),
  BUS_OPEN_API_ANAM_STREET_ARSID("&arsId=06177");

  private String url;
}
