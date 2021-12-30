package com.delta.deltanet.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delta.deltanet.models.entity.Historial;
import com.delta.deltanet.models.service.IHistorialService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/ticket")
public class TicketHistorialController {

	@Autowired
	private IHistorialService historialService;
  
	//VariableEntorno
	@Value("#{${tablas}}")
	private Map<String,String> tablas;
	@Value("#{${acciones}}")
	private Map<String,String> acciones;
	
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
}
