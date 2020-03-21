package de.wirvsvirus.hack.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.wirvsvirus.hack.rest.model.ProviderModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/providers")
public class ProviderController {

	@ApiOperation(value = "Ping")
	@RequestMapping(method = RequestMethod.GET, value = "/ping", produces = "text/plain")
	public String ping() {
		return "provider endpoint alive";
	}

	@ApiOperation(value = "Get Provider")
	@RequestMapping(method = RequestMethod.GET, value = "/get", produces = "application/json")
	public ProviderModel getProvider(@RequestParam final String providerId) {
		return ProviderModel.builder().build();
	}

}
