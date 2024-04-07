package com.example.freelacetelegrambot.controller;


import com.example.freelacetelegrambot.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mail")
@RestController
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/activation/{id}")
    public ResponseEntity<HttpStatus> activation(@PathVariable("id") long id) {
        System.out.println(id);
        mailService.activation(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/")
    public String getPage() {
        return "Привет";
    }
}
