package com.slack.chatbot.service;

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

import com.slack.chatbot.dto.RequestBodyDTO;
import com.slack.chatbot.message.BusOpenApiMessage;
import com.slack.chatbot.message.BusOpenApiUrl;
import com.slack.chatbot.message.KwuNoticeMessage;
import com.slack.chatbot.message.KwuStudyRoomMessage;
import com.slack.chatbot.message.KwuUrl;

@Service("kwInfoBotService")
public class KwInfoBotServiceImpl implements KwInfoBotService {
	private final static String SLACK_BOT_NAME = "<@USJBNQ36>";
	private final static int BUS_COUNT = 3;
	private final static int KWU = 0;
	private final static int HYUNDAI_APT = 1;
	private final static int ANAM_STREET = 2;
	private final static int KWU_NOTICE_COUNT = 25;

	@Override
	public boolean echoMyMessage(RequestBodyDTO request) throws URISyntaxException {
		String messageFromUser = request.getEvent().getText();
		if(containCorrectBotName(messageFromUser, SLACK_BOT_NAME)){
			String echoMessage = extractMyMessage(messageFromUser, SLACK_BOT_NAME);
			sendBotMessageToChannel(echoMessage,request.getEvent().getChannel());
			return true;
		}
		return false;
	}

	@Override
	public boolean sendBusArriveTime(RequestBodyDTO request) throws URISyntaxException, SAXException, IOException, ParserConfigurationException {
		if(containCorrectBotName(request.getEvent().getText(), SLACK_BOT_NAME)){
			for(int i = 0; i<BUS_COUNT; i++){
				String busOpenApiUrl = makeURL(i);
				Elements busListOfStation = getXmlTagList(busOpenApiUrl);
				String printMessage = makeBusArriveMessageFormat(busListOfStation);
				sendBotMessageToChannel(printMessage, request.getEvent().getChannel());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean sendKwuNotice(RequestBodyDTO request) throws IOException, URISyntaxException {
		if(containCorrectBotName(request.getEvent().getText(), SLACK_BOT_NAME)){
			Elements kwuNoticeList = getXmlTagList(KwuUrl.KWU_NOTICE_URL.getUrl());
			String printMessage = makeKwuNoticeMessageFormat(kwuNoticeList);
			sendBotMessageToChannel(printMessage,request.getEvent().getChannel());
			return true;
		}
		return false;
	}

	@Override
	public boolean sendKwuStudyRoomSeat(RequestBodyDTO request) throws IOException, URISyntaxException {
		if(containCorrectBotName(request.getEvent().getText(), SLACK_BOT_NAME)) {
			Elements studyRoomList = getXmlTagList(KwuUrl.KWU_STUDY_ROOM_URL.getUrl());
			String printMessage = makeKwuStudyRoomMessageFormat(studyRoomList);
			sendBotMessageToChannel(printMessage.toString(), request.getEvent().getChannel());
			return true;
		}
		return false;
	}

	private String extractMyMessage(String messageFromUser, String slackBotName) {
		return messageFromUser.substring(messageFromUser.indexOf(slackBotName) + slackBotName.length() + 1);
	}

	private boolean containCorrectBotName(String messageFromUser, String slackBotName) {
		for(String eachWord : messageFromUser.split(" ")){
			if(eachWord.equals(slackBotName) && messageFromUser.split(" ").length!=1) {
				return true;
			}
		}
		return false;
	}

	private String makeBusArriveMessageFormat(Elements busListOfStation) {
		String printMessage = "";
		for(Element eachBus : busListOfStation){
			if(isCorrectBusNumber(eachBus)){
				String busNumber = eachBus.select("rtNm").text();
				String station = eachBus.select("stNm").text();
				String firstArriveMessage = eachBus.select("arrmsg1").text();
				String secondArriveMessage = eachBus.select("arrmsg2").text();
				printMessage += station + " " + busNumber + BusOpenApiMessage.BUS_API_MESSAGE.getMessage()
						+ BusOpenApiMessage.BUS_API_FIRST_BUS.getMessage() + firstArriveMessage + "\n"
						+ BusOpenApiMessage.BUS_API_SECOND_BUS.getMessage() + secondArriveMessage + "\n\n";
			}
		}
		return printMessage;
	}

	private Elements getXmlTagList(String url) throws IOException {
		Document xmlDocument = Jsoup.connect(url).ignoreContentType(true).get();
		String tags="";
		if(url.contains(BusOpenApiUrl.BUS_OPEN_API_BASE_URL.getUrl())) tags = "itemList";
		else if(url.equals(KwuUrl.KWU_NOTICE_URL)) tags = "li.top-notice";
		else tags = "tr";
		return xmlDocument.select(tags);
	}

	private String makeURL(int station) {
		String url = BusOpenApiUrl.BUS_OPEN_API_BASE_URL.getUrl() +
				BusOpenApiUrl.BUS_OPEN_API_SERVICE_KEY.getUrl();
		switch (station){
			case KWU :
				url += BusOpenApiUrl.BUS_OPEN_API_KWU_ARSID.getUrl();
				break;
			case HYUNDAI_APT :
				url += BusOpenApiUrl.BUS_OPEN_API_HYUNDAI_APT_ARSID.getUrl();
				break;
			case ANAM_STREET :
				url += BusOpenApiUrl.BUS_OPEN_API_ANAM_STREET_ARSID.getUrl();
		}
		return url;
	}

	private boolean isCorrectBusNumber(Element eachBus) {
		return eachBus.select("rtNm").text().matches("173|1017");
	}

	private String makeKwuNoticeMessageFormat(Elements kwuNoticeList) {
		String printMessage = KwuNoticeMessage.KWU_NOTICE_MESSAGE.getMessage();
		for(int i = 0; i<KWU_NOTICE_COUNT; i++){
			Element eachNotice = kwuNoticeList.get(i);
			String title = eachNotice.select("a").text();
			String createDate = eachNotice.select("p.info").text();
			title = title.replaceAll("Attachment", "").replaceAll("신규게시글","");
			printMessage += ((i+1) + ". " + title + "\n" + createDate + "\n\n");
		}
		return printMessage;
	}

	private String makeKwuStudyRoomMessageFormat(Elements studyRoomList) {
		String printMessage = KwuStudyRoomMessage.KWU_STUDY_ROOM_SEAT_INFO_MESSAGE.getMessage();
		for (int i = 0; i<studyRoomList.size(); i++) {
			Elements eachRoom = studyRoomList.get(i).select("td");
			String roomNumber = eachRoom.get(0).text();
			if(roomNumber.matches("[1-3]"))
				printMessage += roomNumber + "열람실 " + eachRoom.get(2).text() + "/" + eachRoom.get(3).text() + "/" + eachRoom.get(4).text() +"\n";
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
