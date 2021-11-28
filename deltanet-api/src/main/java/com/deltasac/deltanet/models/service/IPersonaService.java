package com.deltasac.deltanet.models.service;

import java.util.Optional;

import com.deltasac.deltanet.models.entity.Persona;

public interface IPersonaService {
	
	public Persona findByUsername(String username);
	public Optional<Persona> findById(Long id);

}
