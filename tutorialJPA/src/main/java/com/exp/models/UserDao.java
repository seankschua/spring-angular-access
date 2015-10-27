package com.exp.models;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserDao extends CrudRepository<User, Long> {

  public User findByEmail(String email);
  
  public long countByName(String name);

  public long countByEmail(String name);

} // class UserDao
