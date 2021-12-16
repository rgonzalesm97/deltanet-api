package com.delta.deltanet.models.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sun.istack.NotNull;

@Entity
@Table(name="archivo")
public class Archivo implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "tabla", length = 100, nullable = false)
	private String tabla;
	
	@Column(name = "tabla_id", nullable = false)
	private Long tablaId;
	
	@Column(name = "nombre", length = 250, nullable = false)
	private String nombre;
	
	@Column(name = "url", columnDefinition = "text", nullable = false)
	@Lob
	private String url;
	

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTabla() {
		return tabla;
	}


	public void setTabla(String tabla) {
		this.tabla = tabla;
	}


	public Long getTablaId() {
		return tablaId;
	}


	public void setTablaId(Long tablaId) {
		this.tablaId = tablaId;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	public String toString() {
		return "Archivo [id=" + id + ", tabla=" + tabla + ", tablaId=" + tablaId + ", nombre=" + nombre + ", url=" + url
				+ "]";
	}



	private static final long serialVersionUID = 1L;

}
