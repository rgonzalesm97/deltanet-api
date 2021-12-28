package com.delta.deltanet.models.service;

import java.util.List;

import com.delta.deltanet.models.entity.Ticket;

public interface ITicketService {
	
	public List<Ticket> findAll();
	public Ticket findById(Long id);
	public Ticket save(Ticket ticket);
	public void delete(Long id);
	public List<Ticket> findAllByUsuarioServicio(Long id);
	
}
