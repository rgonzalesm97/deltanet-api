package com.deltasac.deltanet.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deltasac.deltanet.models.entity.Persona;

public interface IPersonaDao extends JpaRepository<Persona, Long> {
	
	public Persona findByUsername(String username);

}
