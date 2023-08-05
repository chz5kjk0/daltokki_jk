package com.moon.daltokki.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moon.daltokki.Model.UserModel;
import com.moon.daltokki.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.*;

@Slf4j
@Component
public class UserService {

  // 1. repo > crud
  @Autowired
  private UserRepository userRepository;

  // 2. template > sql
  @Autowired
  MongoTemplate mongoTemplate;

  public String getPass(String user_id) { // id로 비밀번호 확인 (로그인)
    System.out.println(user_id);
    String user_pass = "";
    user_pass += userRepository.selectPass(user_id).getUser_pass();
    System.out.println(user_pass);
    return user_pass;
  }

  public void joinPro(UserModel user) { // 회원 가입
    // 토끼 유형 결정
    String[] rabbit_array = {"hj1", "je1", "jk1", "mj1", "yr1"};
    List<String> rabbit_list = Arrays.asList(rabbit_array);
    Collections.shuffle(rabbit_list);
    rabbit_list.toArray(rabbit_array);
    String rabbit = rabbit_array[0];
    user.setRabbit_type(rabbit); // 토끼 유형 세팅
    user.setSp_record(0); // 송편 개수 0
    user.setLogin_type("D"); // 기본 가입 유형 "D"
    userRepository.save(user);
  }

}
