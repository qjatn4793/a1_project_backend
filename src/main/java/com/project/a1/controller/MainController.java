package com.project.a1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@GetMapping("/search")
	public String main() {
		
		logger.info("search");
		
		return "[\"농협\", \"SKB\", \"KT\", \"LG\", \"KB증권\"]";
	}
	
	@GetMapping("/searchItem")
    public String searchItem(@RequestParam String item) {
		
		logger.info("search");
		
        String itemDetails = "Details for item: " + item;

        return itemDetails;
    }
	
}
