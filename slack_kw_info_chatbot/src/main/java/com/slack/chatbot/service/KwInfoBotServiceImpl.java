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

import com.slack.chatbot.dto.Event;
import com.slack.chatbot.dto.RequestBodyDTO;
import com.slack.chatbot.message.ChatBotMessage;

@Service("kwInfoBotService")
public class KwInfoBotServiceImpl implements KwInfoBotService {

	@Override
	public boolean echoMyMessage(RequestBodyDTO request) throws URISyntaxException {
		Event event = request.getEvent();
		String userMessage = event.getText();
		String splitMessage[] = userMessage.split(" ");
		for (String findSlackBotName : splitMessage) {
			if (findSlackBotName.equals("<@USJBNQ36D>")) {
				int indexOfBotName = userMessage.indexOf(findSlackBotName);
				if (indexOfBotName + findSlackBotName.length() < userMessage.length()) {
					String echoMessage = userMessage.substring(indexOfBotName + findSlackBotName.length() + 1);
					BotSendMessageToUser(echoMessage, event.getChannel());
				}
				break;
			}
		}
		return true;
	}
	
	@Override
	public boolean sendBusInfo(RequestBodyDTO request) throws URISyntaxException, SAXException, IOException, ParserConfigurationException {
		// request to open api
		final String busAPiBaseUrl = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute"
				+ "?serviceKey=7jHs%2Bx3TFCBGi9X0WFnQeUXUpCC7KJLJ1aA%2BmM3j8cJau%2B67c22LhWy%2FmkWvLG7m%2BieC4iQay%2FgroMRytDrmzQ%3D%3D"
				+ "&stId=110000234"
				+ "&busRouteId=100100130"
				+ "&ord=5";
		URI busApiUri = new URI(busAPiBaseUrl);		
		RestTemplate busApiRestTemplate = new RestTemplate();

		String responseOpenApiToSpring = busApiRestTemplate.getForObject(busApiUri, String.class);

		StringReader sr = new StringReader(responseOpenApiToSpring);
		InputSource is = new InputSource(sr);
		try {
			Document parseXmlData = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			Element rootTag = parseXmlData.getDocumentElement();
			String firstArriveMessage1 = rootTag.getElementsByTagName("arrmsg1").item(0).getFirstChild().getNodeValue();
			String secondArriveMessage2 = rootTag.getElementsByTagName("arrmsg2").item(0).getFirstChild().getNodeValue();
			String busNumber = rootTag.getElementsByTagName("rtNm").item(0).getFirstChild().getNodeValue();
			StringBuffer printMessage = new StringBuffer();
			printMessage.append(busNumber + "번 버스 도착 정보 입니다\n" + "첫번째 버스 : "
					+ firstArriveMessage1 + "\n" + "두번째 버스 : " + secondArriveMessage2);
			
			BotSendMessageToUser(printMessage.toString(), request.getEvent().getChannel());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean sendNoticeKwInfo(RequestBodyDTO request) throws IOException, URISyntaxException {
		
		
		String url = "https://www.kw.ac.kr/ko/life/notice.jsp";
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		Elements element = doc.select("div.list-box");
		
		StringBuffer printMessage = new StringBuffer();
		printMessage.append("공지사항 : " + url + "\n\n" );
		int count = 0;

		for(org.jsoup.nodes.Element el : element.select("li")) {
			String text = el.select("a").text();
			String info = el.select("p.info").text();
			if(text.contains("Attachment")) text = text.replaceAll("Attachment", "");
			if(text.contains("신규게시글"))	 text = text.replaceAll("신규게시글", "");
			printMessage.append((count+1) + ". " + text + "\n" + "\t\t" + info + "\n\n\n");
			count++;
			if(count==25)
				break;
		}
		System.out.println(printMessage.toString());
		BotSendMessageToUser(printMessage.toString(), request.getEvent().getChannel());
	}

	@Override
	public boolean sendStudyRoomSeatInfo(RequestBodyDTO request) throws IOException, URISyntaxException {
		String url = "http://mobileid.kw.ac.kr/seatweb/domian5.asp";
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		
		Elements table = doc.getElementsByTag("tr");
		StringBuffer printMessage = new StringBuffer();
		printMessage.append("도서관 열람실 정보(전체/사용/잔여)\n");
		for(org.jsoup.nodes.Element tableRow : table) {
			Elements seatInfo = tableRow.getElementsByTag("td");
			String roomNum = seatInfo.get(0).text();
			if(roomNum.matches("[1-3]")){
				printMessage.append("제 " + roomNum + " 열람실 : " + seatInfo.get(2).text() + "/" + seatInfo.get(3).text() + "/" + seatInfo.get(4).text()+ "\n");
			}
		}
		BotSendMessageToUser(printMessage.toString(), request.getEvent().getChannel());		
	}
	
	
	private void BotSendMessageToUser(String message, String channel) throws URISyntaxException {

		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add("text", message);
		parameters.add("channel", channel);
		parameters.add("token", "xoxb-892170117313-902396819217-Jmown4sbBelQMsCz2vXrdM97");

		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "https://slack.com/api/chat.postMessage";
		URI uri = new URI(baseUrl);

		ResponseEntity<String> response = restTemplate.postForEntity(uri, parameters, String.class);
		restTemplate.delete(baseUrl);
	}
}
