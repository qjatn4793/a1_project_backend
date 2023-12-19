package com.project.a1.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public String extractKeyword(MultipartFile file) throws IOException {
    	int chunkSize = 2000;
		
		File source = new File(file.getOriginalFilename());
		source.createNewFile();
	    FileOutputStream fos = new FileOutputStream(source);
	    fos.write(file.getBytes());
	    fos.close();

		PDDocument pdfDoc = PDDocument.load(source);
		String text = new PDFTextStripper().getText(pdfDoc);
		
		List<String> results = splitText(text, chunkSize);
		log.info("length: {}", results.size());
		log.info("text: {}", results.get(0));
		
		text = "성공";

		return text;
    }
    
    public static List<String> splitText(String inputText, int chunkSize) {
        int length = inputText.length();
        int numOfChunks = (int) Math.ceil((double) length / chunkSize);
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < numOfChunks; i++) {
            int startIndex = i * chunkSize;
            int endIndex = Math.min((i + 1) * chunkSize, length);
            chunks.add(inputText.substring(startIndex, endIndex));
        }

        return chunks;
    }
	
}
