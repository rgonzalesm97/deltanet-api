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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delta.deltanet.models.entity.Area;
import com.delta.deltanet.models.entity.CatalogoServicio;
import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.service.IAreaService;
import com.delta.deltanet.models.service.ICatalogoServicioService;
import com.delta.deltanet.models.service.IHistorialService;
import com.delta.deltanet.models.service.ITipoAccionService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketCatalogoServicioController {

	@Autowired
	private IAreaService areaService;
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
		catalogoBack.setUsuarios(catalogo.getUsuarios());
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
		catalogoBack.setUsuarios(catalogo.getUsuarios());
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
}
