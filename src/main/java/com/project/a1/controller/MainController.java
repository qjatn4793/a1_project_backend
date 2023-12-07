package com.project.a1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

// api 통신
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@Value("${naver.client.key}")
    private String naverClientKey;

    @Value("${naver.client.secret.key}")
    private String naverClientSecretKey;
    
    @Value("${naver.api.url}")
    private String naverApiUrl;
	
	@GetMapping("/search")
	public String main() {
		
		logger.info("search");
		
		return "[\"농협\", \"SKB\", \"KT\", \"LG\", \"KB증권\"]";
	}
	
	@GetMapping("/searchItem")
    public String searchItem(@RequestParam String item) {
		
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
    	
    	// 네이버 응답 값
    	String result = responseEntity.getBody();
    	logger.info("naver : {}", result);
    	
        return "results : " + item;
    }
	
}
