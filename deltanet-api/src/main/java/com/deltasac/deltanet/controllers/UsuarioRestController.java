package com.deltasac.deltanet.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deltasac.deltanet.models.entity.Persona;
import com.deltasac.deltanet.models.service.IPersonaService;

@CrossOrigin(origins= {"http://localhost:4200","http://173.255.202.95:8080"})
@RestController
@RequestMapping("/apiUser")
public class UsuarioRestController {
	
	@Autowired
	private IPersonaService personaService;
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/usuarios/buscaid")
	public Optional<Persona> buscarId(@RequestParam("id") Long id) {
		return personaService.findById(id);
	}

}
