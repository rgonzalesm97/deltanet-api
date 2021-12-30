package com.delta.deltanet.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.delta.deltanet.models.entity.Archivo;
import com.delta.deltanet.models.service.IArchivoService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketArchivoController {

	@Autowired
	private IArchivoService archivoService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
	
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
}