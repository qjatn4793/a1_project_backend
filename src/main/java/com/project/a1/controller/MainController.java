package com.project.a1.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.project.a1.response.ApiResponse;
import com.project.a1.service.MainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;

// api 통신
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
@RequiredArgsConstructor
public class MainController {

	private final MainService mainService;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@Value("${naver.client.key}")
    private String naverClientKey;

    @Value("${naver.client.secret.key}")
    private String naverClientSecretKey;
    
    @Value("${naver.api.url}")
    private String naverApiUrl;
	
	@GetMapping("/search")
	public String main() {
		
		log.info("search");
		
		return "[\"농협\","
				+ " \"SKB\","
				+ " \"KT\","
				+ " \"LG\","
				+ " \"KB증권\","
				+ " \"Kakao\","
				+ " \"카카오\","
				+ " \"Naver\","
				+ " \"네이버\","
				+ " \"Samsung\","
				+ " \"삼성\","
				+ " \"Samsung SDS\","
				+ " \"삼성 SDS\","
				+ " \"SK Telecom\","
				+ " \"LG 일렉트로닉스\","
				+ " \"쿠팡\","
				+ " \"우아한 형제들\","
				+ " \"라인\","
				+ " \"왓챠\","
				+ " \"직방\","
				+ " \"야놀자\","
				+ " \"인포뱅크\","
				+ " \"Infobank\","
				+ " \"잔디\","
				+ " \"배달의민족\","
				+ " \"토스\","
				+ " \"마켓컬리\","
				+ " \"지그재그\","
				+ " \"KIA\","
				+ " \"기아\","
				+ " \"현대\","
				+ " \"Hyundai\","
				+ " \"LG CNS\","
				+ " \"야나두\"]";
	}
	
	@GetMapping("/searchItem")
    public String searchItem(@RequestParam String item) {
		
        return item;
    }
	
	@PostMapping("/searchItem")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        // 받은 파일의 정보 출력 (실제로는 여기에서 파일을 처리해야 합니다.)
		log.info("Received file: {}", file.getOriginalFilename());

        // 클라이언트에 응답
        return "파일 업로드를 성공했습니다.";
    }
	
	@GetMapping("/searchResult")
    public String searchResult(@RequestParam String item) {
		
		/* 임시로 주석 처리 */
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
    	log.info("naver : {}", result);
    	
        return item;
    }
	
	@GetMapping("/getGptAnswer")
	public ApiResponse<String> getGptAnswer(@RequestParam String content) {
		
		return ApiResponse.success(mainService.getGPTAnswer(content));
	}
	
}
