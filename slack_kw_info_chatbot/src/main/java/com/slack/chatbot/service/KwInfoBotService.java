package com.slack.chatbot.service;


import com.slack.chatbot.dto.RequestBodyDTO;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public interface KwInfoBotService {

  public boolean echoMyMessage(RequestBodyDTO request) throws URISyntaxException;

  public boolean sendBusArriveTime(RequestBodyDTO request)
      throws URISyntaxException, SAXException, IOException, ParserConfigurationException;

  public boolean sendKwuNotice(RequestBodyDTO request) throws IOException, URISyntaxException;

  public boolean sendKwuStudyRoomSeat(RequestBodyDTO request)
      throws IOException, URISyntaxException;

}
