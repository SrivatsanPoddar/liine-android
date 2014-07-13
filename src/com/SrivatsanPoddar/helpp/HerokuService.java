package com.SrivatsanPoddar.helpp;

import retrofit.http.GET;

public interface HerokuService
{
	@GET("/nodes")
	Node[] nodes();
}