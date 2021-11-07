package com.deltasac.deltanet.models.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ges_peticion")
public class Solicitud implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idpeticion")
	private Long id;

	private String titulo;

	@Column(name = "despeticion")
	private String desTitulo;

	private String estado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDesTitulo() {
		return desTitulo;
	}

	public void setDesTitulo(String desTitulo) {
		this.desTitulo = desTitulo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	private static final long serialVersionUID = 1L;

}
