package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Ticket;

public interface ITicketDao extends JpaRepository<Ticket, Long> {

	@Query("from Ticket where usuarioServicio.id = ?1")
	public List<Ticket> findAllByUsuarioServicio(Long idUsuarioServicio);
}
