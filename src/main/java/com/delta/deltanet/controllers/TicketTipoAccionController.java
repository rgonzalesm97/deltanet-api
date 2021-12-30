package com.delta.deltanet.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delta.deltanet.models.entity.TipoAccion;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketTipoAccionController {

	@Autowired
	private ITipoAccionService tipoAccionService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
		
	//TIPO ACCION
	@PostMapping("/tipoAccion/create")
	public ResponseEntity<?> CreateTipoAccion(@RequestParam("nombreTipoAccion") String nombreTipoAccion, @RequestParam("usuario") String usuarioCreacion){
		TipoAccion tipoAccion = new TipoAccion();
		tipoAccion.setNombre(nombreTipoAccion);
		tipoAccion.setUsuCreado(usuarioCreacion);
		tipoAccion.setFechaCreado(new Date());
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			tipoAccionService.save(tipoAccion);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El tipo acción ha sido creado con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/tipoAccion/read/{id}")
	public ResponseEntity<?> ReadTipoAccion(@PathVariable Long id) {
		TipoAccion tipoAccion= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			tipoAccion = tipoAccionService.findById(id);
			
			if(tipoAccion==null) {
				response.put("mensaje", "El tipo acción ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			if(tipoAccion.getEstadoRegistro()=='B') {
				response.put("mensaje", "El tipo acción ID: ".concat(id.toString()
						.concat(" ha sido eliminado")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<TipoAccion>(tipoAccion,HttpStatus.OK);
	}
	
	@GetMapping("/tipoAccion/read")
	public ResponseEntity<?> ReadAllTipoAccion() {
		List<TipoAccion> tipoAccions = tipoAccionService.findAll();
		
		return new ResponseEntity<List<TipoAccion>>(tipoAccions,HttpStatus.OK);
	}
	
	@PutMapping("/tipoAccion/update/{id}")
	public ResponseEntity<?> UpdateTipoAccion(@PathVariable Long id, @RequestParam("nombreTipoAccion") String nombreTipoAccion, @RequestParam("usuario") String usuarioActualizacion) {
		TipoAccion tipoAccionActual = tipoAccionService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(tipoAccionActual==null) {
			response.put("mensaje", "Error: no se puede editar, el tipo acción ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			tipoAccionActual.setNombre(nombreTipoAccion);
			tipoAccionActual.setFechaEditado(new Date());
			tipoAccionActual.setUsuEditado(usuarioActualizacion);
			
			tipoAccionService.save(tipoAccionActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el tipo acción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El tipo acción ha sido actualizado con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/tipoAccion/delete/{id}")
	public ResponseEntity<?> DeleteTipoAccion(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		TipoAccion tipoAccionActual = tipoAccionService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(tipoAccionActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, el tipo acción ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		tipoAccionActual.setEstadoRegistro('B');
		tipoAccionActual.setFechaEditado(new Date());
		tipoAccionActual.setUsuEditado(usuarioActualizacion);
		
		try {
			tipoAccionService.save(tipoAccionActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el tipo acción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "el tipo acción ha sido eliminado con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
}