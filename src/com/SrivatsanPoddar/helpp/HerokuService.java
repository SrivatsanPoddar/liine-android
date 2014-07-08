package com.SrivatsanPoddar.helpp;

import retrofit.http.GET;

public interface HerokuService
{
	@GET("/")
	Node[] nodes();
}