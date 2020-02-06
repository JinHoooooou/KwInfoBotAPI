# Slack Echo Chat Bot 만들기 Flow

1. https://api.slack.com에서 Start Building으로 app만들기

![image-20200119130614441](README.assets/image-20200119130614441.png)

2. app 이름과 작업수행할 workspace선택

![image-20200119130807115](README.assets/image-20200119130807115.png)

3. 첫 화면에서 Bot Users 탭 선택

![image-20200119130851528](README.assets/image-20200119130851528.png)

4. Add Bot User 후 display name, default username작성, always .. on 선택

![image-20200119131256740](README.assets/image-20200119131256740.png)

5. Event Subscriptions에서 Enable Events On

![image-20200119131335435](README.assets/image-20200119131335435.png)

5. 1. Request URL을 입력하기 위한 Spring boot 실행

![image-20200119131940527](README.assets/image-20200119131940527.png)

5. 2. Controller 작성

![image-20200119133351530](README.assets/image-20200119133351530.png)

Request URL 확인용

> @RestController와 @Controller의 차이점
>
> @Controller를 사용했을때 Request URL입력하니 404 코드가 나오고
>
> @RestController를 사용했을때는 200 코드가 나왔다.
>
> 차이점이 뭔지 알아보자

5. 3. Spring boot 실행 후 Forwarding을 위한 ngrok 실행

![image-20200119132614299](README.assets/image-20200119132614299.png)

명령어 : ngork http 8080

Forwarding 된 url을 Request URL에 입력

5. 4. 결과확인

![image-20200119134625650](README.assets/image-20200119134625650.png)

Challenge parameter를 Response로 보내줘야한다.

![image-20200119134700104](README.assets/image-20200119134700104.png)

> @Controller 를 사용했을때는 404, @RestController를 사용했을때는 200

![image-20200119134743022](README.assets/image-20200119134743022.png)

RequestBody로 전송된 Json형태의 파라미터들, challenge parameter값을 return해주어야한다.

5. 5. Response로 전송

![image-20200119135738751](README.assets/image-20200119135738751.png)

> 메소드에 @RespnseBody추가
>
> Challenge code return

![image-20200119135829390](README.assets/image-20200119135829390.png)

> Verified 확인

6. Subscribe to bot events에 이벤트 추가

![image-20200119140139807](README.assets/image-20200119140139807.png)

> app_mention과 message.channels추가

7. Install App으로 workspace에 app추가

![image-20200119140350252](README.assets/image-20200119140350252.png)

8. 메시지 보내보고 테스트

![image-20200120193340389](README.assets/image-20200120193340389.png)

9. Request로 받은 json 데이터들 저장하기위한 DTO 생성

* RequestBodyDTO

``` java
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestBodyDTO {

	private String[] authed_users;
	private String event_id;
	private String api_id;
	private String team_id;
	private String type;
	private String token;
	private Event event;
}
```

* Event

``` java
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event {

	private String client_msg_id;
	private String type;
	private String text;
	private String user;
	private String ts;
	private Object blocks;
	private String channel;
	private String event_ts;
	private String channel_type;
}

```

* ReceiveMessage 메소드 인자를 HashMap → RequestBodyDTO로 변경

``` java
@RequestMapping(value = "/echobot", method = RequestMethod.POST)
	public String ReceiveMessage(@RequestBody RequestBodyDTO request) {
		System.out.println(request);
		System.out.println(request.getEvent());
		return "";
	}
```

![image-20200120193937107](README.assets/image-20200120193937107.png)

> 왜 두번씩 출력되는지는 잘 모르겠음..
>
> @RequestBody를 통해 전달받은 데이터중 Event의 Type이 app_mention(@Slackbot같은) 일 경우에 대한 처리를 해주어야한다.

10. Event객체의 Type이 app_mention일 경우에 대한 처리

```java
	@RequestMapping(value = "/echobot", method = RequestMethod.POST)
	public String ReceiveMessage(@RequestBody RequestBodyDTO request) {
		Event event = request.getEvent();				//RequestBody의 Event
		if(event.getType().equals("app_mention")) {
			String my_text = event.getText();			//Slack channel에 보낸 메세지
			String split_text[] = my_text.split(" ");	
			for(String slack_bot_name : split_text) {	//@slackbottest2 추출
				if(slack_bot_name.equals("<@USJBNQ36D>")) {
					int start_index = my_text.indexOf(slack_bot_name);
					String echo_bot_text = my_text.substring(start_index+(slack_bot_name.length())+1);	// @slackbottest2 이후의 text
				}
			}
		}
		return "";
	}
```

> Echo Bot을 만들고 있기 때문에 @slackbot 부분 이후의 text를 그대로 복사하여 출력해주어야한다. 따라서 @slackbot 이전의 text와 @slackbot은 지워준다.



11. Slack에서 보낸 Text 내용을 slackbottest2가 있는 채널로 요청

