package com.deltasac.deltanet.models.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deltasac.deltanet.models.entity.Area;
import com.deltasac.deltanet.models.entity.Persona;
import com.deltasac.deltanet.models.entity.Role;

public interface IPersonaDao extends JpaRepository<Persona, Long> {
	
	public Persona findByUsername(String username);
	
	public Optional<Persona> findById(Long id);
	
	@Query("from Role")
	public List<Role> findAllRoles();

}
