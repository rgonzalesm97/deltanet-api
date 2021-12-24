package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.delta.deltanet.models.dao.IArchivoDao;
import com.delta.deltanet.models.entity.Archivo;

@Service
public class ArchivoServiceImpl implements IArchivoService {
	
	@Autowired
	private IArchivoDao archivoDao;
	@Autowired
	private IFilesStorageService fileStorageService;

	@Override
	public List<Archivo> findAll() {
		return archivoDao.findAll();
	}

	@Override
	public Archivo findById(Long id) {
		return archivoDao.findById(id).get();
	}

	@Override
	public Archivo save(Archivo archivo) {
		return archivoDao.save(archivo);
	}

	@Override
	public void delete(Long id) {
		Archivo file = archivoDao.getById(id);
		String nomFile = file.getNombre();
		fileStorageService.deleteArchivo(nomFile);
		archivoDao.deleteById(id);
	}

	@Override
	public void registrar(MultipartFile archivo, String tabla, Long idTabla) {
		String dataArchivo = null;
		
		Archivo file = new Archivo();
		dataArchivo = fileStorageService.save(archivo);
		
		String[] datos = dataArchivo.split("\\|",-1);
				
		file.setNombre(datos[0]);
		file.setTabla(tabla);
		file.setTablaId(idTabla);
		file.setUrl(datos[1]);
		archivoDao.save(file);	
	}

	@Override
	public List<Archivo> findByTablaAndTablaId(String tabla, Long tablaId) {
		List<Archivo> archivos = archivoDao.findByTablaAndTablaId(tabla, tablaId);
		return archivos;
	}
	
}
