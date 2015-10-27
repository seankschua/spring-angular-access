package com.exp.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.exp.models.ResponseData;
import com.exp.models.SubGeneric;
import com.exp.models.Universe;
import com.exp.models.User;
import com.exp.models.UserDao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class UserController {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private Universe universe;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

  @RequestMapping("/create")
  @ResponseBody
  public ResponseData create(String email, String name, String password) {
    User user = null;
    try {
      user = new User(email, name, password);
      userDao.save(user);
    }
    catch (Exception ex) {
    	return new ResponseData(false, Arrays.asList("Error with user creation."));
    }
    return new ResponseData(true, Arrays.asList(user));
  }
  
  @RequestMapping(value="/createPOST", method = RequestMethod.POST)
  @ResponseBody
  public ResponseData createPOST(@RequestBody User user) {
	  ArrayList<String> errors = new ArrayList<String>();
	  ArrayList<Object> errorObj = null;
    try {
    	if(userDao.countByEmail(user.getEmail())>0){
    		errors.add("user email " + user.getEmail() + " already exists.");
    	}
    	if(userDao.countByName(user.getName())>0){
    		errors.add("user name " + user.getName() + " already exists.");
    	}
    	if(!errors.isEmpty()){
    		errorObj = new ArrayList<Object>(errors);
    		throw new Exception();
    	}
      userDao.save(user);
    }
    catch (Exception ex) {
    	log.error("/createPOST: " + ex.getMessage() + "~" + universe.getGson().toJson(user));
    	return new ResponseData(false, errorObj);
    }
    log.info("/createPOST: " + universe.getGson().toJson(user));
    return new ResponseData(true, Arrays.asList(user));
  }
  
  @RequestMapping(value="/loginPOST", method = RequestMethod.POST)
  @ResponseBody
  public ResponseData loginPOST(@RequestBody User user) {
	  ArrayList<String> errors = new ArrayList<String>();
	  ArrayList<Object> errorObj = null;
	  String jwt = "";
    try {
    	
    	User userDB = userDao.findByEmail(user.getEmail());
    	if(userDB==null){
    		errors.add("User does not exist.");
    	}
    	if(!errors.isEmpty()){
    		errorObj = new ArrayList<Object>(errors);
    		throw new Exception();
    	}
    	if(!userDB.getPassword().contentEquals(user.getPassword())){
    		errors.add("Password does not match.");
    	}
    	if(!errors.isEmpty()){
    		errorObj = new ArrayList<Object>(errors);
    		throw new Exception();
    	}
    	//log.info(universe.getGson().toJson(userDB));
    	//log.info("/loginPOST key: " + Base64.getEncoder().encodeToString(universe.getKey().getEncoded()));
    	
    	HashMap<String, Object> claims = new HashMap<String, Object>();
    	claims.put("issuer", "expedia.com");
    	claims.put("sub", userDB.getId());
    	claims.put("iat", new DateTime().getMillis());
    	claims.put("name", userDB.getName());
    	claims.put("role", userDB.getRole());
    	
    	jwt = Jwts.builder().setClaims(claims)
    			.signWith(SignatureAlgorithm.HS512, universe.getKey())
    			.compact();
    }
    catch (Exception ex) {
    	log.error("/loginPOST: " + ex.getMessage() + "~" + universe.getGson().toJson(user));
    	return new ResponseData(false, errorObj);
    }
    log.info("/loginPOST: " + universe.getGson().toJson(user));
    return new ResponseData(true, Arrays.asList(jwt));
  }
  
  @RequestMapping(value="/checkLoginPOST", method = RequestMethod.POST)
  @ResponseBody
  public ResponseData checkLoginPOST(@RequestBody SubGeneric sub) {
	  ArrayList<String> errors = new ArrayList<String>();
	  ArrayList<Object> errorObj = null;
	  Claims claims = null;
    try {
    	claims = Jwts.parser().setSigningKey(universe.getKey()).parseClaimsJws(sub.getInput()).getBody();
    	//log.info("/checkLoginPOST: issuer~" + claims.get("issuer"));
    }
    catch (Exception ex) {
    	log.error("/checkLoginPOST: " + ex.getMessage() + "~" + universe.getGson().toJson(sub));
    	return new ResponseData(false, errorObj);
    }
    log.info("/checkLoginPOST: " + universe.getGson().toJson(claims));
    return new ResponseData(true, Arrays.asList(claims.get("name") + " login succeeded."));
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
