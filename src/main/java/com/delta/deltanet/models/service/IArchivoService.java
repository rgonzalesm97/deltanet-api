package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.delta.deltanet.models.entity.Archivo;

public interface IArchivoService {
	
	public List<Archivo> findAll();
	public List<Archivo> findByTablaAndTablaId(String tabla, Long tablaId);
	public Archivo findById(Long id);
	public Archivo save(Archivo archivo);
	public void delete(Long id);
	public void registrar(MultipartFile archivo, String tabla, Long idTabla);
	
}
