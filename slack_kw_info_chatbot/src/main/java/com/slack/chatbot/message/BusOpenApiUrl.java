package com.slack.chatbot.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusOpenApiUrl {
	
	BUS_OPEN_API_BASE_URL("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute"),
	
	BUS_OPEN_API_SERVICE_KEY("?serviceKey=7jHs%2Bx3TFCBGi9X0WFnQeUXUpCC7KJLJ1aA%2BmM3j8cJau%2B67c22LhWy%2FmkWvLG7m%2BieC4iQay%2FgroMRytDrmzQ%3D%3D"),
	
	BUS_OPEN_API_KWU_STID("&stId=110000234"),
	
	BUS_OPEN_API_KWU_ORD("&busRouteId=100100130"),
	
	BUS_OPEN_API_1017_ROUTE_ID("&ord=5");
	
	private String url;
}
