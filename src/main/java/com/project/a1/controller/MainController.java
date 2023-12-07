package com.project.a1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
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
		
		/* 임시로 주석 처리 */
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
    	
    	// 네이버 응답 값
    	String result = responseEntity.getBody();
    	logger.info("naver : {}", result);
    	*/
    	
        return "results : " + item;
    }
	
	@PostMapping("/searchItem")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        // 받은 파일의 정보 출력 (실제로는 여기에서 파일을 처리해야 합니다.)
        System.out.println("Received file: " + file.getOriginalFilename());

        // 클라이언트에 응답
        return "파일 업로드가 성공했습니다.";
    }
	
}
