package com.deltasac.deltanet.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ges_peticion")
public class Solicitud implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idpeticion")
	private Long id;

	@NotEmpty
	@Column(nullable = false)
	private String titulo;

	@NotEmpty
	@Column(name = "despeticion", nullable = false)
	private String desTitulo;

	@Column(name = "fecCreacion")
	@Temporal(TemporalType.DATE)
	private Date createAt;

	private String imgsol;

	// Muchas solicitudes asignadas a una sola area
	@NotNull(message = "el area no puede estar vacía")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Area area;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private EstadoSolic estadoSolic;

	@Column(name = "id_crea")
	private Integer idcrea;

	@Column(name = "id_asignado", nullable = true)
	private Integer idasignado;

	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}

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

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getImgsol() {
		return imgsol;
	}

	public void setImgsol(String imgsol) {
		this.imgsol = imgsol;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Integer getIdcrea() {
		return idcrea;
	}

	public void setIdcrea(Integer idcrea) {
		this.idcrea = idcrea;
	}

	public Integer getIdasignado() {
		return idasignado;
	}

	public void setIdasignado(Integer idasignado) {
		this.idasignado = idasignado;
	}

	public EstadoSolic getEstadoSolic() {
		return estadoSolic;
	}

	public void setEstadoSolic(EstadoSolic estadoSolic) {
		this.estadoSolic = estadoSolic;
	}

	@Override
	public String toString() {
		return "Solicitud [id=" + id + ", titulo=" + titulo + ", desTitulo=" + desTitulo 
				+ ", createAt=" + createAt + ", imgsol=" + imgsol + ", area=" + area + ", idcrea=" + idcrea
				+ ", idasignado=" + idasignado + "]";
	}

	private static final long serialVersionUID = 1L;

}
