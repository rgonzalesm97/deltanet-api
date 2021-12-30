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
import com.delta.deltanet.models.entity.UsuarioServicio;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.ITipoAccionService;
import com.delta.deltanet.models.service.IUsuarioServicioService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketUsuarioServicioController {

	@Autowired
	private ITipoAccionService tipoAccionService;
	@Autowired
	private IUsuarioServicioService usuarioServicioService;
	@Autowired
	private IHistorialService historialService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
	
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
}