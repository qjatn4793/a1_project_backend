package com.project.a1.vo;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBodyVO {

	private String model;
	private List<Map<String, Object>> messages;
	private int max_tokens;
	private float temperature;
	private float top_p;
	private float frequency_penalty;
	private float presence_penalty;
	private List<String> stop;
	
}