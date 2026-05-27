package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.GetMutterListService;
import com.example.demo.model.Mutter;
import com.example.demo.model.PostMutterService;
import com.example.demo.model.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
  private final GetMutterListService getMutterListService;
  private final PostMutterService postMutterService;
  @GetMapping("/Main")
  public String showMain(HttpServletRequest request, HttpSession session) {
    // つぶやきリストを取得して、リクエストスコープに保存
    List<Mutter> mutterList = getMutterListService.execute();
    request.setAttribute("mutterList", mutterList);

    // ログインしているか確認するため
    // セッションスコープからユーザー情報を取得
    User loginUser = (User) session.getAttribute("loginUser");

    if (loginUser == null) { // ログインしていない
      // リダイレクト
      return "redirect:index.jsp";
    } else { // ログイン済み
      // フォワード
      return "main";
    }
  }
  @PostMapping("/Main")
  public String postMutter(@RequestParam String text, HttpServletRequest request, HttpSession session) {
    // 入力値チェック
    if (text != null && text.length() != 0) {
      // セッションスコープに保存されたユーザー情報を取得
      User loginUser = (User) session.getAttribute("loginUser");

      // つぶやきを作成してつぶやきリストに追加
      Mutter mutter = new Mutter(loginUser.getName(), text);
      postMutterService.execute(mutter);
    } else {
      // エラーメッセージをリクエストスコープに保存
      request.setAttribute("errorMsg", "つぶやきが入力されていません");
    }

    // つぶやきリストを取得して、リクエストスコープに保存
    List<Mutter> mutterList = getMutterListService.execute();
    request.setAttribute("mutterList", mutterList);

    // メイン画面にフォワード
    return "main";
  }
}