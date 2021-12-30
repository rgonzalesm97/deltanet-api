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

import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.entity.Prioridad;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.IPrioridadService;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketPrioridadController {

	@Autowired
	private IPrioridadService prioridadService;
	@Autowired
	private ITipoAccionService tipoAccionService;
	@Autowired
	private IHistorialService historialService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
	
	//PRIORIDAD
	@PostMapping("/prioridad/create")
	public ResponseEntity<?> CreatePrioridad(@RequestParam("nombrePrioridad") String nombrePrioridad, @RequestParam("usuario") String usuarioCreacion){		
		Prioridad prioridad = new Prioridad();
		prioridad.setNombre(nombrePrioridad);
		prioridad.setUsuCreado(usuarioCreacion);
		prioridad.setFechaCreado(new Date());
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("PRIORIDAD"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuarioCreacion);
		historial.setFechaCreado(new Date());
		
		Prioridad prioridadCreada = new Prioridad();
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			prioridadCreada = prioridadService.save(prioridad);
			
			try {
				
				historial.setTablaId(prioridadCreada.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				prioridadService.delete(prioridadCreada.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La prioridad ha sido creada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/prioridad/read/{id}")
	public ResponseEntity<?> ReadPrioridad(@PathVariable Long id) {
		Prioridad prioridad= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			prioridad = prioridadService.findById(id);
			
			if(prioridad==null) {
				response.put("mensaje", "La prioridad ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			if(prioridad.getEstadoRegistro()=='B') {
				response.put("mensaje", "La prioridad ID: ".concat(id.toString()
						.concat(" ha sido eliminada")));
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
	public ResponseEntity<?> UpdatePrioridad(@PathVariable Long id, @RequestParam("nombrePrioridad") String nombrePrioridad, @RequestParam("usuario") String usuarioActualizacion) {
		Prioridad prioridadActual = prioridadService.findById(id);
		
		Map<String,Object> response = new HashMap<>();
		
		if(prioridadActual==null) {
			response.put("mensaje", "Error: no se puede editar, la prioridad ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("PRIORIDAD"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Prioridad prioridadBack = new Prioridad();
		prioridadBack.setId(prioridadActual.getId());
		prioridadBack.setNombre(prioridadActual.getNombre());
		prioridadBack.setUsuCreado(prioridadActual.getUsuCreado());
		prioridadBack.setFechaCreado(prioridadActual.getFechaCreado());
		prioridadBack.setUsuEditado(prioridadActual.getUsuEditado());
		prioridadBack.setFechaEditado(prioridadActual.getFechaEditado());
		prioridadBack.setEstadoRegistro(prioridadActual.getEstadoRegistro());
		
		try {
			prioridadActual.setNombre(nombrePrioridad);
			prioridadActual.setFechaEditado(new Date());
			prioridadActual.setUsuEditado(usuarioActualizacion);
			
			prioridadService.save(prioridadActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				prioridadService.save(prioridadBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la prioridad en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La prioridad ha sido actualizada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/prioridad/delete/{id}")
	public ResponseEntity<?> DeletePrioridad(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Prioridad prioridadActual = prioridadService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(prioridadActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, la prioridad ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("PRIORIDAD"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Prioridad prioridadBack = new Prioridad();
		prioridadBack.setId(prioridadActual.getId());
		prioridadBack.setNombre(prioridadActual.getNombre());
		prioridadBack.setUsuCreado(prioridadActual.getUsuCreado());
		prioridadBack.setFechaCreado(prioridadActual.getFechaCreado());
		prioridadBack.setUsuEditado(prioridadActual.getUsuEditado());
		prioridadBack.setFechaEditado(prioridadActual.getFechaEditado());
		prioridadBack.setEstadoRegistro(prioridadActual.getEstadoRegistro());
		
		prioridadActual.setEstadoRegistro('B');
		prioridadActual.setFechaEditado(new Date());
		prioridadActual.setUsuEditado(usuarioActualizacion);
		
		try {
			prioridadService.save(prioridadActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				prioridadService.save(prioridadBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la prioridad en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La prioridad ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
}
