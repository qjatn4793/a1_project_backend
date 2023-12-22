package com.project.a1.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.a1.response.ApiResponse;
import com.project.a1.service.MainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/fileUpload")
@CrossOrigin(origins = "*")
@Validated
@RequiredArgsConstructor
public class FileUploadController {
	
	private final MainService mainService;
	
	@PostMapping("/searchItem")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {

        // 받은 파일의 정보 출력 (실제로는 여기에서 파일을 처리해야 합니다.)
		log.info("Received file: {}", file.getOriginalFilename());
		
		ApiResponse.success(mainService.analyzePDF(file));

        // 클라이언트에 응답
        return file.getOriginalFilename();
    }

}
