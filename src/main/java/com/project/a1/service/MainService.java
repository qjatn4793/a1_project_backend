package com.project.a1.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
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
import com.project.a1.vo.Keyword;
import com.project.a1.vo.PdfResponseVO;
import com.project.a1.vo.RequestBodyVO;
import com.project.a1.vo.ResponseBodyVO;
import com.project.a1.vo.SearchResultVO;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.util.common.model.Pair;
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
    
    // RestTemplate 생성
    RestTemplate restTemplate = new RestTemplate();

    public String getGPTAnswer(String content) {
        String result = null;
        RequestBodyVO RequestBodyVO = new RequestBodyVO();
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> system = new HashMap<>();
        system.put("role", "system");
        system.put("content", "");
        Map<String, Object> user = new HashMap<>();
        user.put("role", "user");
        user.put("content", "다음 대화를 완성하시오 고객: " + content);
        messages.add(system);
        messages.add(user);
        List<String> stop = new ArrayList<>();
        stop.add("고객:");
        
        try {
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
    	
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Naver-Client-Id", naverClientKey);
        headers.add("X-Naver-Client-Secret", naverClientSecretKey);
        
    	ResponseEntity<String> responseEntity = restTemplate.exchange(
    			naverApiUrl + "?query=" + item + "&display=12&start=1&sort=sim",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        
    	Gson gson = new Gson();
    	
    	SearchResultVO searchResultVO = gson.fromJson(responseEntity.getBody(), SearchResultVO.class);
    	List<Keyword> topKeyWord = findTopWords(responseEntity.getBody(), 10);
    	searchResultVO.setKeywords(topKeyWord);
    	
        /*
    	Gson gson = new Gson();
    	
    	SearchResultVO searchResultVO = gson.fromJson("{\"items\":[{\"title\":\"[산업 이모저모]현대자동차, 연구개발 부문 세 자릿수 규모 경력직 채용 나선...\",\"originallink\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d305848\",\"link\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d305848\",\"description\":\"[사진\\u003d현대차] ㅇ..\\u003cb\\u003e기아\\u003c/b\\u003e가 79년 역사를 지닌 모빌리티 기업으로서의 시작점을 재조명한다. \\u003cb\\u003e기아\\u003c/b\\u003e는 1944년... SK텔레콤이 국내 최고 수준의 \\u003cb\\u003eAICC\\u003c/b\\u003e 개발사 페르소나에이아이에 주요 전략적투자자(SI)로 참여해 3대 주주에... \",\"pubDate\":\"Mon, 21 Aug 2023 17:13:00 +0900\"},{\"title\":\"하반기 이통사 수익성 전망에 \\u0027그늘\\u0027...이통사, 모빌리티 신사업 추진\",\"originallink\":\"http://www.metroseoul.co.kr/article/20230815500080\",\"link\":\"http://www.metroseoul.co.kr/article/20230815500080\",\"description\":\"KT는 \\u003cb\\u003eAICC\\u003c/b\\u003e(AI컨택센터) 분야에서 2025년 매출 3000억원을 달성하고, 하반기에 \\u0027믿음\\u0027을 출시해 애플리케이션... 올해부터 현대차 및 \\u003cb\\u003e기아\\u003c/b\\u003e차 등 완성차 브랜드에 커넥티드용 무선통신 회선을 독점 공급하게 된 만큼... \",\"pubDate\":\"Tue, 15 Aug 2023 10:32:00 +0900\"},{\"title\":\"LGU+, 5G·알뜰폰 가입자 성장에 2분기 영업익 16% 증가\",\"originallink\":\"https://biz.chosun.com/it-science/ict/2023/08/08/NGYRDAAGARB5JGBN4PSWNJB6PA/?utm_source\\u003dnaver\\u0026utm_medium\\u003doriginal\\u0026utm_campaign\\u003dbiz\",\"link\":\"https://n.news.naver.com/mnews/article/366/0000922730?sid\\u003d105\",\"description\":\"IDC 사업, 기업인프라 매출 견인… \\u003cb\\u003eAICC\\u003c/b\\u003e·스마트모빌리티 등 B2B 신사업 성과 기업 회선, 솔루션, IDC(인터넷... 그는 “올해 초부터 현대차그룹과의 제휴 확대로 제네시스 등 현대\\u003cb\\u003e기아\\u003c/b\\u003e차 전 차종에 무선통신회선을... \",\"pubDate\":\"Tue, 08 Aug 2023 07:57:00 +0900\"},{\"title\":\"[주간 클라우드 동향/12월②] 4년만에 결정난 美 국방부 클라우드 사업 JWCC\",\"originallink\":\"https://www.ddaily.co.kr/news/article.html?no\\u003d253095\",\"link\":\"https://n.news.naver.com/mnews/article/138/0002138572?sid\\u003d105\",\"description\":\"과거 \\u003cb\\u003e기아\\u003c/b\\u003e 오너스매뉴얼(KIA Owner\\u0027s manual) 구축 프로젝트와 국내 주요 대기업의 수요 예측 프로젝트를... AI 기술을 이용한 AI 클라우드 컨택센터(CC)를 구독형 서비스로 신규 출시, 초기 구축 비용 부담 없이 \\u003cb\\u003eAICC\\u003c/b\\u003e... \",\"pubDate\":\"Mon, 12 Dec 2022 13:52:00 +0900\"},{\"title\":\"AI 역량 강화에 몰두한 통신사들, 핵심 전략은?\",\"originallink\":\"https://www.epnc.co.kr/news/articleView.html?idxno\\u003d238300\",\"link\":\"https://www.epnc.co.kr/news/articleView.html?idxno\\u003d238300\",\"description\":\"계획\\u0026quot;이라며 \\u0026quot;AI컨택센터(\\u003cb\\u003eAICC\\u003c/b\\u003e)나 기가지니 등 기존 KT 사업에도 AI를 적용, 수익성 향상과 KT 그룹 전체의... 현재 LG유플러스는 현대·\\u003cb\\u003e기아\\u003c/b\\u003e차에 \\u0027U+모바일tv\\u0027를 제공하며 인포테인먼트 서비스를 확대 중이다. 로봇... \",\"pubDate\":\"Thu, 09 Nov 2023 11:02:00 +0900\"},{\"title\":\"[산업 이모저모] LG유플러스, 콘텐츠 전문 브랜드 \\u0027STUDIO X+U\\u0027 론칭...티빙 ...\",\"originallink\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d298473\",\"link\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d298473\",\"description\":\"ㅇ..현대자동차·\\u003cb\\u003e기아\\u003c/b\\u003e는 대구광역시 엑스코(EXCO)에서 이달 27일부터 29일까지 열리는 \\u00272022 대구 국제... \\u003cb\\u003eAICC\\u003c/b\\u003e·초거대AI·BigData(CU2.0) 등 DIGICO 기반 기술을 확보하여 상용화 또는 다양한 업종으로 적용을 확대하고, AI인재... \",\"pubDate\":\"Thu, 27 Oct 2022 19:15:00 +0900\"},{\"title\":\"[산업 이모저모] LG유플러스, AI 브랜드 \\u0027익시(ixi)\\u0027 공개...\\u0026quot;친구 같은 AI 플...\",\"originallink\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d298415\",\"link\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d298415\",\"description\":\"스포츠 승부예측 등 놀이 서비스부터 소상공인을 대상으로 한 \\u003cb\\u003eAICC\\u003c/b\\u003e까지 앞으로 LG유플러스의 B2C와 B2B... ㅇ..\\u003cb\\u003e기아\\u003c/b\\u003e의 사회복지관 노후차량 정비지원 사업 \\u0027K-모빌리티 케어\\u0027가 8년째를 맞이했다. \\u003cb\\u003e기아\\u003c/b\\u003e는 25일(화) 서울... \",\"pubDate\":\"Tue, 25 Oct 2022 17:01:00 +0900\"},{\"title\":\"[산업 이모저모] \\u003cb\\u003e기아\\u003c/b\\u003e, 글로벌 누적 판매량 5000만대 돌파…해외 수출 성장이...\",\"originallink\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d289988\",\"link\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d289988\",\"description\":\"\\u003cb\\u003e기아\\u003c/b\\u003e는 전 세계 시장에서 누적 판매량 5000만대를 돌파했습니다.  2017년 누적 판매량 4000만대를 기록한 데... 광주광역시 서구는 지난 2월 전국 최초로 KT \\u003cb\\u003eAICC\\u003c/b\\u003e(AI Contact Center, AI 컨택센터)를 적용한 AI 복지사 서비스를... \",\"pubDate\":\"Thu, 03 Jun 2021 17:04:00 +0900\"},{\"title\":\"[산업 이모저모] \\u003cb\\u003e기아\\u003c/b\\u003e-SK이노베이션, 전기차 배터리 재이용·재활용 체계 공동...\",\"originallink\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d289226\",\"link\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d289226\",\"description\":\"\\u003cb\\u003e기아\\u003c/b\\u003e와 SK이노베이션이 사용을 완료한 전기차 배터리를 친환경적으로 처리 가능한 시스템을 갖추는 데... ㅇ..KT는 개발한 클라우드 기반 인공지능 컨택센터(이하 \\u003cb\\u003eAICC\\u003c/b\\u003e: AI Contact Center) 서비스가 SaaS 표준등급... \",\"pubDate\":\"Thu, 29 Apr 2021 17:16:00 +0900\"},{\"title\":\"LGU+, 2Q 영업익 2880억 원···무선 가입자 두 자릿수↑\",\"originallink\":\"https://www.thereport.co.kr/news/articleView.html?idxno\\u003d39280\",\"link\":\"https://www.thereport.co.kr/news/articleView.html?idxno\\u003d39280\",\"description\":\"그러나 올해 2분기 \\u003cb\\u003eAICC\\u003c/b\\u003e, 메타버스, 스마트팩토리, 스마트모빌리티 등 B2B 신사업의 성과 가시화로 직전... 있다\\u0026quot;며 \\u0026quot;현대·\\u003cb\\u003e기아\\u003c/b\\u003e·제네시스 차에 커넥티드카 통신망 독점 공급, 도요타 차량에 인포테인먼트... \",\"pubDate\":\"Tue, 08 Aug 2023 15:58:00 +0900\"},{\"title\":\"[산업 이모저모]정의선 현대자동차그룹 회장 인도 중장기 모빌리티 전략 점검\",\"originallink\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d305583\",\"link\":\"http://www.greened.kr/news/articleView.html?idxno\\u003d305583\",\"description\":\"정의선 회장은 7일부터 이틀간 현대차·\\u003cb\\u003e기아\\u003c/b\\u003e 인도기술연구소와 현대자동차 인도공장을 둘러보고, 현지... 컨택센터(\\u003cb\\u003eAICC\\u003c/b\\u003e) 도입 및 전환에 소요되는 비용과 시간을 대폭 절감하고 있다고 밝혔다. 카카오 i 커넥트 센터... \",\"pubDate\":\"Tue, 08 Aug 2023 17:31:00 +0900\"},{\"title\":\"수익성 후퇴한 LGU+…\\u0026quot;4분기 회복\\u0026quot; 자신\",\"originallink\":\"http://www.dailyimpact.co.kr/news/articleView.html?idxno\\u003d106043\",\"link\":\"http://www.dailyimpact.co.kr/news/articleView.html?idxno\\u003d106043\",\"description\":\"현재 LG유플러스는 KG모빌리티와 토요타에는 카인포테인먼트 플랫폼을, 현대\\u003cb\\u003e기아\\u003c/b\\u003e자동차에는 U+모바일tv를... B2B 성장 가능성이 큰 AI고객센터(\\u003cb\\u003eAICC\\u003c/b\\u003e) 사업도 확대해나간다. 전기차충전 합작회사 설립도 예정대로... \",\"pubDate\":\"Tue, 07 Nov 2023 14:30:00 +0900\"}],\"Keywords\":[{\"text\":\"기아\",\"value\":12},{\"text\":\"사업\",\"value\":8},{\"text\":\"센터\",\"value\":8},{\"text\":\"티\",\"value\":7},{\"text\":\"빌리\",\"value\":7},{\"text\":\"모\",\"value\":6},{\"text\":\"이모저모\",\"value\":6},{\"text\":\"택\",\"value\":6},{\"text\":\"산업\",\"value\":6},{\"text\":\"서비스\",\"value\":5}]}", SearchResultVO.class);
    	*/
    	
    	// 네이버 응답 값
    	return searchResultVO;
    }

    public PdfResponseVO analyzePDF(MultipartFile file) throws Exception {
    	PdfResponseVO result = new PdfResponseVO();
		File source = new File(file.getOriginalFilename());
		source.createNewFile();
	    FileOutputStream fos = new FileOutputStream(source);
	    fos.write(file.getBytes());
	    fos.close();

		PDDocument pdfDoc = PDDocument.load(source);
		String text = new PDFTextStripper().getText(pdfDoc);
		String frontText = text.substring(0, 300).replaceAll(" ", "");
		String backText = text.substring(text.length() - 700, text.length() - 1).replaceAll(" ", "");
		// 첫 문장을 기업명으로 예상 (추후 보완 필요)
		result.setCompany(file.getOriginalFilename().split(" ")[0]);
        
		/*
        CompletableFuture<Void> apiResponseFuture = CompletableFuture
                .supplyAsync(() -> getGPTAnswer(frontText + "이 RFP에서 프로젝트(사업) 배경 및 목적, 사업 개요, 추진 일정을 요약해줘"))
                .thenCompose(summary -> CompletableFuture.supplyAsync(() -> getGPTAnswer(backText + "이 RFP에서 평가 기준이 뭔지 알려줘"))
                .thenAccept(evaluationStandard -> {
                	result.setSummary(summary);
                    result.setEvaluationStandard(evaluationStandard);
                }));
        */
        
		CompletableFuture<Void> apiResponseFuture = CompletableFuture
                .supplyAsync(() -> getGPTAnswer(frontText + "이 RFP에서 1.사업개요, 2.추진일정, 3.요구사항, 4.제안안내사항을 항목을 나눠서 알려줘"))
                .thenCompose(summary -> CompletableFuture.supplyAsync(() -> getGPTAnswer(backText + "이 RFP에서 1.사업개요, 2.추진일정, 3.요구사항, 4.제안안내사항을 항목을 나눠서 알려줘"))
                .thenAccept(evaluationStandard -> {
                	
                	if (!evaluationStandard.trim().endsWith(".")) {
                        evaluationStandard += "...";
                    }
                	
                	result.setSummary(summary);
                    result.setEvaluationStandard(evaluationStandard);
                }));
        
        // 모든 비동기 작업이 완료될 때까지 대기
        try {
        	apiResponseFuture.get();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		// 키워드
        List<Keyword> topKeyWord = findTopWords(text, 10);
		result.setKeywords(topKeyWord);
		result.setFileName(file.getOriginalFilename());

		return result;
    }
    
    public static List<Keyword> findTopWords(String text, int limit) {
    	List<Keyword> result = new ArrayList<>();
    	
    	Komoran Komoran = new Komoran(DEFAULT_MODEL.LIGHT);
		
		//분석할 문장에 대해 정제(쓸데없는 특수문자 제거)
		String replace_text = text.replace("[^가-힣a-zA-Z0-9", " ");

		//분석할 문장의 앞, 뒤에 존재할 수 있는 필요없는 공백 제거
		String trim_text = replace_text.trim();
		//형태소 분석 시작
		KomoranResult analyzeResultList = Komoran.analyze(trim_text);
		
		//형태소 분석 결과 중 명사만 가져오기
		List<String> rList = analyzeResultList.getNouns();
		
		//List에 존재하는 중복되는 단어들의 중복제거를 위해 set 데이터타입에 데이터를 저장합니다.
		//rSet 변수는 중복된 데이터가 저장되지 않기 떄문에 중복되지 않은 단어만 저장하고 나머지는 자동 삭제합니다.
		Set<String> rSet = new HashSet<String>(rList);
		
		//중복이 제거된 단어 모음에 빈도수를 구하기 위해 반복문을 사용합니다.
		Iterator<String> it = rSet.iterator();
		
		while(it.hasNext()) {
			Keyword keyword = new Keyword();
			
			//중복 제거된 단어
			String word = it.next();
			
			// 단어 길이 확인 2글자 제외
		    if (word.length() > 2) {
				//단어가 중복 저장되어 있는 pList로부터 단어의 빈도수 가져오기
				int frequency = Collections.frequency(rList, word);
				
				keyword.setText(word);
				keyword.setValue(frequency);
				
				result.add(keyword);
		    }
		}
		
		result = result.stream().sorted(Comparator.comparing(Keyword::getValue).reversed()).collect(Collectors.toList()).subList(0, Math.min(result.size(), limit));

        return result;
    }
    
}
