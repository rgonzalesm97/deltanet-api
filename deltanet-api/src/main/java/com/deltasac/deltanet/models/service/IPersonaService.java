package com.deltasac.deltanet.models.service;

import java.util.List;
import java.util.Optional;

import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.Persona;
import com.deltasac.deltanet.models.entity.Role;
import com.deltasac.deltanet.models.entity.Solicitud;

public interface IPersonaService {
	
	public Persona findByUsername(String username);
	public Optional<Persona> findById(Long id);
	public List<Persona> findAll();
	public void delete(Long id);
	public Persona save(Persona persona);
	public List<Role> findAllRoles();

}
