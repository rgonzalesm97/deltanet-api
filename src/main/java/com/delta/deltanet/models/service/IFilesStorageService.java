package com.delta.deltanet.models.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFilesStorageService {
	
	public void init();
	public String save(MultipartFile file);
	public Resource load(String filename);
	public void deleteAll();
	public Stream<Path> loadAll();

}
