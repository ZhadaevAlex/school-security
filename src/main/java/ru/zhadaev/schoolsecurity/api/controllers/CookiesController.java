package ru.zhadaev.schoolsecurity.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CookiesController {
    @GetMapping("/setCookies")
    public void setCookies(HttpServletResponse response) {
        Cookie cookie1 = new Cookie("some_Id", "123");
        Cookie cookie2 = new Cookie("name", "Sasha");
        cookie1.setMaxAge(30);
        cookie2.setMaxAge(60);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
    }

    @GetMapping("/getCookies")
    public void getCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }

    }

}
