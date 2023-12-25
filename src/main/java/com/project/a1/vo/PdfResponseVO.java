package com.project.a1.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfResponseVO {

	private String company;
	private List<Keyword> Keywords;
	private String summary;
	private String evaluationStandard;
	private String fileName;
	
}
