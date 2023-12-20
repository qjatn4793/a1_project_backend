package com.project.a1.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.a1.vo.GptItem;
import com.project.a1.vo.RequestBodyVO;
import com.project.a1.vo.ResponseBodyVO;
import com.project.a1.vo.SearchResultVO;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MainService {
	
	@Value("${openAi.api.url}")
    private String openAi_api_url;
	
	@Value("${openAi.api.key}")
    private String openAi_api_key;
  
    @Value("${openAi.api.model}")
    private String openAi_api_model;
  
    @Value("${openAi.api.maxToken}")
    private int openAi_api_maxToken;
  
    @Value("${openAi.api.temperature}")
    private float openAi_api_temperature;
  
    @Value("${openAi.api.top_p}")
    private float openAi_api_top_p;
  
    @Value("${openAi.api.frequency_penalty}")
    private float openAi_api_frequency_penalty;
  
    @Value("${openAi.api.presence_penalty}")
    private float openAi_api_presence_penalty;
    
    @Value("${naver.client.key}")
    private String naverClientKey;

    @Value("${naver.client.secret.key}")
    private String naverClientSecretKey;
    
    @Value("${naver.api.url}")
    private String naverApiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
  
    public String getGPTAnswer(String content) {
        String result = null;
        RequestBodyVO RequestBodyVO = new RequestBodyVO();
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> system = new HashMap<>();
        system.put("role", "system");
        system.put("content", "상담원: ");
        Map<String, Object> user = new HashMap<>();
        user.put("role", "user");
        user.put("content", "다음 대화를 완성하시오 고객: " + content);
        messages.add(system);
        messages.add(user);
        List<String> stop = new ArrayList<>();
        stop.add("고객:");
        
        try {
          // RestTemplate 생성
          RestTemplate restTemplate = new RestTemplate();

          // 요청 헤더 생성
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          headers.set("Authorization", "Bearer " + openAi_api_key);
          
          // 요청 바디 생성
          RequestBodyVO.setModel(openAi_api_model);
          RequestBodyVO.setMessages(messages);
          RequestBodyVO.setMax_tokens(openAi_api_maxToken);
          RequestBodyVO.setTemperature(openAi_api_temperature);
          RequestBodyVO.setTop_p(openAi_api_top_p);
          RequestBodyVO.setFrequency_penalty(openAi_api_frequency_penalty);
          RequestBodyVO.setPresence_penalty(openAi_api_presence_penalty);
          RequestBodyVO.setStop(stop);
          
          ObjectMapper mapper = new ObjectMapper();
          log.info("RequestBodyVO: {}", mapper.writeValueAsString(RequestBodyVO));

          // HttpEntity 생성
          HttpEntity<String> requestEntity = new HttpEntity<>(mapper.writeValueAsString(RequestBodyVO), headers);
          
          // API 호출
          ResponseBodyVO response = restTemplate.exchange(openAi_api_url, HttpMethod.POST, requestEntity, ResponseBodyVO.class).getBody();
          result = String.valueOf(response.getChoices().get(0).getMessage().get("content"));

          log.info("chatGPT result: {}", result);
          
        } catch(Exception e) {
          e.printStackTrace();
          result = "에러";
        }
        
        return result;
      }
    
    public SearchResultVO getNaverApiDate(String item) {
    	
    	/*
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Naver-Client-Id", naverClientKey);
        headers.add("X-Naver-Client-Secret", naverClientSecretKey);
        
    	ResponseEntity<String> responseEntity = restTemplate.exchange(
    			naverApiUrl + "?query=" + item + "&display=10&start=1&sort=sim",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        
    	Gson gson = new Gson();
    	
    	SearchResultVO searchResultVO = gson.fromJson(responseEntity.getBody(), SearchResultVO.class);
    	*/
    	
    	Gson gson = new Gson();
    	
    	SearchResultVO searchResultVO = gson.fromJson("{\"items\":[{\"title\":\"[금융 이모저모] NH\\u003cb\\u003e농협\\u003c/b\\u003e은행 IT사랑봉사단, 쌀 나눔 행사 실시\",\"originallink\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d308922\",\"link\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d308922\",\"description\":\"NH\\u003cb\\u003e농협\\u003c/b\\u003e은행 IT사랑봉사단이 지난 24일 지난 24일 경기 안양시 관내 복지관을 찾아 쌀 1500Kg을 전달했다고... ○...카카오뱅크, \\u0027금융 분야 최대\\u0027 AI 국제 학회서 국내 은행 최초로 논문 발표...\\u0026quot;\\u003cb\\u003eAICC\\u003c/b\\u003e 기술력 인정받아... \",\"pubDate\":\"Tue, 28 Nov 2023 08:51:00 +0900\"},{\"title\":\"\\u003cb\\u003e농협\\u003c/b\\u003e중앙회, 범\\u003cb\\u003e농협\\u003c/b\\u003e 인공지능콘택트센터 구축한다\",\"originallink\":\"https://www.etnews.com/20231018000212\",\"link\":\"https://n.news.naver.com/mnews/article/030/0003146467?sid\\u003d101\",\"description\":\"\\u003cb\\u003e농협\\u003c/b\\u003e중앙회는 최근 \\u003cb\\u003e농협\\u003c/b\\u003e은행, 상호금융, \\u003cb\\u003e농협\\u003c/b\\u003e생명, 손해보험 등 계열사를 아우르는 \\u0027범\\u003cb\\u003e농협\\u003c/b\\u003e NH \\u003cb\\u003eAICC\\u003c/b\\u003e 구축\\u0027에 돌입했다. 계열사 간 표준화된 \\u003cb\\u003eAICC\\u003c/b\\u003e 모델을 운영해 업무 효율성을 높인다는 방침이다. \\u003cb\\u003e농협\\u003c/b\\u003e중앙회는 올해... \",\"pubDate\":\"Wed, 18 Oct 2023 14:16:00 +0900\"},{\"title\":\"챗GPT 컨택센터에도 접목…범\\u003cb\\u003e농협 AICC\\u003c/b\\u003e구축 추진\",\"originallink\":\"https://www.ddaily.co.kr/news/article.html?no\\u003d260669\",\"link\":\"https://n.news.naver.com/mnews/article/138/0002145443?sid\\u003d105\",\"description\":\"31일 관련업계에 따르면 농협이 ‘범\\u003cb\\u003e농협 AICC\\u003c/b\\u003e(AI 컨택센터) 구축 컨설팅’ 사업을 발주하고 사업자 선정을 마무리하고 컨설팅 사업에 들어간다. 농협은 챗GPT 등 AI 기술의 혁신적인 변화에 대응하는 한편 금융권의 AICC... \",\"pubDate\":\"Fri, 31 Mar 2023 10:08:00 +0900\"},{\"title\":\"[주간 서머리] 경제·산업계 \\u0026quot;작년 5대 은행 직원 연봉 1억원 넘어·대형 건설...\",\"originallink\":\"http://www.asiaa.co.kr/news/articleView.html?idxno\\u003d146111\",\"link\":\"http://www.asiaa.co.kr/news/articleView.html?idxno\\u003d146111\",\"description\":\"NH\\u003cb\\u003e농협\\u003c/b\\u003e은행 2억2513만원이다. [사진\\u003dLG유플러스] ◇IT·전자업계 소식 △LG유플러스, \\u0027AI 3대 서비스\\u0027 로 B2B 시장 공략 박차\\u003dLG유플러스가 \\u003cb\\u003eAICC\\u003c/b\\u003e와 소상공인 AI 솔루션을 기반으로 B2B AI 사업을 본격화한다. 구체적으로 △\\u0027U... \",\"pubDate\":\"Fri, 03 Nov 2023 17:02:00 +0900\"},{\"title\":\"\\u003cb\\u003e농협\\u003c/b\\u003e중앙회, 디지털혁신위원회 개최... 빅데이터플랫폼, 인공지능 고객센터 등...\",\"originallink\":\"https://www.aitimes.kr/news/articleView.html?idxno\\u003d28349\",\"link\":\"https://www.aitimes.kr/news/articleView.html?idxno\\u003d28349\",\"description\":\"이날 회의는 이재식 \\u003cb\\u003e농협\\u003c/b\\u003e중앙회 부회장(위원장)을 비롯한 15명의 내·외부 디지털혁신위원이 참석한 가운데 \\u0027범\\u003cb\\u003e농협\\u003c/b\\u003e 빅데이터플랫폼(N-Hub)\\u0027, \\u0027인공지능 컨택센터(\\u003cb\\u003eAICC\\u003c/b\\u003e)\\u0027,  \\u0027데이터비즈니스\\u0027 등 범\\u003cb\\u003e농협\\u003c/b\\u003e 디지털혁신을 위한... \",\"pubDate\":\"Mon, 26 Jun 2023 21:34:00 +0900\"},{\"title\":\"[인터뷰] 양정기 \\u003cb\\u003e농협\\u003c/b\\u003e은행 콜인프라운영팀 팀장 \\u0027AI콜봇RPA\\u0027 통한 \\u0027고도화\\u0027\",\"originallink\":\"http://www.newsprime.co.kr/news/article.html?no\\u003d586219\",\"link\":\"http://www.newsprime.co.kr/news/article.html?no\\u003d586219\",\"description\":\"그는 \\u0026quot;이번 프로젝트에서 AI가 전화 상담부터 업무처리까지 완료하는 시스템을 구축했다\\u0026quot;며 \\u0026quot;진보된 \\u003cb\\u003eAICC\\u003c/b\\u003e로의 진화와 함께 상담사 업무 경감에 도움이 되고자 한다\\u0026quot;라고 추진 배경에 관해 설명했다. \\u003cb\\u003e농협\\u003c/b\\u003e은행... \",\"pubDate\":\"Mon, 28 Nov 2022 14:12:00 +0900\"},{\"title\":\"KT, 기업 DX 방안 \\u0027\\u003cb\\u003eAICC\\u003c/b\\u003e 기술\\u0027 제안…AI 콜센터 도입한 결과는?\",\"originallink\":\"http://www.aitimes.com/news/articleView.html?idxno\\u003d139052\",\"link\":\"http://www.aitimes.com/news/articleView.html?idxno\\u003d139052\",\"description\":\"KT는 AI와 DX기술을 융합한 \\u0027AI컨택센터(\\u003cb\\u003eAICC\\u003c/b\\u003e)\\u0027를 효율적으로 잘 운영해 성과를 내고 있다고 밝혔다. 기존... NH\\u003cb\\u003e농협\\u003c/b\\u003e손해보험, 라이나생명 등 은행, 카드, 보험, 핀테크 등 금융 분야의 다양한 고객이 \\u0027KT 클라우드\\u0027를... \",\"pubDate\":\"Thu, 17 Jun 2021 11:18:00 +0900\"},{\"title\":\"\\u0027은행 맏형\\u0027으로 복귀한 조용병 전 신한금융 회장\",\"originallink\":\"http://news.bizwatch.co.kr/article/finance/2023/11/17/0027\",\"link\":\"https://n.news.naver.com/mnews/article/648/0000021118?sid\\u003d101\",\"description\":\"전 \\u003cb\\u003e농협\\u003c/b\\u003e중앙회 부회장 등에 이어 다섯번째다. 조 전 회장은 은행장 및 회장 시절 금융당국과 함께 다양한... 신한금융, 그룹통합 AI 컨택센터 \\u0027\\u003cb\\u003eAICC\\u003c/b\\u003e\\u0027 구축 신한금융그룹은 그룹 통합 AI 컨택센터(\\u003cb\\u003eAICC\\u003c/b\\u003e·AI Contact Center) 플랫폼... \",\"pubDate\":\"Sat, 18 Nov 2023 08:02:00 +0900\"},{\"title\":\"\\u003cb\\u003e농협\\u003c/b\\u003e은행, 고객과 직원이 행복한 고객행복센터 운영\",\"originallink\":\"http://www.newsprime.co.kr/news/article.html?no\\u003d582390\",\"link\":\"http://www.newsprime.co.kr/news/article.html?no\\u003d582390\",\"description\":\"1000여명의 직원과 함께 일 평균 5만건의 고객 전화를 응대하고 있는 \\u003cb\\u003e농협\\u003c/b\\u003e은행 콜센터는 용산센터를 비롯... 활용해 \\u003cb\\u003eAICC\\u003c/b\\u003e 추진에 박차를 가하고 있다. 또한, 인천센터 부지 선정 시 상담사의 출퇴근 접근성과 편의성을... \",\"pubDate\":\"Tue, 18 Oct 2022 09:28:00 +0900\"},{\"title\":\"[영상] ECS텔레콤 류기동 박사, \\u0027옴니채널 AI 컨택센터의 고객 경험 가치 창출...\",\"originallink\":\"http://www.aitimes.kr/news/articleView.html?idxno\\u003d20691\",\"link\":\"http://www.aitimes.kr/news/articleView.html?idxno\\u003d20691\",\"description\":\"컨퍼런스(\\u003cb\\u003eAICC\\u003c/b\\u003e 2021)\\u0027가 지난달 30일, 라이브로디브이(LiverTV) 메인 스튜디오에서 온라인 라이브 컨퍼런스로... NH\\u003cb\\u003e농협\\u003c/b\\u003e은행 인공지능 빅데이터 컨택센터 구축 등의 지능형 옴니채널 서비스를 위한 AI 기반의 컨택센터... \",\"pubDate\":\"Sat, 03 Apr 2021 14:46:00 +0900\"}]}", SearchResultVO.class);
    	
    	// 네이버 응답 값
    	return searchResultVO;
    }
    
    public Map<String, Object> analyzePDF(MultipartFile file) throws Exception {
    	Map<String, Object> result = new HashMap<>();
		File source = new File(file.getOriginalFilename());
		source.createNewFile();
	    FileOutputStream fos = new FileOutputStream(source);
	    fos.write(file.getBytes());
	    fos.close();

		PDDocument pdfDoc = PDDocument.load(source);
		String text = new PDFTextStripper().getText(pdfDoc);
		
		// 키워드
		Map<String, Integer> topKeyWord = findTopWords(text, 10);
		result.put("keyWord", topKeyWord);

		return result;
    }
    
    public static Map<String, Integer> findTopWords(String text, int limit) {
    	Komoran Komoran = new Komoran(DEFAULT_MODEL.LIGHT);
		
		//분석할 문장에 대해 정제(쓸데없는 특수문자 제거)
		String replace_text = text.replace("[^가-힣a-zA-Z0-9", " ");

		//분석할 문장의 앞, 뒤에 존재할 수 있는 필요없는 공백 제거
		String trim_text = replace_text.trim();
		//형태소 분석 시작
		KomoranResult analyzeResultList = Komoran.analyze(trim_text);
		
		//형태소 분석 결과 중 명사만 가져오기
		List<String> rList = analyzeResultList.getNouns();
		
		//단어 빈도수(사과, 3) 결과를 저장하기 위해 Map객체 생성합니다.
		Map<String, Integer> rMap = new HashMap<>();
		
		//List에 존재하는 중복되는 단어들의 중복제거를 위해 set 데이터타입에 데이터를 저장합니다.
		//rSet 변수는 중복된 데이터가 저장되지 않기 떄문에 중복되지 않은 단어만 저장하고 나머지는 자동 삭제합니다.
		Set<String> rSet = new HashSet<String>(rList);
		
		//중복이 제거된 단어 모음에 빈도수를 구하기 위해 반복문을 사용합니다.
		Iterator<String> it = rSet.iterator();
		
		while(it.hasNext()) {
			//중복 제거된 단어
			String word = it.next();
			
			//단어가 중복 저장되어 있는 pList로부터 단어의 빈도수 가져오기
			int frequency = Collections.frequency(rList, word);
			
			rMap.put(word, frequency);
		}
		
		Map<String, Integer> topMap = rMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return topMap;
    }
    
    
    // JSON 데이터 추출, 재조립 함수
    private String extractJsonData(String input) {
        int startIndex = input.indexOf("{");
        if (startIndex == -1) {
            return "";
        }

        int endIndex = input.lastIndexOf("}");
        if (endIndex == -1) {
            return "";
        }

        String jsonData = input.substring(startIndex, endIndex + 1);
        return jsonData;
    }
	
}
