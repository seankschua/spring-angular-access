package com.exp.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  
  // The user's email
  @NotNull
  private String email;
  
  // The user's name
  @NotNull
  private String name;
  
  @NotNull
  //The user's ps
  private String password;
  
  public User() { }

  public User(long id) { 
    this.id = id;
  }
  
  public User(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
  }

  // Getter and setter methods

  public long getId() {
    return id;
  }

  public void setId(long value) {
    this.id = value;
  }

  public String getEmail() {
    return email;
  }
  
  public void setEmail(String value) {
    this.email = value;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }
  
} // class User
