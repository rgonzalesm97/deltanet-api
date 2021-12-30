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

import com.delta.deltanet.models.entity.Area;
import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.service.IAreaService;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketAreaController {

	@Autowired
	private IAreaService areaService;
	@Autowired
	private ITipoAccionService tipoAccionService;
	@Autowired
	private IHistorialService historialService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
	
	//AREA
	@PostMapping("/area/create")
	public ResponseEntity<?> CreateArea(@RequestParam("nombreArea") String nombreArea, @RequestParam("usuario") String usuarioCreacion){
		Area area = new Area();
		area.setNombre(nombreArea);
		area.setUsuCreado(usuarioCreacion);
		area.setFechaCreado(new Date());
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("AREA"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuarioCreacion);
		historial.setFechaCreado(new Date());
		
		Area areaCreada = new Area();
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			areaCreada = areaService.save(area);
			
			try {
				
				historial.setTablaId(areaCreada.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				areaService.delete(areaCreada.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El área ha sido creada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/area/read/{id}")
	public ResponseEntity<?> ReadArea(@PathVariable Long id) {
		Area area= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			area = areaService.findById(id);
			
			if(area==null) {
				response.put("mensaje", "El área ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			if(area.getEstadoRegistro()=='B') {
				response.put("mensaje", "El área ID: ".concat(id.toString()
						.concat(" ha sido eliminada")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Area>(area,HttpStatus.OK);
	}
	
	@GetMapping("/area/read")
	public ResponseEntity<?> ReadAllArea() {
		List<Area> areas = areaService.findAll();
		
		return new ResponseEntity<List<Area>>(areas,HttpStatus.OK);
	}
	
	@PutMapping("/area/update/{id}")
	public ResponseEntity<?> UpdateArea(@PathVariable Long id, @RequestParam("nombreArea") String nombreArea, @RequestParam("usuario") String usuarioActualizacion) {
		Area areaActual = areaService.findById(id);
		
		Map<String,Object> response = new HashMap<>();
		
		if(areaActual==null) {
			response.put("mensaje", "Error: no se puede editar, el área ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("AREA"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Area areaBack = new Area();
		areaBack.setId(areaActual.getId());
		areaBack.setNombre(areaActual.getNombre());
		areaBack.setUsuCreado(areaActual.getUsuCreado());
		areaBack.setFechaCreado(areaActual.getFechaCreado());
		areaBack.setUsuEditado(areaActual.getUsuEditado());
		areaBack.setFechaEditado(areaActual.getFechaEditado());
		areaBack.setEstadoRegistro(areaActual.getEstadoRegistro());
		
		try {
			areaActual.setNombre(nombreArea);
			areaActual.setFechaEditado(new Date());
			areaActual.setUsuEditado(usuarioActualizacion);
			
			areaService.save(areaActual);
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				areaService.save(areaBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el area en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El área ha sido actualizada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/area/delete/{id}")
	public ResponseEntity<?> DeleteArea(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Area areaActual = areaService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(areaActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, la area ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("AREA"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Area areaBack = new Area();
		areaBack.setId(areaActual.getId());
		areaBack.setNombre(areaActual.getNombre());
		areaBack.setUsuCreado(areaActual.getUsuCreado());
		areaBack.setFechaCreado(areaActual.getFechaCreado());
		areaBack.setUsuEditado(areaActual.getUsuEditado());
		areaBack.setFechaEditado(areaActual.getFechaEditado());
		areaBack.setEstadoRegistro(areaActual.getEstadoRegistro());
		
		areaActual.setEstadoRegistro('B');
		areaActual.setFechaEditado(new Date());
		areaActual.setUsuEditado(usuarioActualizacion);
		
		try {
			areaService.save(areaActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				areaService.save(areaBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el área en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El área ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
}