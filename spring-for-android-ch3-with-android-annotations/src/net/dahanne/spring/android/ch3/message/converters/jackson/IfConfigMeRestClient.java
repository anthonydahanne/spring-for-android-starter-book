package net.dahanne.spring.android.ch3.message.converters.jackson;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;

@Rest(rootUrl = "http://ifconfig.me", converters = { MappingJacksonHttpMessageConverter.class })
public interface IfConfigMeRestClient {

	@Get("/all.json")
	@Accept(MediaType.APPLICATION_JSON)
	IfConfigMeJson getAll();

}
