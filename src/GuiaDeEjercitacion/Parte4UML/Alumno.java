package GuiaDeEjercitacion.Parte4UML;

public class Alumno {
    private int legajo;
    private String nombre;
    private String correo;
    private String carrera;

    public Alumno(int legajo, String nombre, String correo, String carrera) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.correo = correo;
        this.carrera = carrera;
    }

    public int getLegajo() {
        return legajo;
    }

    public void setLegajo(int legajo) {
        this.legajo = legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }
}
