package com.deltasac.deltanet.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deltasac.deltanet.models.entity.Persona;
import com.deltasac.deltanet.models.entity.Solicitud;
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
	
	@Secured({"ROLE_ADMIN"})
	@GetMapping("/usuarios/all")
	public List<Persona> buscarUsuarios() {
		return personaService.findAll();
	}
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/usuarios/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			personaService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la solicitud en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		
	}
}
