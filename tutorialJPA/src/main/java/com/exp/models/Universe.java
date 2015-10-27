package com.exp.models;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import io.jsonwebtoken.impl.crypto.MacProvider;

@Component
public class Universe {
	
	private SecretKey key;
	private String test;
	
	private Gson gson;
	
	public Universe(){
		this.key = MacProvider.generateKey();
		this.test = "test works";
		this.gson = new Gson();
	}

	public SecretKey getKey() {
		return key;
	}

	public void setKey(SecretKey key) {
		this.key = key;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	
	
}
