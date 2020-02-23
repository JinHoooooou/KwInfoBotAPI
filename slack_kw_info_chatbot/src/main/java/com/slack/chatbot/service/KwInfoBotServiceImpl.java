package com.slack.chatbot.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

	@Override
	public boolean echoMyMessage(RequestBodyDTO request) throws URISyntaxException {
		String messageFromUser = request.getEvent().getText();
		if(containCorrectBotName(messageFromUser, SLACK_BOT_NAME)){
			String echoMessage = extractMyMessage(messageFromUser, SLACK_BOT_NAME);
			sendBotMessageToChannel(echoMessage,request.getEvent().getChannel());
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean sendBusInfo(RequestBodyDTO request) throws URISyntaxException, SAXException, IOException, ParserConfigurationException {
		String busOpenApiUrl = BusOpenApiUrl.BUS_OPEN_API_BASE_URL.getUrl() +
				BusOpenApiUrl.BUS_OPEN_API_SERVICE_KEY.getUrl() +
				BusOpenApiUrl.BUS_OPEN_API_KWU_STID.getUrl() +
				BusOpenApiUrl.BUS_OPEN_API_1017_ROUTE_ID.getUrl() +
				BusOpenApiUrl.BUS_OPEN_API_KWU_ORD.getUrl();
		Document responseData = getDataUsingJsoup(busOpenApiUrl);
		
		String firstArriveMessage = responseData.getElementsByTag("arrmsg1").text();
		String secondArriveMessage = responseData.getElementsByTag("arrmsg2").text();
		String busNumber = responseData.getElementsByTag("rtNm").text();
		
		StringBuffer printMessage = new StringBuffer();
		printMessage.append(busNumber + BusOpenApiMessage.BUS_API_MESSAGE.getMessage() +
							BusOpenApiMessage.BUS_API_FIRST_BUS.getMessage() + firstArriveMessage + "\n" +
							BusOpenApiMessage.BUS_API_SECOND_BUS.getMessage() + secondArriveMessage +"\n");
		System.out.println(printMessage);
		sendBotMessageToUser(printMessage.toString(), request.getEvent().getChannel());
		return true;
	}

	@Override
	public boolean sendKwuNotice(RequestBodyDTO request) throws IOException, URISyntaxException {
		String kwuNoitceUrl = KwuUrl.KWU_NOTICE_URL.getUrl();
		Document responseData = getDataUsingJsoup(kwuNoitceUrl);
		
		Elements notices = responseData.select("div.list-box");
		StringBuffer printMessage = new StringBuffer();
		printMessage.append(KwuNoticeMessage.KWU_NOTICE_MESSAGE.getMessage());
		int count = 0;

		for(org.jsoup.nodes.Element el : notices.select("li")) {
			String text = el.select("a").text();
			String info = el.select("p.info").text();
			if(text.contains("Attachment")) text = text.replaceAll("Attachment", "");
			if(text.contains("신규게시글"))	 text = text.replaceAll("신규게시글", "");
			printMessage.append((count+1) + ". " + text + "\n" + info + "\n\n");
			count++;
			if(count==25) break;
		}
		System.out.println(printMessage);
		sendBotMessageToUser(printMessage.toString(), request.getEvent().getChannel());
		return true;
	}

	@Override
	public boolean sendKwuStudyRoomSeat(RequestBodyDTO request) throws IOException, URISyntaxException {
		String kwuStudyRoomUrl = KwuUrl.KWU_STUDY_ROOM_URL.getUrl();
		Document responseData = getDataUsingJsoup(kwuStudyRoomUrl);
		
		Elements table = responseData.select("tr");
		StringBuffer printMessage = new StringBuffer();
		printMessage.append(KwuStudyRoomMessage.KWU_STUDY_ROOM_SEAT_INFO_MESSAGE.getMessage());
		for(org.jsoup.nodes.Element tableRow : table) {
			Elements seatInfo = tableRow.select("td");
			String roomNum = seatInfo.get(0).text();
			if(roomNum.matches("[1-3]")){
				printMessage.append("제 " + roomNum + " 열람실 : " + seatInfo.get(2).text() + "/" + seatInfo.get(3).text() + "/" + seatInfo.get(4).text()+ "\n");
			}
		}
		System.out.println(printMessage);
		sendBotMessageToUser(printMessage.toString(), request.getEvent().getChannel());
		return true;
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
	
	private Document getDataUsingJsoup(String url) throws IOException {
		Document doc = Jsoup.connect(url).ignoreContentType(true).get();
		return doc;
	}

	private void sendBotMessageToUser(String message, String channel) throws URISyntaxException {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add("text", message);
		parameters.add("channel", channel);
		parameters.add("token", "xoxb-892170117313-902396819217-ZlU9s06beJ3MMsgNFvKsKN1v");

		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "https://slack.com/api/chat.postMessage";
		URI uri = new URI(baseUrl);

		ResponseEntity<String> response = restTemplate.postForEntity(uri, parameters, String.class);
		restTemplate.delete(baseUrl);
	}
}
