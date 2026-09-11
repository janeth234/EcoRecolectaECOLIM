package com.senati.ecorecolecta;

/**
 * Modelo que representa un registro de recolección de residuos.
 * Se mapea directamente con la tabla "residuos" de la base de datos SQLite.
 */
public class Residuo {

    private int id;
    private String tipoResiduo;   // Ej: Papel/Cartón, Plástico, Vidrio, Orgánico, Peligroso
    private double cantidadKg;
    private String area;          // Área o ubicación dentro de la instalación del cliente
    private String observaciones;
    private String fechaHora;     // formato yyyy-MM-dd HH:mm:ss
    private String usuario;       // trabajador que realizó el registro
    private int sincronizado;     // 0 = pendiente de enviar a la API, 1 = ya sincronizado

    public Residuo() { }

    public Residuo(String tipoResiduo, double cantidadKg, String area,
                    String observaciones, String fechaHora, String usuario) {
        this.tipoResiduo = tipoResiduo;
        this.cantidadKg = cantidadKg;
        this.area = area;
        this.observaciones = observaciones;
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.sincronizado = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipoResiduo() { return tipoResiduo; }
    public void setTipoResiduo(String tipoResiduo) { this.tipoResiduo = tipoResiduo; }

    public double getCantidadKg() { return cantidadKg; }
    public void setCantidadKg(double cantidadKg) { this.cantidadKg = cantidadKg; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public int getSincronizado() { return sincronizado; }
    public void setSincronizado(int sincronizado) { this.sincronizado = sincronizado; }
}
