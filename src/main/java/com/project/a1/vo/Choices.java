package com.project.a1.vo;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Choices {

  private int index;
  private Map<String, Object> message;
  private String finish_reason;
  
}
