package com.example.demo.model;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.dao.MuttersDAO;

@Service
public class GetMutterListService {
  public List<Mutter> execute() {
    MuttersDAO dao = new MuttersDAO();
    List<Mutter> mutterList = dao.findAll();
    return mutterList;
  }
}