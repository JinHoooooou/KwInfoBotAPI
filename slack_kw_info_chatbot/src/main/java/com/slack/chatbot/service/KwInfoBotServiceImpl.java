package com.slack.chatbot.service;

import com.slack.chatbot.dto.RequestBodyDto;
import com.slack.chatbot.message.BusOpenApiMessage;
import com.slack.chatbot.message.BusOpenApiUrl;
import com.slack.chatbot.message.KwuNoticeMessage;
import com.slack.chatbot.message.KwuStudyRoomMessage;
import com.slack.chatbot.message.KwuUrl;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;


@Service("kwInfoBotService")
public class KwInfoBotServiceImpl implements KwInfoBotService {

  private static final String SLACK_BOT_NAME = "<@USJBNQ36D>";
  private static final int BUS_COUNT = 3;
  private static final int KWU = 0;
  private static final int HYUNDAI_APT = 1;
  private static final int ANAM_STREET = 2;
  private static final int KWU_NOTICE_COUNT = 25;


  @Override
  public boolean echoMyMessage(RequestBodyDto request) throws URISyntaxException {
    String messageFromUser = request.getEvent().getText();
    if (containCorrectBotName(messageFromUser)) {
      String echoMessage = extractMyMessage(messageFromUser);
      sendBotMessageToChannel(echoMessage, request.getEvent().getChannel());
      return true;
    }
    return false;
  }

  @Override
  public boolean sendKwuNotice(RequestBodyDto request) throws IOException, URISyntaxException {
    if (containCorrectBotName(request.getEvent().getText())) {
      Elements kwuNoticeList = getXmlTagList(KwuUrl.KWU_NOTICE_URL.getUrl());
      String printMessage = makeKwuNoticeMessageFormat(kwuNoticeList);
      sendBotMessageToChannel(printMessage, request.getEvent().getChannel());
      return true;
    }
    return false;
  }

  @Override
  public boolean sendBusArriveTime(RequestBodyDto request)
      throws URISyntaxException, SAXException, IOException, ParserConfigurationException {
    StringBuilder printMessage = new StringBuilder();
    if (containCorrectBotName(request.getEvent().getText())) {
      for (int i = 0; i < BUS_COUNT; i++) {
        String busOpenApiUrl = makeUrl(i);
        Elements busListOfStation = getXmlTagList(busOpenApiUrl);
        printMessage.append(makeBusArriveMessageFormat(busListOfStation));
      }
      sendBotMessageToChannel(printMessage.toString(), request.getEvent().getChannel());
      return true;
    }
    return false;
  }

  @Override
  public boolean sendKwuStudyRoomSeat(RequestBodyDto request)
      throws IOException, URISyntaxException {

    if (containCorrectBotName(request.getEvent().getText())) {
      Elements studyRoomList = getXmlTagList(KwuUrl.KWU_STUDY_ROOM_URL.getUrl());
      String printMessage = makeKwuStudyRoomMessageFormat(studyRoomList);
      sendBotMessageToChannel(printMessage, request.getEvent().getChannel());
      return true;
    }
    return false;
  }


  private String extractMyMessage(String messageFromUser) {
    return messageFromUser
        .substring(messageFromUser.indexOf(
            KwInfoBotServiceImpl.SLACK_BOT_NAME) + KwInfoBotServiceImpl.SLACK_BOT_NAME.length()
            + 1);
  }

  private boolean containCorrectBotName(String messageFromUser) {
    String[] eachWord = messageFromUser.split(" ");
    for (String s : eachWord) {
      if (s.equals(KwInfoBotServiceImpl.SLACK_BOT_NAME) && !eachWord[eachWord.length - 1].equals(
          KwInfoBotServiceImpl.SLACK_BOT_NAME)) {
        return true;
      }
    }
    return false;
  }

