package com.delta.deltanet.controllers;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.delta.deltanet.models.entity.Comentario;
import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.entity.Ticket;
import com.delta.deltanet.models.service.IComentarioService;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.ITicketService;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketComentarioController {

	@Autowired
	private ITicketService ticketService;
	@Autowired
	private ITipoAccionService tipoAccionService;
	@Autowired
	private IHistorialService historialService;
	@Autowired
	private IComentarioService comentarioService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;

	//COMENTARIO
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
}