package com.delta.deltanet.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.delta.deltanet.models.entity.Archivo;
import com.delta.deltanet.models.entity.Area;
import com.delta.deltanet.models.entity.CatalogoServicio;
import com.delta.deltanet.models.entity.Categoria;
import com.delta.deltanet.models.entity.Comentario;
import com.delta.deltanet.models.entity.Estado;
import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.entity.Prioridad;
import com.delta.deltanet.models.entity.Ticket;
import com.delta.deltanet.models.entity.TipoAccion;
import com.delta.deltanet.models.entity.UsuarioServicio;
import com.delta.deltanet.models.service.IArchivoService;
import com.delta.deltanet.models.service.IAreaService;
import com.delta.deltanet.models.service.ICatalogoServicioService;
import com.delta.deltanet.models.service.ICategoriaService;
import com.delta.deltanet.models.service.IComentarioService;
import com.delta.deltanet.models.service.IEstadoService;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.IPrioridadService;
import com.delta.deltanet.models.service.ITicketService;
import com.delta.deltanet.models.service.ITipoAccionService;
import com.delta.deltanet.models.service.IUsuarioServicioService;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/ticket")
public class TicketController {

	@Autowired
	private ITicketService ticketService;
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
	@Autowired
	private IArchivoService archivoService;
	@Autowired
	private ICatalogoServicioService catalogoServicioService;
	@Autowired
	private IUsuarioServicioService usuarioServicioService;
	@Autowired
	private IHistorialService historialService;
	@Autowired
	private IComentarioService comentarioService;
  
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
	
