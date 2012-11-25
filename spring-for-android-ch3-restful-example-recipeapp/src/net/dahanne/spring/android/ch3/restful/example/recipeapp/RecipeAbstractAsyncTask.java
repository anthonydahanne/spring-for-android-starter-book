package net.dahanne.spring.android.ch3.restful.example.recipeapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import android.os.AsyncTask;

public abstract class RecipeAbstractAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	
	private static final String USERNAME = "s4a";
	private static final String PASSWORD = "s4a";
	
	protected RestClientException exception;
	
	protected HttpHeaders prepareHeadersWithMediaTypeAndBasicAuthentication() {
		HttpHeaders requestHeaders = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		requestHeaders.setAccept(acceptableMediaTypes);
		HttpAuthentication authHeader = new HttpBasicAuthentication(USERNAME, PASSWORD);
		requestHeaders.setAuthorization(authHeader);
		return requestHeaders;
	}
	
}
