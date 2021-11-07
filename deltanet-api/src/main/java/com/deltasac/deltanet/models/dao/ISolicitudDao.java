package com.deltasac.deltanet.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deltasac.deltanet.models.entity.Solicitud;

public interface ISolicitudDao extends JpaRepository<Solicitud, Long> {

}
