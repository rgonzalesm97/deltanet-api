package com.delta.deltanet.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements IFilesStorageService {
	private final static String DIRECTORIO_UPLOAD = "C://TEMP//UPLOADS";
	private final Path root = Paths.get(DIRECTORIO_UPLOAD);

	@Override
	public void init() {
		try {
			Files.createDirectory(root);
		} catch (IOException e) {
			throw new RuntimeException("No se puede inicializar la carpeta para UPLOADS");
		}
		
	}

	@Override
	public String save(MultipartFile file) {
		String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "");
		String url = "";
		try {
			Path rutaArchivo = this.root.resolve(nombreArchivo).toAbsolutePath();
			
			Files.copy(file.getInputStream(), rutaArchivo);
			url = this.load(nombreArchivo).toString();
		} catch (Exception e) {
			throw new RuntimeException("No se puede almacenar el archivo. Error: " + e.getMessage());
		}
		return nombreArchivo + "|" + url;
		
	}

	@Override
	public Resource load(String filename) {
		try {
			Path file = root.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("No se puede leer el archivo!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(root.toFile());
		
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
		} catch (IOException e) {
			throw new RuntimeException("No se pueden cargar los archivos!!");
		}
	}

	@Override
	public void deleteArchivo(String filename) {
		if(filename != null && filename.length() > 0) {
			Path rutaArchivo = Paths.get(DIRECTORIO_UPLOAD).resolve(filename).toAbsolutePath();
			File ArchivoDelete = rutaArchivo.toFile();
			if(ArchivoDelete.exists() && ArchivoDelete.canRead()) {
				ArchivoDelete.delete();
			}
		}
		
	}

}
