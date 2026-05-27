package com.example.demo.model;

import org.springframework.stereotype.Service;
import com.example.demo.dao.MuttersDAO;

@Service
public class PostMutterService {
  public void execute(Mutter mutter) { 
    MuttersDAO dao = new MuttersDAO();
    dao.create(mutter);
  }
}