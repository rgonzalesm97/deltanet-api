package com.deltasac.deltanet.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.Solicitud;
import com.deltasac.deltanet.models.service.ISolicitudService;
import com.deltasac.deltanet.models.service.IUploadFileService;

@CrossOrigin(origins= {"http://localhost:4200","http://173.255.202.95:8080"})
@RestController
@RequestMapping("/apiSolic")
public class SolicitudRestController {
	
	@Autowired
	private ISolicitudService solicitudService;
	
	@Autowired
	private IUploadFileService uploadService;
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/solicitudes")
	public List<Solicitud> index(){
		return solicitudService.findAll();
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/solicitudes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Solicitud solicitud = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			System.out.println("ID: " + id);
			solicitud = solicitudService.findById(id);
			
			if(solicitud.getId()==null) {
				response.put("mensaje", "La solicitud ID: ".concat(id.toString()
						.concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		return new ResponseEntity<Solicitud>(solicitud,HttpStatus.OK);
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/solicitudes")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Solicitud solicitud, BindingResult result) {
		Solicitud solicitudNew = null;
		Map<String, Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errores", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		try {
			solicitudNew = solicitudService.save(solicitud);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realzar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido creada con éxito!");
		response.put("solicitud",solicitudNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN"})
	@PutMapping("/solicitudes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Solicitud solicitud, BindingResult result, @PathVariable Long id) {
		Solicitud solicitudActual = solicitudService.findById(id);
		Solicitud solicitudUpdated = null;
		Map<String,Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		if(solicitudActual==null) {
			response.put("mensaje", "Error: no se puede editar, la solicitud ID: "
					.concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			solicitudActual.setTitulo(solicitud.getTitulo());
			solicitudActual.setDesTitulo(solicitud.getDesTitulo());
			solicitudActual.setEstado(solicitud.getEstado());
			solicitudActual.setArea(solicitud.getArea());
			
			solicitudUpdated = solicitudService.save(solicitudActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la solicitud en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido actualizada con éxito!");
		response.put("solicitud", solicitudUpdated);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/solicitudes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Solicitud solicitud = solicitudService.findById(id);
			String nombreImagenAnterior = solicitud.getImgsol();
			
			uploadService.eliminar(nombreImagenAnterior);
			solicitudService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la solicitud en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La solicitud ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		
	}
	/*
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/solicitudes/page/{page}")
	public Page<Solicitud> index(@PathVariable Integer page){
		Sort sort = Sort.by(Sort.Direction.ASC,"estadoSolic.descrip");
		Pageable pageable = PageRequest.of(page, 8, sort);
		return solicitudService.findAll(pageable);
	}*/
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/solicitudes/page")
	public Page<Solicitud> index(@RequestParam("page") Integer page,
			                     @RequestParam("id") Integer id){
		Sort sort = Sort.by(Sort.Direction.ASC,"estadoSolic.descrip");
		Pageable pageable = PageRequest.of(page, 8, sort);
		return solicitudService.findAll(id,pageable);
	}
	
	/*
	 * Retorna lista de solicitudes creadas por un usuario (ID)
	 * 
	 * */
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/solicitudes/usuario")
	public List<Solicitud> listSolUser(@RequestParam("id") Integer id){
		return solicitudService.findAllIdCrea(id);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PostMapping("/solicitudes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo,
			                        @RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		
		Solicitud solicitud = solicitudService.findById(id);
		if(!archivo.isEmpty()) {
			String nombreArchivo = null;
			try {
				nombreArchivo = uploadService.copiar(archivo);
			} catch (IOException e) {
				response.put("mensaje", "Error al subir la imagen ");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreImagenAnterior = solicitud.getImgsol();
			
			uploadService.eliminar(nombreImagenAnterior);
			solicitud.setImgsol(nombreArchivo);
			solicitudService.save(solicitud);
			response.put("solicitud", solicitud);
			response.put("mensaje", "Has subido correctamente la imagen " + nombreArchivo);
		}
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verImagen(@PathVariable String nombreImagen){
		Resource recurso = null;
		
		try {
			recurso = uploadService.cargar(nombreImagen);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
		
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
	
	@Secured({"ROLE_ADMIN"})
	@GetMapping("/solicitudes/areas")
	public List<Area> listarAreas(){
		return solicitudService.findAllAreas();
	}

}
