package com.delta.deltanet.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delta.deltanet.models.entity.Archivo;

public interface IArchivoDao extends JpaRepository<Archivo, Long> {

}
