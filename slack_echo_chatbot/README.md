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