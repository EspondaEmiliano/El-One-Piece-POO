package GuiaDeEjercitacion.Parte4UML;

public class Comision {
    private int numeroComision;
    private int cupoMax;
    private Aula aula;
    private int cantidadAlumnos;
    private Alumno[] alumnos;

    public Comision(int numeroComision, int cupoMax, Aula aula) {
        this.numeroComision = numeroComision;
        this.cupoMax = cupoMax;
        this.aula = aula;
        this.cantidadAlumnos = 0;
        this.alumnos = new Alumno[cupoMax];
    }

    public boolean hayCuposDisponibles() {
        return cantidadAlumnos < cupoMax;
    }

    public void registrarInscripcion(Alumno alumno) {
        for (int i = 0; i < cantidadAlumnos; i++) {
            if (alumnos[i].getLegajo() == alumno.getLegajo()) {
                System.out.println("El alumno ya está inscripto.");
                return;
            }
        }
        if (hayCuposDisponibles()) {
            alumnos[cantidadAlumnos] = alumno;
            cantidadAlumnos++;

            System.out.println("Inscripción registrada. Cantidad de alumnos: " + cantidadAlumnos);
        } else {
            System.out.println("No se pueden registrar más inscripciones. Cupo máximo alcanzado.");
        }
    }

    public int cuposDisponibles(){
        return cantidadAlumnos;
    }
}