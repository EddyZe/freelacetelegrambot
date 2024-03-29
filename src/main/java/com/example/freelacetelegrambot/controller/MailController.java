package com.example.freelacetelegrambot.controller;


import com.example.freelacetelegrambot.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/mail")
@RestController
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("${service.activation.url}")
    public ResponseEntity<HttpStatus> activation(@PathVariable("id") long id) {
        mailService.activation(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
