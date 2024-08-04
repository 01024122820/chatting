package com.kjbank.chat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainController {
	
	 private static final Logger logger = LoggerFactory.getLogger(mainController.class);
	 
	    @GetMapping("/")
	    public String index() {
	    	 logger.info("Received index: {}", "");
	    	 
	        return "index"; // This will resolve to /WEB-INF/views/index.jsp
	    }

}
