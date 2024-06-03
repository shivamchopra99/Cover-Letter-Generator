package com.CoverLetterGenerator.Controller;

import Response.CoverLetterResponse;
import com.CoverLetterGenerator.Request.CoverLetterRequest;
import com.CoverLetterGenerator.Service.CoverLetterService;
import com.CoverLetterGenerator.Service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class CoverLetterController {

    @Autowired
    private CoverLetterService coverLetterService;


    @Autowired
    private RateLimiterService rateLimiterService;

    @PostMapping("/generate-cover-letter")
    public ResponseEntity<CoverLetterResponse> generateCoverLetter(@Valid @RequestBody CoverLetterRequest request) {
        String rateLimitKey = request.getEmail();

        if (!rateLimiterService.isAllowed(rateLimitKey)) {
            return ResponseEntity.status(429).body(new CoverLetterResponse("Rate limit exceeded, Please try again later."));
        }
        CoverLetterResponse response = coverLetterService.generateCoverLetter(request);
        return ResponseEntity.ok(response);
    }
}
