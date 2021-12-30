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

import com.delta.deltanet.models.entity.Estado;
import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.service.IEstadoService;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketEstadoController {

	@Autowired
	private IEstadoService estadoService;
	@Autowired
	private ITipoAccionService tipoAccionService;
	@Autowired
	private IHistorialService historialService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
		
	//ESTADO
	@PostMapping("/estado/create")
	public ResponseEntity<?> CreateEstado(@RequestParam("nombreEstado") String nombreEstado, @RequestParam("usuario") String usuarioCreacion){
		Estado estado = new Estado();
		estado.setNombre(nombreEstado);
		estado.setUsuCreado(usuarioCreacion);
		estado.setFechaCreado(new Date());
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("ESTADO"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuarioCreacion);
		historial.setFechaCreado(new Date());
		
		Estado estadoCreada = new Estado();
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			estadoCreada = estadoService.save(estado);
			
			try {
				
				historial.setTablaId(estadoCreada.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				estadoService.delete(estadoCreada.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El estado ha sido creada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/estado/read/{id}")
	public ResponseEntity<?> ReadEstado(@PathVariable Long id) {
		Estado estado= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			estado = estadoService.findById(id);
			
			if(estado==null) {
				response.put("mensaje", "El estado ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			if(estado.getEstadoRegistro()=='B') {
				response.put("mensaje", "El estado ID: ".concat(id.toString()
						.concat(" ha sido eliminado")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Estado>(estado,HttpStatus.OK);
	}
	
	@GetMapping("/estado/read")
	public ResponseEntity<?> ReadAllEstado() {
		List<Estado> estados = estadoService.findAll();
		
		return new ResponseEntity<List<Estado>>(estados,HttpStatus.OK);
	}
	
	@PutMapping("/estado/update/{id}")
	public ResponseEntity<?> UpdateEstado(@PathVariable Long id, @RequestParam("nombreEstado") String nombreEstado, @RequestParam("usuario") String usuarioActualizacion) {
		Estado estadoActual = estadoService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(estadoActual==null) {
			response.put("mensaje", "Error: no se puede editar, el estado ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("ESTADO"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Estado estadoBack = new Estado();
		estadoBack.setId(estadoActual.getId());
		estadoBack.setNombre(estadoActual.getNombre());
		estadoBack.setUsuCreado(estadoActual.getUsuCreado());
		estadoBack.setFechaCreado(estadoActual.getFechaCreado());
		estadoBack.setUsuEditado(estadoActual.getUsuEditado());
		estadoBack.setFechaEditado(estadoActual.getFechaEditado());
		estadoBack.setEstadoRegistro(estadoActual.getEstadoRegistro());
		
		try {
			estadoActual.setNombre(nombreEstado);
			estadoActual.setFechaEditado(new Date());
			estadoActual.setUsuEditado(usuarioActualizacion);
			
			estadoService.save(estadoActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				estadoService.save(estadoBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el estado en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El estado ha sido actualizada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/estado/delete/{id}")
	public ResponseEntity<?> DeleteEstado(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Estado estadoActual = estadoService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(estadoActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, el estado ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("ESTADO"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Estado estadoBack = new Estado();
		estadoBack.setId(estadoActual.getId());
		estadoBack.setNombre(estadoActual.getNombre());
		estadoBack.setUsuCreado(estadoActual.getUsuCreado());
		estadoBack.setFechaCreado(estadoActual.getFechaCreado());
		estadoBack.setUsuEditado(estadoActual.getUsuEditado());
		estadoBack.setFechaEditado(estadoActual.getFechaEditado());
		estadoBack.setEstadoRegistro(estadoActual.getEstadoRegistro());
		
		estadoActual.setEstadoRegistro('B');
		estadoActual.setFechaEditado(new Date());
		estadoActual.setUsuEditado(usuarioActualizacion);
		
		try {
			estadoService.save(estadoActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				estadoService.save(estadoBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el estado en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "el estado ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
}