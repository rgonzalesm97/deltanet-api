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

import com.delta.deltanet.models.entity.Categoria;
import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.service.ICategoriaService;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketCategoriaController {

	@Autowired
	private ICategoriaService categoriaService;
	@Autowired
	private ITipoAccionService tipoAccionService;
	@Autowired
	private IHistorialService historialService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
	
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
}