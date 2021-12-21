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

import com.delta.deltanet.models.entity.Area;
import com.delta.deltanet.models.entity.Categoria;
import com.delta.deltanet.models.entity.Estado;
import com.delta.deltanet.models.entity.Prioridad;
import com.delta.deltanet.models.entity.TipoAccion;
import com.delta.deltanet.models.service.IAreaService;
import com.delta.deltanet.models.service.ICategoriaService;
import com.delta.deltanet.models.service.IEstadoService;
import com.delta.deltanet.models.service.IPrioridadService;
import com.delta.deltanet.models.service.ITipoAccionService;

@RestController
@RequestMapping("/ticket")
public class TicketController {

	@Autowired
	private IPrioridadService prioridadService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private ICategoriaService categoriaService;
	@Autowired
	private IEstadoService estadoService;
	@Autowired
	private ITipoAccionService tipoAccionService;
	
	
	
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
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/prioridad/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> DeletePrioridad(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Prioridad prioridadActual = prioridadService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(prioridadActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, la prioridad ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		prioridadActual.setEstadoRegistro('B');
		prioridadActual.setFechaEditado(new Date());
		prioridadActual.setUsuEditado(usuarioActualizacion);
		
		try {
			prioridadService.save(prioridadActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la prioridad en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La prioridad ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	//AREA
	@PostMapping("/area/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> CreateArea(@RequestParam("nombreArea") String nombreArea, @RequestParam("usuario") String usuarioCreacion){
		Area area = new Area();
		area.setNombre(nombreArea);
		area.setUsuCreado(usuarioCreacion);
		area.setFechaCreado(new Date());
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			areaService.save(area);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
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
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> UpdateArea(@PathVariable Long id, @RequestParam("nombreArea") String nombreArea, @RequestParam("usuario") String usuarioActualizacion) {
		Area areaActual = areaService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(areaActual==null) {
			response.put("mensaje", "Error: no se puede editar, el área ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			areaActual.setNombre(nombreArea);
			areaActual.setFechaEditado(new Date());
			areaActual.setUsuEditado(usuarioActualizacion);
			
			areaService.save(areaActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el area en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El área ha sido actualizada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/area/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> DeleteArea(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Area areaActual = areaService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(areaActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, la area ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		areaActual.setEstadoRegistro('B');
		areaActual.setFechaEditado(new Date());
		areaActual.setUsuEditado(usuarioActualizacion);
		
		try {
			areaService.save(areaActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el área en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El área ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	//CATEGORIA
	@PostMapping("/categoria/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> CreateCategoria(@RequestParam("nombreCategoria") String nombreCategoria, @RequestParam("usuario") String usuarioCreacion){
		Categoria categoria = new Categoria();
		categoria.setNombre(nombreCategoria);
		categoria.setUsuCreado(usuarioCreacion);
		categoria.setFechaCreado(new Date());
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			categoriaService.save(categoria);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La categoria ha sido creada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/categoria/read/{id}")
	public ResponseEntity<?> ReadCategoria(@PathVariable Long id) {
		Categoria categoria= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			categoria = categoriaService.findById(id);
			
			if(categoria==null) {
				response.put("mensaje", "La categoria ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			if(categoria.getEstadoRegistro()=='B') {
				response.put("mensaje", "La categoria ID: ".concat(id.toString()
						.concat(" ha sido eliminada")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Categoria>(categoria,HttpStatus.OK);
	}
	
	@GetMapping("/categoria/read")
	public ResponseEntity<?> ReadAllCategoria() {
		List<Categoria> categorias = categoriaService.findAll();
		
		return new ResponseEntity<List<Categoria>>(categorias,HttpStatus.OK);
	}
	
	@PutMapping("/categoria/update/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> UpdateCategoria(@PathVariable Long id, @RequestParam("nombreCategoria") String nombreCategoria, @RequestParam("usuario") String usuarioActualizacion) {
		Categoria categoriaActual = categoriaService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(categoriaActual==null) {
			response.put("mensaje", "Error: no se puede editar, la categoria ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			categoriaActual.setNombre(nombreCategoria);
			categoriaActual.setFechaEditado(new Date());
			categoriaActual.setUsuEditado(usuarioActualizacion);
			
			categoriaService.save(categoriaActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la categoria en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La categoria ha sido actualizada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/categoria/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> DeleteCategoria(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Categoria categoriaActual = categoriaService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(categoriaActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, la categoria ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		categoriaActual.setEstadoRegistro('B');
		categoriaActual.setFechaEditado(new Date());
		categoriaActual.setUsuEditado(usuarioActualizacion);
		
		try {
			categoriaService.save(categoriaActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la categoria en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "la categoria ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	//ESTADO
	@PostMapping("/estado/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> CreateEstado(@RequestParam("nombreEstado") String nombreEstado, @RequestParam("usuario") String usuarioCreacion){
		Estado estado = new Estado();
		estado.setNombre(nombreEstado);
		estado.setUsuCreado(usuarioCreacion);
		estado.setFechaCreado(new Date());
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			estadoService.save(estado);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
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
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> UpdateEstado(@PathVariable Long id, @RequestParam("nombreEstado") String nombreEstado, @RequestParam("usuario") String usuarioActualizacion) {
		Estado estadoActual = estadoService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(estadoActual==null) {
			response.put("mensaje", "Error: no se puede editar, el estado ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			estadoActual.setNombre(nombreEstado);
			estadoActual.setFechaEditado(new Date());
			estadoActual.setUsuEditado(usuarioActualizacion);
			
			estadoService.save(estadoActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el estado en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El estado ha sido actualizada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/estado/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> DeleteEstado(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Estado estadoActual = estadoService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(estadoActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, el estado ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		estadoActual.setEstadoRegistro('B');
		estadoActual.setFechaEditado(new Date());
		estadoActual.setUsuEditado(usuarioActualizacion);
		
		try {
			estadoService.save(estadoActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el estado en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "el estado ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	//TIPO ACCION
	@PostMapping("/tipoAccion/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> CreateTipoAccion(@RequestParam("nombreTipoAccion") String nombreTipoAccion, @RequestParam("usuario") String usuarioCreacion){
		TipoAccion tipoAccion = new TipoAccion();
		tipoAccion.setNombre(nombreTipoAccion);
		tipoAccion.setUsuCreado(usuarioCreacion);
		tipoAccion.setFechaCreado(new Date());
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			tipoAccionService.save(tipoAccion);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
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
	@ResponseStatus(HttpStatus.CREATED)
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
	
	@DeleteMapping("/tipoAccion/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
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
