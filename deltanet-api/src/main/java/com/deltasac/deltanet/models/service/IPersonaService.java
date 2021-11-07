package com.deltasac.deltanet.models.service;

import com.deltasac.deltanet.models.entity.Persona;

public interface IPersonaService {
	
	public Persona findByUsername(String username);

}
