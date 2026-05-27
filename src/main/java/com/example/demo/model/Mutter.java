package com.example.demo.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mutter implements Serializable {
  private int id;          // ID
  private String userName; // ユーザー名
  private String text;     // つぶやき内容
  
  public Mutter(String userName, String text) {
    this.userName = userName;
    this.text = text;
  }
}