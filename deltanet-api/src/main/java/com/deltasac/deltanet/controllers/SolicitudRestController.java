package com.deltasac.deltanet.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deltasac.deltanet.models.entity.Solicitud;
import com.deltasac.deltanet.models.service.ISolicitudService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/apiSolic")
public class SolicitudRestController {
	
	@Autowired
	private ISolicitudService solicitudService;
	
	//---@Secured({"ROLE_ADMIN"})
	@GetMapping("/solicitudes")
	public List<Solicitud> index(){
		return solicitudService.findAll();
	}
	
	@GetMapping("/solicitudes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Solicitud solicitud = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			solicitud = solicitudService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(solicitud==null) {
			response.put("mensaje", "La solicitud ID: ".concat(id.toString()
					.concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@PostMapping("/solicitudes")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Solicitud solicitud, BindingResult result) {
		Solicitud solicitudNew = null;
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
			solicitudNew = solicitudService.save(solicitud);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido creada con éxito!");
		response.put("solicitud",solicitudNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/solicitudes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Solicitud solicitud, BindingResult result, @PathVariable Long id) {
		Solicitud solicitudActual = solicitudService.findById(id);
		Solicitud solicitudUpdated = null;
		Map<String,Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		if(solicitudActual==null) {
			response.put("mensaje", "Error: no se puede editar, la solicitud ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			solicitudActual.setTitulo(solicitud.getTitulo());
			solicitudActual.setDesTitulo(solicitud.getDesTitulo());
			solicitudActual.setEstado(solicitud.getEstado());
			
			solicitudUpdated = solicitudService.save(solicitudActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la solicitud en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido actualizada con éxito!");
		response.put("solicitud", solicitudUpdated);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/solicitudes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			solicitudService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la solicitud en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		
	}
	
	@GetMapping("/solicitudes/page/{page}")
	public Page<Solicitud> index(@PathVariable Integer page){
		Pageable pageable = PageRequest.of(page, 10);
		return solicitudService.findAll(pageable);
	}

}
