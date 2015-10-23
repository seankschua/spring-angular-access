package com.exp.controllers;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.exp.models.ResponseData;
import com.exp.models.User;
import com.exp.models.UserDao;

@RestController
public class UserController {
	
	@Autowired
	private UserDao userDao;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

  @RequestMapping("/create")
  @ResponseBody
  public ResponseData create(String email, String name) {
    User user = null;
    try {
      user = new User(email, name);
      userDao.save(user);
    }
    catch (Exception ex) {
    	return new ResponseData(false, Arrays.asList("Error with user creation."));
    }
    return new ResponseData(true, Arrays.asList(user));
  }
  
  @RequestMapping("/delete")
  @ResponseBody
  public ResponseData delete(long id) {
	  User user;
    try {
      user = new User(id);
      userDao.delete(user);
    }
    catch (Exception ex) {
    	return new ResponseData(false, Arrays.asList("User not found."));
    }
    return new ResponseData(true, Arrays.asList("User " + user.getId() + " deleted."));
  }
  
  @RequestMapping("/get-by-email")
  @ResponseBody
  public ResponseData getByEmail(String email) {
    User user;
    try {
      user = userDao.findByEmail(email);
      log.info("/get-by-email: " + email);
    }
    catch (Exception ex) {
    	log.error("/get-by-email: " + email);
      return new ResponseData(false, Arrays.asList("User not found"));
    }
    return new ResponseData(true, Arrays.asList(user));
  }
  
  @RequestMapping("/update")
  @ResponseBody
  public ResponseData updateUser(long id, String email, String name) {
	  User user;
    try {
      user = userDao.findOne(id);
      user.setEmail(email);
      user.setName(name);
      userDao.save(user);
    }
    catch (Exception ex) {
    	return new ResponseData(false, Arrays.asList("User not found"));
    }
    return new ResponseData(true, Arrays.asList(user));
  }

  
  
} // class UserController
