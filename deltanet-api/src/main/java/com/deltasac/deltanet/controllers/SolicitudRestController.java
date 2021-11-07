package com.deltasac.deltanet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deltasac.deltanet.models.entity.Solicitud;
import com.deltasac.deltanet.models.service.ISolicitudService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/apiSolic")
public class SolicitudRestController {
	
	@Autowired
	private ISolicitudService solicitudService;
	
	@GetMapping("/solicitudes")
	public List<Solicitud> index(){
		return solicitudService.findAll();
	}

}
