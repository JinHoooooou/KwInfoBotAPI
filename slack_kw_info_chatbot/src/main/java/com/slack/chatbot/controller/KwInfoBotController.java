package com.slack.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.slack.chatbot.dto.RequestBodyDTO;
import com.slack.chatbot.service.KwInfoBotService;

@RestController
public class KwInfoBotController {
	
	@Autowired
	private KwInfoBotService kwInfoBotService;
	
	@RequestMapping(value = "/kwinfobot", method = RequestMethod.POST)
	public String giveServiceToSlack(@RequestBody RequestBodyDTO request) throws Exception {
		
		if(request.getChallenge()!=null)	//first connection(event subscribe)
			return request.getChallenge();
		else if(request.getEvent().getType().equals("app_mention")) {
			if(request.getEvent().getText().contains("버스")) {
				kwInfoBotService.sendBusInfo(request);
			}
			else if(request.getEvent().getText().contains("공지")) {
				kwInfoBotService.sendKwuNotice(request);
			}
			else if(request.getEvent().getText().contains("열람실")) {
				kwInfoBotService.sendKwuStudyRoomSeat(request);
			}
			else kwInfoBotService.echoMyMessage(request);
		}		
		
		return "";
	}
	
}
