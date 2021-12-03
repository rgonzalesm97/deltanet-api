package com.deltasac.deltanet.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.Persona;
import com.deltasac.deltanet.models.entity.Role;
import com.deltasac.deltanet.models.entity.Solicitud;
import com.deltasac.deltanet.models.service.IPersonaService;

@CrossOrigin(origins= {"http://localhost:4200","http://173.255.202.95:8080"})
@RestController
@RequestMapping("/apiUser")
public class UsuarioRestController {
	
	@Autowired
	private IPersonaService personaService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/usuarios/buscaid")
	public Optional<Persona> buscarId(@RequestParam("id") Long id) {
		System.out.println(personaService.findById(id));
		return personaService.findById(id);
	}
	
	@Secured({"ROLE_ADMIN"})
	@GetMapping("/usuarios/all")
	public List<Persona> buscarUsuarios() {
		return personaService.findAll();
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/usuarios")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Persona persona, BindingResult result) {
		Persona personaNew = null;
		System.out.println(persona.toString());
		Map<String, Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errores", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		try {
			String passwordBCrypt = passwordEncoder.encode(persona.getPassword());
			persona.setPassword(passwordBCrypt);
			personaNew = personaService.save(persona);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido creada con éxito!");
		response.put("persona",personaNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_BOSS","ROLE_USER"})
	@PutMapping("/usuarios/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Persona persona, BindingResult result, @PathVariable Long id) {
		Optional<Persona> personaActual = personaService.findById(id);
		Persona personaUpdated = null;
		Map<String,Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		if(personaActual==null) {
			response.put("mensaje", "Error: no se puede editar, la solicitud ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			personaActual.get().setNomper(persona.getNomper());
			personaActual.get().setApeper(persona.getApeper());
			personaActual.get().setEmail(persona.getEmail());
			personaActual.get().setUsername(persona.getUsername());
			personaActual.get().setNrodoc(persona.getNrodoc());
			personaActual.get().setRoles(persona.getRoles());
			
			personaUpdated = personaService.save(personaActual.get());
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el usuario en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El usuario ha sido actualizado con éxito!");
		response.put("usuario", personaUpdated);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_BOSS","ROLE_USER"})
	@PutMapping("/usuarios/cambioContra")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> updatePassword(@Valid @RequestBody Persona persona, BindingResult result) {
		Persona personaUpdated = null;
		Map<String,Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		if(persona==null) {
			response.put("mensaje", "Error: no se puede editar, el usuario"
					.concat(" no existe en la base de datos"));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			String passwordBCrypt = passwordEncoder.encode(persona.getPassword());
			persona.setPassword(passwordBCrypt);
			
			personaUpdated = personaService.save(persona);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el usuario en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El usuario ha sido actualizado con éxito!");
		response.put("usuario", personaUpdated);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
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
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/usuarios/roles")
	public List<Role> listarRoles(){
		return personaService.findAllRoles();
	}
}
