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

import com.google.gson.Gson;
import com.project.a1.response.ApiResponse;
import com.project.a1.service.MainService;
import com.project.a1.vo.SearchResultVO;

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
@CrossOrigin(origins = "*")
@Validated
@RequiredArgsConstructor
public class MainController {

	private final MainService mainService;
	
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
    public ApiResponse<Map<String, Object>> handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {

        // 받은 파일의 정보 출력 (실제로는 여기에서 파일을 처리해야 합니다.)
		log.info("Received file: {}", file.getOriginalFilename());

        // 클라이언트에 응답
        return ApiResponse.success(mainService.analyzePDF(file));
    }
	
	@GetMapping("/searchResult")
    public SearchResultVO searchResult(@RequestParam String item) {
		
		// naver api 호출
		String jsonData = mainService.getNaverApiDate(item);
		
		// Gson 선언
		Gson gson = new Gson();
		SearchResultVO searchResultVO = gson.fromJson(jsonData, SearchResultVO.class);
		
		log.info("searchResultVO: {}", gson.toJson(searchResultVO));
		
        return searchResultVO;
    }
	
	@GetMapping("/getGptAnswer")
	public ApiResponse<String> getGptAnswer(@RequestParam String content) {
		
		return ApiResponse.success(mainService.getGPTAnswer(content));
	}
	
}
