package we.employ.you.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import we.employ.you.service.InitializerService;

@RestController
public class InitializerController {

	private InitializerService service;
	
	@Autowired
	public InitializerController(InitializerService service) {
		this.service = service;
	}
	
	@RequestMapping(value = "/initialize")
	public String initialize() {
		return service.initialize();
	}
}
