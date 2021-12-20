package com.delta.deltanet.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delta.deltanet.models.dao.ITicketDao;
import com.delta.deltanet.models.entity.Ticket;

@Service
public class TicketServiceImpl implements ITicketService {
	
	@Autowired
	private ITicketDao ticketDao;

	@Override
	public List<Ticket> findAll() {
		return ticketDao.findAll();
	}

	@Override
	public Ticket findById(Long id) {
		return ticketDao.findById(id).get();
	}

	@Override
	public Ticket save(Ticket Ticket) {
		return ticketDao.save(Ticket);
	}

	@Override
	public void delte(Long id) {
		ticketDao.deleteById(id);
	}
	
}
