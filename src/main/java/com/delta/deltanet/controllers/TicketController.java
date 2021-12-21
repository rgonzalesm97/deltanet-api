package com.delta.deltanet.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.delta.deltanet.models.entity.Prioridad;
import com.delta.deltanet.models.service.IPrioridadService;

@RestController
@RequestMapping("/ticket")
public class TicketController {

	@Autowired
	private IPrioridadService prioridadService;
	
	
	//PRIORIDAD
	@PostMapping("/prioridad/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> CreatePrioridad(@RequestParam("nombrePrioridad") String nombrePrioridad, @RequestParam("usuario") String usuarioCreacion){
		Prioridad prioridad = new Prioridad();
		prioridad.setNombre(nombrePrioridad);
		prioridad.setUsuCreado(usuarioCreacion);
		prioridad.setFechaCreado(new Date());
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			prioridadService.save(prioridad);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La prioridad ha sido creada con éxito!");
		//response.put("prioridad", prioridadNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/prioridad/read/{id}")
	public ResponseEntity<?> ReadPrioridad(@PathVariable Long id) {
		Prioridad prioridad= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			prioridad = prioridadService.findById(id);
			
			if(prioridad.getId()==null) {
				response.put("mensaje", "La solicitud ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Prioridad>(prioridad,HttpStatus.OK);
	}
	
	@GetMapping("/prioridad/read")
	public ResponseEntity<?> ReadAllPrioridad() {
		List<Prioridad> prioridades = prioridadService.findAll();
		
		return new ResponseEntity<List<Prioridad>>(prioridades,HttpStatus.OK);
	}
	
	@PutMapping("/prioridad/update/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> UpdatePrioridad(@PathVariable Long id, @RequestParam("nombrePrioridad") String nombrePrioridad, @RequestParam("usuario") String usuarioActualizacion) {
		Prioridad prioridadActual = prioridadService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(prioridadActual==null) {
			response.put("mensaje", "Error: no se puede editar, la prioridad ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			prioridadActual.setNombre(nombrePrioridad);
			prioridadActual.setFechaEditado(new Date());
			prioridadActual.setUsuEditado(usuarioActualizacion);
			
			prioridadService.save(prioridadActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la prioridad en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La prioridad ha sido actualizada con éxito!");
		//response.put("prioridad", prioridadUpdated);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/prioridad/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Map<String, Object> response = new HashMap<>();
		try {
			prioridadService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la prioridad en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La prioridad ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
}
