package com.SrivatsanPoddar.helpp;

import retrofit.http.GET;

public interface HerokuService
{
	@GET("/")  //The endpoint should be '/nodes' -ppod
	Node[] nodes();
	
	
}