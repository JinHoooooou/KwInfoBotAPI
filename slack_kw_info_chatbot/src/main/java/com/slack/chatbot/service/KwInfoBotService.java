package com.slack.chatbot.service;


import com.slack.chatbot.dto.RequestBodyDto;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public interface KwInfoBotService {

  public boolean echoMyMessage(RequestBodyDto request) throws URISyntaxException;

  public boolean sendBusArriveTime(RequestBodyDto request)
      throws URISyntaxException, SAXException, IOException, ParserConfigurationException;

  public boolean sendKwuNotice(RequestBodyDto request) throws IOException, URISyntaxException;

  public boolean sendKwuStudyRoomSeat(RequestBodyDto request)
      throws IOException, URISyntaxException;

}