``` java
	@RequestMapping(value = "/echobot", method = RequestMethod.POST)
	public String ReceiveMessage(@RequestBody RequestBodyDTO request) throws URISyntaxException {
		Event event = request.getEvent();				//RequestBody의 Event
		if(event.getType().equals("app_mention")) {
			String my_text = event.getText();			//Slack channel에 보낸 메세지
			String split_text[] = my_text.split(" ");	
			for(String slack_bot_name : split_text) {	//@slackbottest2 추출
				if(slack_bot_name.equals("<@USJBNQ36D>")) {
					int start_index = my_text.indexOf(slack_bot_name);
					String echo_bot_text = my_text.substring(start_index+(slack_bot_name.length())+1);	// @slackbottest2 이후의 text
					
					MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
                    parameters.add("text", echo_bot_text);
                    parameters.add("channel", event.getChannel());
                    parameters.add("token", "xoxb-892170117313-902396819217-e0Nd0YSz5LbqUFU9JmDMGmT1");
					
                    System.out.println(parameters);
                    
					RestTemplate restTemplate = new RestTemplate();
					final String baseUrl = "https://slack.com/api/chat.postMessage";
					URI uri = new URI(baseUrl);
					
					ResponseEntity<String> result = restTemplate.postForEntity(uri, parameters, String.class);
					
					System.out.println(result);
					
				}
			}
		}
		return "";
	}
```



---

Challenge 파라미터는 처음에만 설정하고 그 이후에는 필요없는거같다.

계속 Controller에서 Springboot 실행하고 종료하고하는거 너무 귀찮다 좋은방법있나?

---

참고자료

https://api.slack.com/bot-users

태형이형





## Open Api 사용하기

1. 공공데이터 포털 회원가입, 로그인

2. 데이터셋 - 오픈 Api에서 사용할 open api 검색 (서울 버스)

3. 1. 활용 신청 선택

   ![image-20200203173010463](README.assets/image-20200203173010463.png)

3. 2. 바로가기 선택

![image-20200203173058200](README.assets/image-20200203173058200.png)

> 신청할때는 유사한 항목이 매우 많으니 신청해야할거 같다? 라는 생각이들면 다 신청하자. 어짜피 돈이 드는것도아니고 신청하면 어지간하면 바로 승인된다.



4. 1. 개발 계정 신청 작성

   ![image-20200203173347284](README.assets/image-20200203173347284.png)

4. 2. 서울 열린데이터 광장 로그인, 인증키 신청

   ![image-20200203173635822](README.assets/image-20200203173635822.png)

> 둘 다 신청하면 뭐 작성하라고 하는데 양식에 맞게 작성하면 거의 바로 인증키가 발급된다.



5. 양식에 맞게 url로 신청

> 신청 후 바로 테스트해보면 SERVICE KEY IS NOT REGISTERED ERROR라고 나오는데 보통 1시간정도 기다려야 사용할 수 있다고 함
>
> 참고자료 - https://www.data.go.kr/participation/developerNetwork/show.do

6. 실제 사용 예시
   * 서울 열린 데이터 광장에서 서울 버스 정보 인증키 발급받아서 사용

---

## 인줄 알았는데 잘못알았다.

개멘붕.. 위방법이아니라 공공데이터포털에서 찾아써야하는거였다..

![image-20200203182625620](README.assets/image-20200203182625620.png)

얘네들로 공공데이터 포털에 검색하면

![image-20200203182925159](README.assets/image-20200203182925159.png)

요런식으로 나온다.. 와 이거때문에 고생했네 ㅜㅜ.. 서비스 유형이 LINK가 아니라 REST로 찾아야한다. 전부 신청해두자.

![image-20200206145509738](README.assets/image-20200206145509738.png)

먼저 나는 광운대학교 정류장의 1017버스의 도착정보를 알고싶으므로 버스도착정보조회 서비스에 들어가보자.

![image-20200206145559015](README.assets/image-20200206145559015.png)

StId와 busRouteId, ord를 요청하여 응답을받는 구조인거같다. 근데 해당 파라미터에 대한 정보가 없으므로 이 정보들을 먼저 찾아야한다.



![image-20200206145713334](README.assets/image-20200206145713334.png)

http://api.bus.go.kr/contents 를 참고하면 stId는 정류소 ID이고 busRouteId는 노선ID ord는 정류소 순번이다. 해당 정보를 찾기위해 다른 api들을 살펴보면 정류서정보조회 서비스와 노선정보조회 서비스가 있다.



![image-20200206150918364](README.assets/image-20200206150918364.png)

정류소정보조회 서비스 오퍼레이션에 정류소 고유번호를 입력하여 해당 정류소의 도착정보를 조회할 수 있다. 정류소 고유번호는 네이버지도나 카카오맵등을 참고하면 나온다. 내가 찾는 정류소의 고유번호는 11335이므로 입력하면 여러 항목이나오는데 그 중 버스번호가 1017인 item을 찾아보면

![image-20200206160232968](README.assets/image-20200206160232968.png)

staOrd가 5로나온다.

1017버스의 busRouteId는 100100130이고, 광운대학교의 stId는 110000234이고 ord가 5이므로 해당 파라미터에 정보들을 입력하여 요청하면

![image-20200206160433728](README.assets/image-20200206160433728.png)

이런식으로 출력된다.(밑에 더 있음)

해당 URL로 파라미터와 서비스키를 보내면 저런식으로 응답이온다. 저기서 우리는 아직까지는 광운대학교 정류장의 1017버스 도착정보만 알고있으면 되기 때문에 하드코딩으로 그냥 가져온다.

![image-20200206160630017](README.assets/image-20200206160630017.png)

RestTemplate의 getForObject매소드는 인자로 String이 아닌 URI객체로 받기때문에 해당 URL String을 URI로 변환해줘야한다. 그리고 GET방식으로 요청을 보내 String으로 응답을 받는다.

Xml파일을 String으로 응답받았기 때문에 해당 String을 Xml로 파싱해줄 필요가있다. 여기서 사용하는 것이 Document객체이다.

![image-20200206161130201](README.assets/image-20200206161130201.png)

출력확인.(리팩토링과 추가 기능은 나중에 더 해볼예정)



---