	//CATEGORIA
	@PostMapping("/categoria/create")
	public ResponseEntity<?> CreateCategoria(@RequestParam("nombreCategoria") String nombreCategoria, @RequestParam("usuario") String usuarioCreacion){
		Categoria categoria = new Categoria();
		categoria.setNombre(nombreCategoria);
		categoria.setUsuCreado(usuarioCreacion);
		categoria.setFechaCreado(new Date());
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("CATEGORIA"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuarioCreacion);
		historial.setFechaCreado(new Date());
		
		Categoria categoriaCreada = new Categoria();
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			categoriaCreada = categoriaService.save(categoria);
			
			try {
				historial.setTablaId(categoriaCreada.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				categoriaService.delete(categoriaCreada.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
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
	public ResponseEntity<?> UpdateCategoria(@PathVariable Long id, @RequestParam("nombreCategoria") String nombreCategoria, @RequestParam("usuario") String usuarioActualizacion) {
		Categoria categoriaActual = categoriaService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(categoriaActual==null) {
			response.put("mensaje", "Error: no se puede editar, la categoria ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("CATEGORIA"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Categoria categoriaBack = new Categoria();
		categoriaBack.setId(categoriaActual.getId());
		categoriaBack.setNombre(categoriaActual.getNombre());
		categoriaBack.setUsuCreado(categoriaActual.getUsuCreado());
		categoriaBack.setFechaCreado(categoriaActual.getFechaCreado());
		categoriaBack.setUsuEditado(categoriaActual.getUsuEditado());
		categoriaBack.setFechaEditado(categoriaActual.getFechaEditado());
		categoriaBack.setEstadoRegistro(categoriaActual.getEstadoRegistro());
		
		try {
			categoriaActual.setNombre(nombreCategoria);
			categoriaActual.setFechaEditado(new Date());
			categoriaActual.setUsuEditado(usuarioActualizacion);
			
			categoriaService.save(categoriaActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				categoriaService.save(categoriaBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la categoria en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La categoria ha sido actualizada con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/categoria/delete/{id}")
	public ResponseEntity<?> DeleteCategoria(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Categoria categoriaActual = categoriaService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(categoriaActual==null) {
			response.put("mensaje", "Error: no se puede eliminar, la categoria ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("CATEGORIA"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Categoria categoriaBack = new Categoria();
		categoriaBack.setId(categoriaActual.getId());
		categoriaBack.setNombre(categoriaActual.getNombre());
		categoriaBack.setUsuCreado(categoriaActual.getUsuCreado());
		categoriaBack.setFechaCreado(categoriaActual.getFechaCreado());
		categoriaBack.setUsuEditado(categoriaActual.getUsuEditado());
		categoriaBack.setFechaEditado(categoriaActual.getFechaEditado());
		categoriaBack.setEstadoRegistro(categoriaActual.getEstadoRegistro());
		
		categoriaActual.setEstadoRegistro('B');
		categoriaActual.setFechaEditado(new Date());
		categoriaActual.setUsuEditado(usuarioActualizacion);
		
		try {
			categoriaService.save(categoriaActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				categoriaService.save(categoriaBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
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
	
  //ARCHIVO
	@PostMapping("/archivo/CreateArchivo")
	public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files,
			                             @RequestParam("IdTabla") Long idTabla,
			                             @RequestParam("Tabla") String Tabla
			                            ) {
		Map<String, Object> response = new HashMap<>();
		List<Archivo> lstFiles = archivoService.findByTablaAndTablaId(Tabla, idTabla);
		if(lstFiles.size() > 0 ) {
			response.put("mensaje", "Ya existen archivos para la tabla: " + Tabla + ", TablaId: " + idTabla);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CONFLICT);
		}
		
		try {
			List<String> fileNames = new ArrayList<>();

			Arrays.asList(files).stream().forEach(file -> {
				archivoService.registrar(file, Tabla, idTabla);
				fileNames.add(file.getOriginalFilename());
			});
			response.put("mensaje", "Archivos subidos correctamente: " + fileNames);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje", "Falla al subir archivos...");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/archivo/ReadAllArchivo")
	public ResponseEntity<?> readFiles(@RequestParam("IdTabla") Long idTabla,
                                         @RequestParam("Tabla") String Tabla
                                        ){
		
		Map<String, Object> response = new HashMap<>();
		try {
			List<Archivo> archivos = archivoService.findByTablaAndTablaId(Tabla, idTabla);
			if (archivos==null) {
				response.put("mensaje", "No se encontraron archivos.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			response.put("archivos", archivos);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje", "No se obtuvo listado de archivos");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		}
	}
	
	@DeleteMapping("/archivo/DeleteArchivo/{id}")
	public ResponseEntity<?> deleteFile(@PathVariable Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			archivoService.delete(id);
			response.put("mensaje","Archivo eliminado satisfactoriamente.");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje","No se elimino el archivo.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	//CATALOGOSERVICIO
	@PostMapping("/catalogo/CreateCatalogoServicio")
	public ResponseEntity<?> creaCatalogo(@RequestParam("nombreCat") String nomCatalogo,
			                              @RequestParam("idArea") Long idArea,
			                              @RequestParam("usuario") String usuario
			                             ){
		Map<String, Object> response = new HashMap<>();
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("CATALOGO"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuario);
		historial.setFechaCreado(new Date());
		
		Area area = null;
		try {
			area = areaService.findById(idArea);
			if (area == null) {
				response.put("mensaje","Area no encontrada.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al buscar area.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		CatalogoServicio catalogoCreado = new CatalogoServicio();
		
		try {
			CatalogoServicio catalogo = new CatalogoServicio();
			catalogo.setNombre(nomCatalogo);
			catalogo.setArea(area);
			catalogo.setUsuCreado(usuario);
			catalogoCreado = catalogoServicioService.save(catalogo);
			
			try {
				historial.setTablaId(catalogoCreado.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				catalogoServicioService.delete(catalogoCreado.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
		} catch (Exception e) {
			response.put("mensaje","Error al crear catalogo.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		response.put("mensaje","El catalogo se creo satisfactoriamente.");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@GetMapping("/catalogo/ReadCatalogoServicio/{id}")
	public ResponseEntity<?> leeCatalogo(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		CatalogoServicio catalogo = null;
		try {
			catalogo = catalogoServicioService.findByIdAndEstado(id, "A");
			if (catalogo == null) {
				response.put("mensaje","No se encontraron catalogos.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			response.put("mensaje","El catalogo se obtuvo satisfactoriamente.");
			response.put("catalogo", catalogo);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje","Error al leer catalogo.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping(value={"/catalogo/ReadAllCatalogoServicio/{idArea}", "/catalogo/ReadAllCatalogoServicio"})
	public ResponseEntity<?> listCatalogo(@PathVariable Optional<Long> idArea) {
		Map<String, Object> response = new HashMap<>();
		List<CatalogoServicio> catalogos = null;
		Area area = null;
		if(idArea.isPresent()) {
			try {
				area=areaService.findById(idArea.get());
				if(area == null) {
					response.put("mensaje","No se encontro el area.");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
				}
			} catch (Exception e) {
				response.put("mensaje","Error al ubicar el area.");
				response.put("error",e.getMessage());
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}			
		}
		try {
			if(idArea.isPresent()) {
				catalogos = catalogoServicioService.findByAreaAndEstado(area, "A");				
			}else {
				catalogos = catalogoServicioService.findAll();
			}
			if (catalogos == null) {
				response.put("mensaje","No se encontraron catalogos para el area requerida.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			response.put("mensaje","Se obtuvo el listado de catalogos.");
			response.put("catalogos", catalogos);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje","Error al obtener catalogos.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/catalogo/UpdateCatalogoServicio")
	public ResponseEntity<?> updateCatalogo(@RequestParam("idCatalogo") Long id,
			                                @RequestParam("nombreCat") String nombreCat,
			                                @RequestParam("idArea") Long idArea,
			                                @RequestParam("usuario") String usuario
			                               ){
		Map<String, Object> response = new HashMap<>();
		CatalogoServicio catalogo = null;
		Area area = null;
		try {
			area = areaService.findById(idArea);
			if(area==null) {
				response.put("mensaje","No se encuentra el area.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al buscar el area.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			catalogo = catalogoServicioService.findById(id);
			if(catalogo==null) {
				response.put("mensaje","No se encontro el catalogo.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al no encontrar el catalogo.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("CATALOGO"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(usuario);
		historial.setFechaCreado(new Date());
		
		CatalogoServicio catalogoBack = new CatalogoServicio();
		catalogoBack.setId(catalogo.getId());
		catalogoBack.setArea(catalogo.getArea());
		//catalogoBack.setUsuarioServicios(catalogo.getUsuarioServicios());//Revisar si esto no genera conflictos
		catalogoBack.setNombre(catalogo.getNombre());
		catalogoBack.setUsuCreado(catalogo.getUsuCreado());
		catalogoBack.setFechaCreado(catalogo.getFechaCreado());
		catalogoBack.setUsuEditado(catalogo.getUsuEditado());
		catalogoBack.setFechaEditado(catalogo.getFechaEditado());
		catalogoBack.setEstadoRegistro(catalogo.getEstadoRegistro());
		
		try {
			catalogo.setNombre(nombreCat);
			catalogo.setArea(area);
			catalogo.setUsuEditado(usuario);
			catalogo.setFechaEditado(new Date());
			catalogoServicioService.save(catalogo);
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				catalogoServicioService.save(catalogoBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al actualizar catalogo.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","Se actualizo el catalogo.");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@PutMapping("/catalogo/DeleteCatalogoServicio")
	public ResponseEntity<?> deleteCatalogo(@RequestParam("idCatalogo") Long id,
			                                @RequestParam("usuario") String usuario
			                               ){
		Map<String, Object> response = new HashMap<>();
		CatalogoServicio catalogo = null;
		try {
			catalogo = catalogoServicioService.findById(id);
			if(catalogo==null) {
				response.put("mensaje","No se encontro el catalogo.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al buscar catalogo.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("CATALOGO"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(usuario);
		historial.setFechaCreado(new Date());
		
		CatalogoServicio catalogoBack = new CatalogoServicio();
		catalogoBack.setId(catalogo.getId());
		catalogoBack.setArea(catalogo.getArea());
		//catalogoBack.setUsuarioServicios(catalogo.getUsuarioServicios());//Revisar si esto no genera conflictos
		catalogoBack.setNombre(catalogo.getNombre());
		catalogoBack.setUsuCreado(catalogo.getUsuCreado());
		catalogoBack.setFechaCreado(catalogo.getFechaCreado());
		catalogoBack.setUsuEditado(catalogo.getUsuEditado());
		catalogoBack.setFechaEditado(catalogo.getFechaEditado());
		catalogoBack.setEstadoRegistro(catalogo.getEstadoRegistro());
		
		try {
			catalogo.setEstadoRegistro("B");
			catalogo.setUsuEditado(usuario);
			catalogo.setFechaEditado(new Date());
			
			catalogoServicioService.save(catalogo);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				catalogoServicioService.save(catalogoBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al eliminar catalogo.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","Se elimino el catalogo.");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	//USUARIO
	@PostMapping("/usuario/CreateUsuarioServicio")
	public ResponseEntity<?> creaUsuario(@RequestParam("usuario") String usuario,
			                             @RequestParam("nombre") String nombre,
			                             @RequestParam("apellido") String apellido,
			                             @RequestParam("rol") char rol,
			                             @RequestParam("userCrea") String userCrea
			                            ){
		Map<String, Object> response = new HashMap<>();
		UsuarioServicio user = new UsuarioServicio();
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("USUARIO"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(userCrea);
		historial.setFechaCreado(new Date());
		
		UsuarioServicio userCreado = new UsuarioServicio();
		
		try {
			user.setUsuario(usuario);
			user.setNombre(nombre);
			user.setApellidos(apellido);
			user.setRol(rol);
			user.setUsuCreado(userCrea);
			
			userCreado = usuarioServicioService.save(user);
			
			try {
				
				historial.setTablaId(userCreado.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				usuarioServicioService.delete(userCreado.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
		} catch (Exception e) {
			response.put("mensaje","Error al crear usuario.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		response.put("mensaje","Se creo el usuario satisfactoriamente.");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@GetMapping("/usuario/ReadUsuarioServicio/{id}")
	public ResponseEntity<?> leeUsuario(@PathVariable Long id){
		Map<String, Object> response = new HashMap<>();
		UsuarioServicio usuario = null;
		try {
			usuario = usuarioServicioService.findByIdAndEstado(id, "A");
			if (usuario == null) {
				response.put("mensaje","No se encontro el usuario.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			response.put("usuario", usuario);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje","Error al buscar usuario.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/usuario/ReadAllUsuarioServicio")
	public ResponseEntity<?> listaUsuarios(){
		Map<String, Object> response = new HashMap<>();
		List<UsuarioServicio> usuarios = null;
		try {
			usuarios = usuarioServicioService.listado();
			if (usuarios == null) {
				response.put("mensaje","No se encontraron usuarios.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			response.put("usuarios",usuarios);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje","Error al listar usuarios.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/usuario/UpdateUsuarioServicio")
	public ResponseEntity<?> actUsuario(@RequestParam("idUsuario") Long idUser,
			                             @RequestParam("nombre") String nombre,
			                             @RequestParam("apellido") String apellido,
			                             @RequestParam("rol") char rol,
			                             @RequestParam("userActu") String userActu
			                            ){
		Map<String, Object> response = new HashMap<>();
		UsuarioServicio user = null;
		try {
			user=usuarioServicioService.findByIdAndEstado(idUser, "A");
			if (user==null) {
				response.put("mensaje","Usuario no encontrado.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al buscar usuario.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("USUARIO"));
		historial.setTablaId(idUser);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(userActu);
		historial.setFechaCreado(new Date());
		
		UsuarioServicio userBack = new UsuarioServicio();
		userBack.setId(user.getId());
		userBack.setCatalogoServicios(user.getCatalogoServicios());//Revisar si esto no genera conflictos
		userBack.setNombre(user.getNombre());
		userBack.setApellidos(user.getApellidos());
		userBack.setRol(user.getRol());
		userBack.setUsuCreado(user.getUsuCreado());
		userBack.setFechaCreado(user.getFechaCreado());
		userBack.setUsuEditado(user.getUsuEditado());
		userBack.setFechaEditado(user.getFechaEditado());
		userBack.setEstadoRegistro(user.getEstadoRegistro());
		
		try {
			user.setNombre(nombre);
			user.setApellidos(apellido);
			user.setRol(rol);
			user.setUsuEditado(userActu);
			user.setFechaEditado(new Date());
			usuarioServicioService.save(user);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				usuarioServicioService.save(userBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al actualizar usuario.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		response.put("mensaje","Se actualizo el usuario satisfactoriamente.");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@PutMapping("/usuario/DeleteUsuarioServicio")
	public ResponseEntity<?> delUsuario(@RequestParam("idUsuario") Long idUser,
			                             @RequestParam("userActu") String userActu
			                            ){
		Map<String, Object> response = new HashMap<>();
		UsuarioServicio user = null;
		try {
			user=usuarioServicioService.findByIdAndEstado(idUser, "A");
			if (user==null) {
				response.put("mensaje","Usuario no encontrado.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al buscar usuario.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("USUARIO"));
		historial.setTablaId(idUser);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(userActu);
		historial.setFechaCreado(new Date());
		
		UsuarioServicio userBack = new UsuarioServicio();
		userBack.setId(user.getId());
		userBack.setCatalogoServicios(user.getCatalogoServicios());//Revisar si esto no genera conflictos
		userBack.setNombre(user.getNombre());
		userBack.setApellidos(user.getApellidos());
		userBack.setRol(user.getRol());
		userBack.setUsuCreado(user.getUsuCreado());
		userBack.setFechaCreado(user.getFechaCreado());
		userBack.setUsuEditado(user.getUsuEditado());
		userBack.setFechaEditado(user.getFechaEditado());
		userBack.setEstadoRegistro(user.getEstadoRegistro());
		
		try {
			user.setEstadoRegistro("B");
			user.setUsuEditado(userActu);
			user.setFechaEditado(new Date());
			usuarioServicioService.save(user);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				usuarioServicioService.save(userBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			response.put("mensaje","Error al eliminar usuario.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		response.put("mensaje","Se elimino el usuario satisfactoriamente.");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}

	//HISTORIAL
	@GetMapping("/historial/read/{tabla}")
	public ResponseEntity<?> ReadAllHistorialTabla(@PathVariable String tabla) {
		List<Historial> historial = historialService.findAllByTabla(tabla);
		
		return new ResponseEntity<List<Historial>>(historial,HttpStatus.OK);
	}
	
	@GetMapping("/historial/read/{tabla}/{idTabla}")
	public ResponseEntity<?> ReadAllHistorialItem(@PathVariable String tabla, @PathVariable Long idTabla) {
		List<Historial> historial = historialService.findAllByItem(tabla, idTabla);
		
		return new ResponseEntity<List<Historial>>(historial,HttpStatus.OK);
	}

	@PostMapping("/comentario/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> CreateComentario(@RequestParam("descripcion") String descripcion,
											@RequestParam("visibilidad") char visibilidad,
											@RequestParam("idTicket") Long idTicket,
											@RequestParam("usuario") String usuarioCreacion){
		Map<String, Object> response = new HashMap<>();
		
		Ticket ticket = ticketService.findById(idTicket);
		if(ticket==null) {
			response.put("mensaje", "El ticket ID: ".concat(idTicket.toString()
					.concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Comentario comentario = new Comentario();
		comentario.setTicket(ticket);
		comentario.setUsuario(usuarioCreacion);
		comentario.setDescripcion(descripcion);
		comentario.setVisibilidad(visibilidad);
		comentario.setUsuCreado(usuarioCreacion);
		comentario.setFechaCreado(new Date());
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("COMENTARIO"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuarioCreacion);
		historial.setFechaCreado(new Date());
		
		Comentario comentarioCreado = new Comentario();
		
		
		try {
			
			comentarioCreado = comentarioService.save(comentario);
			
			try {
				
				historial.setTablaId(comentarioCreado.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				comentarioService.delete(comentarioCreado.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El comentario ha sido creado con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/comentario/read/{id}")
	public ResponseEntity<?> ReadComentario(@PathVariable Long id) {
		Comentario comentario= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			comentario = comentarioService.findById(id);
			
			if(comentario==null) {
				response.put("mensaje", "El comentario ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Comentario>(comentario,HttpStatus.OK);
	}
	
	@GetMapping("/comentario/read")
	public ResponseEntity<?> ReadAllComentario(@RequestParam("idTicket") Optional<Long> idTicket) {
		if(idTicket.isPresent()) {
			List<Comentario> comentarios = comentarioService.findAllByTicket(idTicket.get());
			
			return new ResponseEntity<List<Comentario>>(comentarios,HttpStatus.OK);
		}
		
		List<Comentario> comentarios = comentarioService.findAll();
		
		return new ResponseEntity<List<Comentario>>(comentarios,HttpStatus.OK);
	}
	
	@PutMapping("/comentario/update/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> UpdateComentario(@PathVariable Long id, 
												@RequestParam("descripcion") String descripcion, 
												@RequestParam("visibilidad") char visibilidad,
												@RequestParam("usuario") String usuarioActualizacion) {
		Comentario comentarioActual = comentarioService.findById(id);
		Map<String,Object> response = new HashMap<>();
		
		if(comentarioActual==null) {
			response.put("mensaje", "Error: no se puede editar, el comentario ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("COMENTARIO"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Comentario comentarioBack = new Comentario();
		comentarioBack.setId(comentarioActual.getId());
		comentarioBack.setTicket(comentarioActual.getTicket());
		comentarioBack.setDescripcion(comentarioActual.getDescripcion());
		comentarioBack.setVisibilidad(comentarioActual.getVisibilidad());
		comentarioBack.setUsuCreado(comentarioActual.getUsuCreado());
		comentarioBack.setFechaCreado(comentarioActual.getFechaCreado());
		comentarioBack.setUsuEditado(comentarioActual.getUsuEditado());
		comentarioBack.setFechaEditado(comentarioActual.getFechaEditado());
		
		try {
			comentarioActual.setDescripcion(descripcion);
			comentarioActual.setVisibilidad(visibilidad);
			comentarioActual.setFechaEditado(new Date());
			comentarioActual.setUsuEditado(usuarioActualizacion);
			
			comentarioService.save(comentarioActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				comentarioService.save(comentarioBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el comentario en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El comentario ha sido actualizado con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/comentario/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> DeleteComentario(@PathVariable Long id, @RequestParam("usuario") String usuarioActualizacion) {
		Map<String, Object> response = new HashMap<>();
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("COMENTARIO"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
	
		try {
			historialService.save(historial);
			
			try {
				comentarioService.delete(id);
			} catch (DataAccessException e) {
				historialService.delete(historial.getId());
				
				response.put("mensaje", "Error al realizar el delete en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el comentario en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "el comentario ha sido eliminado con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	//TICKET
	@PostMapping("/create")
	public ResponseEntity<?> CreateTicket(@RequestParam("titulo") String titulo, 
											@RequestParam("descripcion") String descripcion,
											@RequestParam("tipoUsuario") char tipoUsuario,
											@RequestParam("idPrioridad") Long idPrioridad,
											@RequestParam("idCategoria") Long idCategoria,
											@RequestParam("idCatalogoServicio") Long idCatalogoServicio,
											@RequestParam("idAreaOrigen") Long idAreaOrigen,
											@RequestParam("idAreaDestino") Long idAreaDestino,
											@RequestParam("idUsuarioServicio") Long idUsuarioServicio,
											@RequestParam("usuCrea") String usuCrea){
		Ticket ticket = new Ticket();		
		Prioridad prioridad = prioridadService.findById(idPrioridad);
		Categoria categoria = categoriaService.findById(idCategoria);
		CatalogoServicio catalogoServicio = catalogoServicioService.findById(idCatalogoServicio);
		Area areaOrigen = areaService.findById(idAreaOrigen);
		Area areaDestino = areaService.findById(idAreaDestino);
		UsuarioServicio usuarioServicio = usuarioServicioService.findById(idUsuarioServicio);
		Estado estado = estadoService.findById(Long.valueOf(1));//por defecto sera 1
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("TICKET"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuCrea);
		historial.setFechaCreado(new Date());
		
		Ticket ticketCreado = new Ticket();
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			ticket.setTitulo(titulo);
			ticket.setDescripcion(descripcion);
			ticket.setUsuarioCreador(usuCrea);
			ticket.setTipoUsuarioCreador(tipoUsuario);
			ticket.setAreaDestino(areaDestino);
			ticket.setAreaOrigen(areaOrigen);
			ticket.setCatalogoServicio(catalogoServicio);
			ticket.setCategoria(categoria);
			ticket.setPrioridad(prioridad);
			ticket.setUsuarioServicio(usuarioServicio);
			ticket.setEstado(estado);
			ticket.setUsuCreado(usuCrea);
			ticket.setFechaCreado(new Date());
			
			ticketCreado = ticketService.save(ticket);
			
			try {
				
				historial.setTablaId(ticketCreado.getId());
				historialService.save(historial);
			} catch (DataAccessException e) {
				estadoService.delete(ticketCreado.getId());
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			} 
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El ticket ha sido creado con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/read/{id}")
	public ResponseEntity<?> ReadTicket(@PathVariable Long id) {
		Ticket ticket= null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			ticket = ticketService.findById(id);
			
			if(ticket==null) {
				response.put("mensaje", "El ticket ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Ticket>(ticket,HttpStatus.OK);
	}
	
	@GetMapping("/read")
	public ResponseEntity<?> ReadAllTicket() {
		//hay parametros opcionales, preguntar...
		List<Ticket> tickets= ticketService.findAll();
		
		return new ResponseEntity<List<Ticket>>(tickets,HttpStatus.OK);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> UpdateTicket(@PathVariable Long id, 
											@RequestParam("descripcion") String descripcion,
											@RequestParam("idPrioridad") Long idPrioridad,
											@RequestParam("idCategoria") Long idCategoria,
											@RequestParam("usuario") String usuarioActualizacion) {
		Ticket ticketActual = ticketService.findById(id);
		Prioridad prioridad = prioridadService.findById(idPrioridad);
		Categoria categoria = categoriaService.findById(idCategoria);
		
		Map<String,Object> response = new HashMap<>();
		
		if(ticketActual==null) {
			response.put("mensaje", "Error: no se puede editar, el ticket ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("EDITARID"))));
		historial.setTabla(tablas.get("TICKET"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("EDITAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		Ticket ticketBack = new Ticket();
		ticketBack.setId(ticketActual.getId());
		ticketBack.setAreaDestino(ticketActual.getAreaDestino());
		ticketBack.setAreaOrigen(ticketActual.getAreaOrigen());
		ticketBack.setCatalogoServicio(ticketActual.getCatalogoServicio());
		ticketBack.setCategoria(ticketActual.getCategoria());
		ticketBack.setPrioridad(ticketActual.getPrioridad());
		ticketBack.setUsuarioServicio(ticketActual.getUsuarioServicio());
		ticketBack.setUsuarioCreador(ticketActual.getUsuarioCreador());
		ticketBack.setTipoUsuarioCreador(ticketActual.getTipoUsuarioCreador());
		ticketBack.setTitulo(ticketActual.getTitulo());
		ticketBack.setDescripcion(ticketActual.getDescripcion());
		ticketBack.setUsuCreado(ticketActual.getUsuCreado());
		ticketBack.setFechaCreado(ticketActual.getFechaCreado());
		ticketBack.setUsuEditado(ticketActual.getUsuEditado());
		ticketBack.setFechaEditado(ticketActual.getFechaEditado());
		ticketBack.setEstado(ticketActual.getEstado());
		
		try {
			ticketActual.setDescripcion(descripcion);
			ticketActual.setPrioridad(prioridad);
			ticketActual.setCategoria(categoria);
			ticketActual.setFechaEditado(new Date());
			ticketActual.setUsuEditado(usuarioActualizacion);
			
			ticketService.save(ticketActual);
			
			try {
				historialService.save(historial);
			} catch (DataAccessException e) {
				ticketService.save(ticketBack);
				
				response.put("mensaje", "Error al realizar el insert en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el ticket en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El ticket ha sido actualizado con éxito!");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteTicket(@PathVariable Long id,
											@RequestParam("usuario") String usuarioActualizacion){
		Map<String, Object> response = new HashMap<>();
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("ELIMINARID"))));
		historial.setTabla(tablas.get("TICKET"));
		historial.setTablaId(id);
		historial.setAccion(acciones.get("ELIMINAR"));
		historial.setUsuCreado(usuarioActualizacion);
		historial.setFechaCreado(new Date());
		
		try {
			historialService.save(historial);
			
			try {
				ticketService.delete(id);
			} catch (DataAccessException e) {
				historialService.delete(historial.getId());
				
				response.put("mensaje", "Error al realizar el delete en la base de datos");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			response.put("mensaje","Ticket eliminado satisfactoriamente.");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			
		} catch (Exception e) {
			response.put("mensaje","No se elimino el ticket.");
			response.put("error",e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
}
