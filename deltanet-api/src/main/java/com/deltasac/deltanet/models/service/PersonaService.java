package com.deltasac.deltanet.models.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deltasac.deltanet.models.dao.IPersonaDao;
import com.deltasac.deltanet.models.entity.Persona;

@Service
public class PersonaService implements IPersonaService, UserDetailsService {
	
	private Logger logger = LoggerFactory.getLogger(PersonaService.class);
	
	@Autowired
	private IPersonaDao personaDao;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Persona persona = personaDao.findByUsername(username);
		String mensaje = "Error en el login: no existe el usuario '\" + username + \"' en el sistema";
		
		if (persona == null) {
			logger.error(mensaje);
			throw new UsernameNotFoundException(mensaje);
		}
		
		List<GrantedAuthority> authorities = persona.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				.peek(authority -> logger.info("Role: " + authority.getAuthority()))
				.collect(Collectors.toList());
		
		return new User(persona.getUsername(),persona.getPassword(),persona.getEnabled(),true,true,true,authorities);
	}

	@Override
	@Transactional(readOnly = true)
	public Persona findByUsername(String username) {
		return personaDao.findByUsername(username);
	}

}
