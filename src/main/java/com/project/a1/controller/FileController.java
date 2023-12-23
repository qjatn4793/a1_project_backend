package com.project.a1.controller;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.a1.response.ApiResponse;
import com.project.a1.service.MainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*")
@Validated
@RequiredArgsConstructor
public class FileController {
	
	private final MainService mainService;
	
	@PostMapping("/uploadFile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {

        // 받은 파일의 정보 출력 (실제로는 여기에서 파일을 처리해야 합니다.)
		log.info("Received file: {}", file.getOriginalFilename());
		
		ApiResponse.success(mainService.analyzePDF(file));

        // 클라이언트에 응답
        return file.getOriginalFilename();
    }
	
	@GetMapping("/downloadFile")
	public ResponseEntity<InputStreamResource> handleFileDownload() {
	    try {
	        log.info("파일 다운로드 시작");

	        XMLSlideShow ppt = new XMLSlideShow();

	        // 시작 슬라이드에 텍스트 추가
	        XSLFSlide startSlide = ppt.createSlide();
	        XSLFTextShape titleStart = startSlide.createTextBox();
	        titleStart.setAnchor(new java.awt.Rectangle(100, 100, 450, 50));
	        titleStart.setText("시작 슬라이드: 여기에 어떤 내용이든 추가할 수 있습니다.");

	        // 중간 슬라이드에 이미지 추가 (이미지 파일 경로는 수정해야 합니다)
	        XSLFSlide middleSlide = ppt.createSlide();
	        XSLFTextShape titleMiddle = middleSlide.createTextBox();
	        titleMiddle.setAnchor(new java.awt.Rectangle(100, 100, 450, 50));
	        titleMiddle.setText("중간 슬라이드: 여기에 어떤 내용이든 추가할 수 있습니다.");

	        // 끝 슬라이드에 리스트 추가
	        XSLFSlide endSlide = ppt.createSlide();
	        XSLFTextShape titleEnd = endSlide.createTextBox();
	        titleEnd.setAnchor(new java.awt.Rectangle(100, 100, 450, 50));
	        XSLFTextParagraph paragraph = titleEnd.addNewTextParagraph();
	        paragraph.addNewTextRun().setText("끝 슬라이드:\n1. 첫 번째 항목\n2. 두 번째 항목\n3. 세 번째 항목");

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        ppt.write(outputStream);
	        byte[] pptxBytes = outputStream.toByteArray();

	        log.info("생성된 PPTX 파일 크기: " + pptxBytes.length + " bytes");

	        InputStream inputStream = new ByteArrayInputStream(pptxBytes);

	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=presentation.pptx");
	        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.presentationml.presentation");

	        return ResponseEntity.ok()
	                .headers(headers)
	                .contentLength(pptxBytes.length)
	                .body(new InputStreamResource(inputStream));
	    } catch (Exception e) {
	        log.error("파일 다운로드 중 오류 발생: ", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

}
