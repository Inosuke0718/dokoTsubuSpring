package com.example.demo.model;

import org.springframework.stereotype.Service;

@Service
public class LoginService {
  public boolean execute(User user) {
    if (user.getPass().equals("1234")) { return true; }
    return false;
  }
}