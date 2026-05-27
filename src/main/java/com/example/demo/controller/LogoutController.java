package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController  {
  @GetMapping("/Logout")
  public String logout(HttpSession session) {
    // セッションスコープを破棄
    session.invalidate();
    // ログアウト画面にフォワード   
    return "logout";
  }
}