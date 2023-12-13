package com.project.a1.vo;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBodyVO {

  private String id;
  private String object;
  private int created;
  private String model;
  private List<Choices> choices; 
  private Map<String, Object> usage;
  private String system_fingerprint;
  
}
