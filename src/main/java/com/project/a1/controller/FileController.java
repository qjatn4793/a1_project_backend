package com.project.a1.controller;

import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFBackground;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
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
import org.springframework.core.io.ClassPathResource;
import org.apache.poi.sl.usermodel.PictureData.PictureType;

import com.project.a1.response.ApiResponse;
import com.project.a1.service.MainService;
import com.project.a1.vo.PdfResponseVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*")
@Validated
@RequiredArgsConstructor
public class FileController {

	private final MainService mainService;

	@PostMapping("/uploadFile")
	public ApiResponse<PdfResponseVO> handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {

		// 받은 파일의 정보 출력 (실제로는 여기에서 파일을 처리해야 합니다.)
		log.info("Received file: {}", file.getOriginalFilename());

		// 클라이언트에 응답
		return ApiResponse.success(mainService.analyzePDF(file));
	}

	@GetMapping("/downloadFile")
    public ResponseEntity<InputStreamResource> handleFileDownload(@RequestParam("contents") String contents) {
        try {
            log.info("파일 다운로드 시작");
            log.info("Contents: {}", contents);

            // 템플릿 파일 로드
            ClassPathResource resource = new ClassPathResource("/template/template.pptx");
            FileInputStream fis = new FileInputStream(resource.getFile());
            XMLSlideShow pptTemplate = new XMLSlideShow(fis);
            log.info("템플릿 파일 로드 완료");

            // 새 슬라이드쇼 생성
            XMLSlideShow ppt = new XMLSlideShow();

            // 템플릿의 첫 번째 슬라이드에서 이미지 찾기
            XSLFSlide templateSlide = pptTemplate.getSlides().get(0);
            XSLFPictureData pictureData = null;
            for (XSLFShape shape : templateSlide.getShapes()) {
                if (shape instanceof XSLFPictureShape) {
                    XSLFPictureShape pictureShape = (XSLFPictureShape) shape;
                    pictureData = pictureShape.getPictureData();
                    break; // 첫 번째 이미지를 찾으면 반복 중단
                }
            }

            // 섹션별로 콘텐츠 처리하고 새 슬라이드에 이미지 추가
            String[] sections = contents.split("(?m)^\\d+\\.");
            for (String section : sections) {
                if (!section.trim().isEmpty()) {
                    String[] lines = section.trim().split("\n", 2);
                    String titleText = lines[0];
                    String subtitleText = lines.length > 1 ? lines[1] : "";

                    // 새 슬라이드 생성
                    XSLFSlideLayout slideLayout = pptTemplate.getSlideMasters().get(0).getLayout(SlideLayout.TITLE);
                    XSLFSlide newSlide = ppt.createSlide(slideLayout);

                    // 타이틀과 서브타이틀 설정
                    XSLFTextShape title = newSlide.getPlaceholder(0);
                    XSLFTextShape subtitle = newSlide.getPlaceholder(1);
                    if (title != null) {
                        // 기존 텍스트를 제거합니다.
                        title.clearText();
                        
                        // 새로운 텍스트 런을 생성하고 속성을 설정합니다.
                        XSLFTextParagraph titlePara = title.addNewTextParagraph();
                        XSLFTextRun titleRun = titlePara.addNewTextRun();
                        titleRun.setText(titleText);
                        titleRun.setFontSize(28.0);
                        titleRun.setFontFamily("Arial"); // 폰트 스타일을 "Arial"로 설정
                        titleRun.setBold(true); // 볼드체를 적용
                    }

                    if (subtitle != null) {
                        // 기존 텍스트를 제거합니다.
                        subtitle.clearText();
                        
                        // 새로운 텍스트 런을 생성하고 속성을 설정합니다.
                        XSLFTextParagraph subtitlePara = subtitle.addNewTextParagraph();
                        XSLFTextRun subtitleRun = subtitlePara.addNewTextRun();
                        subtitleRun.setText(subtitleText);
                        subtitleRun.setFontSize(16.0);
                        subtitleRun.setFontFamily("Arial"); // 폰트 스타일을 "Arial"로 설정
                        subtitleRun.setItalic(true); // 이탤릭체를 적용
                    }

                    // 이미지 추가
                    if (pictureData != null) {
                        // 이미지 데이터로부터 새로운 이미지 생성
                        byte[] pictureByteArray = pictureData.getData();
                        PictureType pictureType = pictureData.getType();
                        XSLFPictureData newPictureData = ppt.addPicture(pictureByteArray, pictureType);
                        
                        // 새로운 이미지 쉐이프 생성 및 크기 설정
                        XSLFPictureShape newPictureShape = newSlide.createPicture(newPictureData);
                        newPictureShape.setAnchor(new Rectangle(0, 0, 720, 130));
                    }
                }
            }

            // 슬라이드쇼를 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ppt.write(outputStream);
            byte[] pptxBytes = outputStream.toByteArray();

            // HTTP 응답 생성
            InputStream inputStream = new ByteArrayInputStream(pptxBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=presentation.pptx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.presentationml.presentation");

            return ResponseEntity.ok().headers(headers).contentLength(pptxBytes.length)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            log.error("파일 다운로드 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
