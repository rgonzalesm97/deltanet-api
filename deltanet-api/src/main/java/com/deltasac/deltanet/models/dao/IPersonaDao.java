package com.deltasac.deltanet.models.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deltasac.deltanet.models.entity.Persona;

public interface IPersonaDao extends JpaRepository<Persona, Long> {
	
	public Persona findByUsername(String username);
	
	public Optional<Persona> findById(Long id);

}
