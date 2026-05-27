package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.model.LoginService;
import com.example.demo.model.User;

@Controller
public class LoginController {
  private final LoginService loginService;

  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  @PostMapping("/Login")
  public String login(@RequestParam String name, @RequestParam String pass, HttpSession session) {
	// Userインスタンス（ユーザー情報）の生成
	User user = new User(name, pass);
	// ログイン処理
	boolean isLogin = loginService.execute(user);

	// ログイン成功時の処理
	if (isLogin) {
	  // ユーザー情報をセッションスコープに保存
	  session.setAttribute("loginUser", user);
	}
	// ログイン結果画面にフォワード
	return "loginResult"; 
  }
 }
