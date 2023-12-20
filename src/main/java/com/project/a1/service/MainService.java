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
import com.project.a1.vo.RequestBodyVO;
import com.project.a1.vo.ResponseBodyVO;

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
    
    public String getNaverApiDate(String item) {
    	
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Naver-Client-Id", naverClientKey);
        headers.add("X-Naver-Client-Secret", naverClientSecretKey);
        
    	ResponseEntity<String> responseEntity = restTemplate.exchange(
    			naverApiUrl + "?query=" + item + "&display=6&start=1&sort=sim",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
    	
    	// 네이버 응답 값
    	return responseEntity.getBody();
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
	
}