  private String makeBusArriveMessageFormat(Elements busListOfStation) {
    StringBuilder printMessage = new StringBuilder();
    for (Element eachBus : busListOfStation) {
      if (isCorrectBusNumber(eachBus)) {
        String busNumber = eachBus.select("rtNm").text();
        String station = eachBus.select("stNm").text();
        String firstArriveMessage = eachBus.select("arrmsg1").text();
        String secondArriveMessage = eachBus.select("arrmsg2").text();
        printMessage.append(station).append(" ").append(busNumber)
            .append(BusOpenApiMessage.BUS_API_MESSAGE.getMessage())
            .append(BusOpenApiMessage.BUS_API_FIRST_BUS.getMessage()).append(firstArriveMessage)
            .append("\n").append(BusOpenApiMessage.BUS_API_SECOND_BUS.getMessage())
            .append(secondArriveMessage).append("\n\n");
      }
    }
    return printMessage.toString();
  }

  private Elements getXmlTagList(String url) throws IOException {
    Document xmlDocument = Jsoup.connect(url).ignoreContentType(true).get();
    String tags = "";
    if (url.contains(BusOpenApiUrl.BUS_OPEN_API_BASE_URL.getUrl())) {
      tags = "itemList";
    } else if (url.contains(KwuUrl.KWU_NOTICE_URL.getUrl())) {
      tags = "li.top-notice";
    } else {
      tags = "tr";
    }
    return xmlDocument.select(tags);
  }

  private String makeUrl(int station) {
    String url = BusOpenApiUrl.BUS_OPEN_API_BASE_URL.getUrl()
        + BusOpenApiUrl.BUS_OPEN_API_SERVICE_KEY.getUrl();
    switch (station) {
      case KWU:
        url += BusOpenApiUrl.BUS_OPEN_API_KWU_ARSID.getUrl();
        break;
      case HYUNDAI_APT:
        url += BusOpenApiUrl.BUS_OPEN_API_HYUNDAI_APT_ARSID.getUrl();
        break;
      case ANAM_STREET:
        url += BusOpenApiUrl.BUS_OPEN_API_ANAM_STREET_ARSID.getUrl();
        break;
      default:
        url = "";
    }
    return url;
  }

  private boolean isCorrectBusNumber(Element eachBus) {
    return eachBus.select("rtNm").text().matches("173|1017");
  }

  private String makeKwuNoticeMessageFormat(Elements kwuNoticeList) {
    String printMessage = KwuNoticeMessage.KWU_NOTICE_MESSAGE.getMessage();
    for (int i = 0; i < KWU_NOTICE_COUNT; i++) {
      Element eachNotice = kwuNoticeList.get(i);
      String title = eachNotice.select("a").text();
      String createDate = eachNotice.select("p.info").text();
      title = title.replaceAll("Attachment", "").replaceAll("신규게시글", "");
      printMessage += ((i + 1) + ". " + title + "\n" + createDate + "\n\n");
    }
    return printMessage;
  }

  private String makeKwuStudyRoomMessageFormat(Elements studyRoomList) {
    String printMessage = KwuStudyRoomMessage.KWU_STUDY_ROOM_SEAT_INFO_MESSAGE.getMessage();
    for (int i = 0; i < studyRoomList.size(); i++) {
      Elements eachRoom = studyRoomList.get(i).select("td");
      String roomNumber = eachRoom.get(0).text();
      if (roomNumber.matches("[1-3]")) {
        printMessage +=
            roomNumber + "열람실 " + eachRoom.get(2).text() + "/" + eachRoom.get(3).text() + "/"
                + eachRoom.get(4).text() + "\n";
      }
    }
    return printMessage;
  }

  private void sendBotMessageToChannel(String message, String channel) throws URISyntaxException {
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
    parameters.add("text", message);
    parameters.add("channel", channel);

    RestTemplate restTemplate = new RestTemplate();
    final String baseUrl = "https://slack.com/api/chat.postMessage";
    URI uri = new URI(baseUrl);

    ResponseEntity<String> response = restTemplate.postForEntity(uri, parameters, String.class);
    restTemplate.delete(baseUrl);
  }
}
