package com.delta.deltanet.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delta.deltanet.models.entity.Comentario;

public interface IComentarioDao extends JpaRepository<Comentario, Long> {

	@Query("from Comentario where ticket.id = ?1")
	public List<Comentario> findAllByTicket(Long idTicket);
}
