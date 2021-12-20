package com.delta.deltanet.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delta.deltanet.models.entity.Ticket;

public interface ITicketDao extends JpaRepository<Ticket, Long> {

}
