package com.delta.deltanet.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delta.deltanet.models.entity.CatalogoServicio;
import com.delta.deltanet.models.entity.UsuarioServicio;
import com.delta.deltanet.models.service.ICatalogoServicioService;
import com.delta.deltanet.models.service.IUsuarioServicioService;

@RestController
@RequestMapping("/ticket/CatalogoUsuarios/")
public class TicketCatalogoUsuariosController {
	@Autowired
	private IUsuarioServicioService usuarioServicioService;
	@Autowired
	private ICatalogoServicioService catalogoServicioService;
	
	@PostMapping("/Create")
	public ResponseEntity<?> creaUsuario(@RequestParam("idUsuario") Long idUsuario,
			                             @RequestParam("idCatalogos[]") List<Long> idCatalogos,
			                             @RequestParam("userCrea") String userCrea
			                            ){
		Map<String, Object> response = new HashMap<>();
		UsuarioServicio usuario = null;
		List<CatalogoServicio> catalogos = new ArrayList<CatalogoServicio>();
		CatalogoServicio catalogo = null;
		System.out.println("Inicio");
		try {
			usuario = usuarioServicioService.findByIdAndEstado(idUsuario, "A");
			System.out.println("Inicio 1");
			if (usuario == null) {
				response.put("mensaje", "Usuario no encontrado o habilitado");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("mensaje", "Error al buscar usuario.");
			response.put("Error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		System.out.println("PAso");
		
		for(int i=0; i < idCatalogos.size(); i++) {
			catalogo = catalogoServicioService.findByIdAndEstado(idCatalogos.get(i), "A");
			System.out.println("Inicio 2");
			if (catalogo==null) {
				response.put("mensaje", "Catalogo no encontrado o habilitado");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			System.out.println("Agrega a la matriz");
			catalogos.add(catalogo);
			
		}
		System.out.println(catalogos);
		
		try {
			System.out.println("Inicia actualizacion");
			usuario.setCatalogoServicios(catalogos);
			usuario.setUsuEditado(userCrea);
			usuario.setFechaEditado(new Date());
			usuarioServicioService.save(usuario);
			System.out.println("Inicio 3");
			response.put("Mensaje", "Se agregaron los catalogos requeridos al usuario.");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("Mensaje", "Error al agregar catalogos requeridos al usuario.");
			response.put("Error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/ReadUsuario")
	public ResponseEntity<?> ListByUsuario(@RequestParam(value = "id", required=false) Long idUsuario
			                            ){
		Map<String, Object> response = new HashMap<>();
		List<CatalogoServicio> catalogos = new ArrayList<CatalogoServicio>();
		List<UsuarioServicio> usuarios = new ArrayList<UsuarioServicio>();
		CatalogoServicio catalogo = null;
		UsuarioServicio usuario = null;
		try {
			if (idUsuario == null) {
				try {
					usuarios = usuarioServicioService.findAll();
					for (int a=0; a<usuarios.size(); a++) {
						for (int b=0;b< usuarios.get(a).getCatalogoServicios().size();b++) {
							usuarios.get(a).getCatalogoServicios().get(b).setUsuarios(null);
						}
					}
					response.put("usuarios",usuarios);
					response.put("Mensaje", "Se listaron los catalogos requeridos al usuario.");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
				} catch (Exception e) {
					response.put("Mensaje", "Error al obtener data de usuarios.");
					response.put("error", e.getMessage());
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
			}else {
				usuario = usuarioServicioService.findByIdAndEstado(idUsuario, "A");
				if(usuario==null) {
					response.put("mensaje","Usuario no encontrado");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
				}
				for(int u=0; u < usuario.getCatalogoServicios().size();u++) {
					catalogo = usuario.getCatalogoServicios().get(u);
					catalogo.setUsuarios(null);
					catalogos.add(catalogo);
				}
				response.put("catalogos", catalogos);
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			}
		} catch (Exception e) {
			response.put("mensaje", "Error al leer usuarios.");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/ReadCatalogo")
	public ResponseEntity<?> ListByatalogo(@RequestParam(value = "id", required=false) Long idCatalogo) {
		Map<String, Object> response = new HashMap<>();
		List<CatalogoServicio> catalogos = new ArrayList<CatalogoServicio>();
		List<UsuarioServicio> usuarios = new ArrayList<UsuarioServicio>();
		CatalogoServicio catalogo = null;
		UsuarioServicio usuario = null;
		
		try {
			if (idCatalogo == null) {
				try {
					catalogos = catalogoServicioService.findAll();
					for (int a=0; a<catalogos.size();a++) {
						for (int b=0; b<catalogos.get(a).getUsuarios().size();b++) {
							catalogos.get(a).getUsuarios().get(b).setCatalogoServicios(null);
						}
					}
					response.put("catalogos", catalogos);
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
				} catch (Exception e) {
					response.put("mensaje", "Error al obtener data de catalogos");
					response.put("error", e.getMessage());
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
			} else {
				catalogo = catalogoServicioService.findByIdAndEstado(idCatalogo, "A");
				if (catalogo == null) {
					response.put("Mensaje", "Catalogo no encontrado");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
				}
				for(int u=0;u<catalogo.getUsuarios().size();u++) {
					usuario = catalogo.getUsuarios().get(u);
					usuario.setCatalogoServicios(null);
					usuarios.add(usuario);
				}
			}
			response.put("usuarios", usuarios);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje", "Error al leer catalogos.");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	@PutMapping("/UpdateCatalogo")
	public ResponseEntity<?> UpdCatalogo(@RequestParam("idUsuario") Long idUsuario,
			                             @RequestParam("idCatalogos[]") List<Long> idCatalogos,
			                             @RequestParam("usrActualiza") String usrActualiza
			                            ) {
		Map<String, Object> response = new HashMap<>();
		UsuarioServicio usuario = null;
		CatalogoServicio catalogo = null;
		List<CatalogoServicio> catalogos = new ArrayList<CatalogoServicio>();
		try {
			usuario = usuarioServicioService.findByIdAndEstado(idUsuario, "A");
			if (usuario==null) {
				response.put("mensaje","usuario no encontrado.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			usuario.setCatalogoServicios(null);
			for(int a=0; a<idCatalogos.size();a++) {
				catalogo = catalogoServicioService.findByIdAndEstado(idCatalogos.get(a), "A");
				if (catalogo==null) {
					response.put("mensaje","catalogo no encontrado.");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
				}
				catalogos.add(catalogo);
			}
			usuario.setCatalogoServicios(catalogos);
			usuario.setUsuEditado(usrActualiza);
			usuario.setFechaEditado(new Date());
			usuarioServicioService.save(usuario);
			response.put("mensaje","Se actualizo la relacion usuario-catalogo satisfactoriamente.");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (Exception e) {
			response.put("mensaje", "Error al actualizar relacion usuario - catalogos.");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
