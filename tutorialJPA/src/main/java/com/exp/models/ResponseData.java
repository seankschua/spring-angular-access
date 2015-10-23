package com.exp.models;

import java.util.List;

public class ResponseData {

	private boolean success;
	private List<Object> data;
	
	public ResponseData(boolean success, List<Object> data) {
		this.success = success;
		this.data = data;
	}
	
	
	
}
