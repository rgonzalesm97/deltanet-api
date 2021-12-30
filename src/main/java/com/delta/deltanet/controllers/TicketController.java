package com.delta.deltanet.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.RestController;

import com.delta.deltanet.models.entity.Area;
import com.delta.deltanet.models.entity.CatalogoServicio;
import com.delta.deltanet.models.entity.Categoria;
import com.delta.deltanet.models.entity.Estado;
import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.entity.Prioridad;
import com.delta.deltanet.models.entity.Ticket;
import com.delta.deltanet.models.entity.UsuarioServicio;
import com.delta.deltanet.models.service.IAreaService;
import com.delta.deltanet.models.service.ICatalogoServicioService;
import com.delta.deltanet.models.service.ICategoriaService;
import com.delta.deltanet.models.service.IEstadoService;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.IPrioridadService;
import com.delta.deltanet.models.service.ITicketService;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
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
	private ICatalogoServicioService catalogoServicioService;
	@Autowired
	private IHistorialService historialService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;

	//TICKET
	@PostMapping("/create")
	public ResponseEntity<?> CreateTicket(@RequestParam("titulo") String titulo, 
											@RequestParam("descripcion") String descripcion,
											@RequestParam("tipoUsuario") String tipoUsuario,
											@RequestParam("idPrioridad") Long idPrioridad,
											@RequestParam("idCategoria") Long idCategoria,
											@RequestParam("idCatalogoServicio") Long idCatalogoServicio,
											@RequestParam("idAreaOrigen") Long idAreaOrigen,
											@RequestParam("idAreaDestino") Long idAreaDestino,
											@RequestParam("usuCrea") String usuCrea){
		Map<String, Object> response = new HashMap<>();
		
		Ticket ticket = new Ticket();		
		Prioridad prioridad = prioridadService.findById(idPrioridad);
		Categoria categoria = categoriaService.findById(idCategoria);
		CatalogoServicio catalogoServicio = catalogoServicioService.findById(idCatalogoServicio);
		Area areaOrigen = areaService.findById(idAreaOrigen);
		Area areaDestino = areaService.findById(idAreaDestino);
		UsuarioServicio usuarioServicio = null;
		try {
			usuarioServicio = catalogoServicio.getUsuarios().get(0);			
		} catch (Exception e) {
			response.put("mensaje", "Error, no existen usuarios para el catalogo seleccionado");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Estado estado = estadoService.findById(Long.valueOf(1));//por defecto sera 1
		
		Historial historial = new Historial();
		historial.setTipoAccionId(tipoAccionService.findById(Long.valueOf(acciones.get("CREARID"))));
		historial.setTabla(tablas.get("TICKET"));
		historial.setAccion(acciones.get("CREAR"));
		historial.setUsuCreado(usuCrea);
		historial.setFechaCreado(new Date());
		
		Ticket ticketCreado = new Ticket();
		
		
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
	public ResponseEntity<?> ReadAllTicket(@RequestParam("idAreaOrigen") Long idAreaOrigen,
											@RequestParam("idAreaDestino") Optional<Long> idAreaDestino,
											@RequestParam("tipoUsuario") Optional<String> tipoUsuario,
											@RequestParam("idPrioridad") Optional<Long> idPrioridad,
											@RequestParam("idCategoria") Optional<Long> idCategoria,
											@RequestParam("idCatalogoServicio") Optional<Long> idCatalogoServicio,
											@RequestParam("usuCrea") Optional<String> usuarioCrea) {
		
		List<Ticket> tickets= ticketService.findAllFiltro(idAreaOrigen,
															idAreaDestino.orElse(null),
															tipoUsuario.orElse(null),
															idPrioridad.orElse(null),
															idCategoria.orElse(null),
															idCatalogoServicio.orElse(null),
															usuarioCrea.orElse(null));
		
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
	
	//MI VISTA
	@GetMapping("/mivista/read")
	public ResponseEntity<?> ReadMiVista(@RequestParam("usuario") String usuario,
											@RequestParam("idUsuario") Optional<Long> idUsuario,
											@RequestParam("idArea") Optional<Long> idArea,
											@RequestParam("rol") Optional<Long> rol) {
		Map<String, Object> response = new HashMap<>();

		List<Ticket> ticketsAsignados = new ArrayList<>();
		List<Ticket> ticketsNoAsignados = new ArrayList<>();
		List<Ticket> ticketsReportadosPorMi = new ArrayList<>();
		List<Ticket> ticketsResueltos = new ArrayList<>();
		List<Ticket> ticketsModificados = new ArrayList<>();
		List<Historial> historial = new ArrayList<>();

		List<Ticket> ticketsModAll = ticketService.findAllModificados();

		ticketsReportadosPorMi = ticketService.findAllByUsuarioCreador(usuario);

		if(idUsuario.isPresent()) {
			ticketsAsignados = ticketService.findAllByUsuarioServicio(idUsuario.get());
			ticketsNoAsignados = ticketService.findAllByUsuarioServicioNull();
			ticketsResueltos = ticketService.findAllResueltos(usuario, idUsuario.get(), idArea.get());	
			
			for (Ticket ticket : ticketsModAll) {
				if(ticket.getFechaEditado().after(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
					ticketsModificados.add(ticket);
				}
			}
		}else {
			ticketsResueltos = ticketService.findAllResueltosByUsuarioCreador(usuario);
			
			for (Ticket ticket : ticketsModAll) {
				if(ticket.getFechaEditado().after(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
					if(ticket.getUsuarioCreador() == usuario) {
						ticketsModificados.add(ticket);						
					}
				}
			}
		}
		
		//Get historial de tickets
		List<Historial> historialAll = new ArrayList<>();
		List<Ticket> ticketsAll = new ArrayList<>();
		ticketsAll.addAll(ticketsAsignados);
		ticketsAll.addAll(ticketsNoAsignados);
		ticketsAll.addAll(ticketsReportadosPorMi);
		ticketsAll.addAll(ticketsResueltos);
		ticketsAll.addAll(ticketsModificados);
		List<Long> ticketsId = new ArrayList<>();
		
		for (Ticket ticket: ticketsAll) {
			if(!ticketsId.contains(ticket.getId())) {
				historialAll.addAll(historialService.findHistorialTicket(ticket.getId()));
				ticketsId.add(ticket.getId());
			}
		}
		historialAll.sort(Comparator.comparing(Historial::getFechaCreado).reversed());
		
		int count = historialAll.size();
		if(count >= 20) {
			count = 20;
		}
		for (int i = 0; i < count; i++) {
			historial.add(historialAll.get(i));
		}
		
		response.put("ticketsAsignados", ticketsAsignados);
		response.put("ticketsNoAsignados", ticketsNoAsignados);
		response.put("ticketsReportadosPorMi", ticketsReportadosPorMi);
		response.put("ticketsResueltos", ticketsResueltos);
		response.put("ticketsModificados", ticketsModificados);
		response.put("historial", historial);
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	//GENERAR TICKET READ
	@GetMapping("/generarTicket/read")
	public ResponseEntity<?> ReadGenerarTicket() {
		
		Map<String, Object> response = new HashMap<>();
		
		List<Area> areas= areaService.findAll();
		List<Prioridad> prioridades = prioridadService.findAll();
		List<Categoria> categorias = categoriaService.findAll();
		
		response.put("areas", areas);
		response.put("prioridades", prioridades);
		response.put("categorias", categorias);
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
}
