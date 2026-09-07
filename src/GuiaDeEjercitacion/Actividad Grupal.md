# Actividad Grupal - UML y Java 

- **Comisión:** 579655  
- **Grupo N°:** El One Piece 
- **Integrantes:** 
     - Torres Matías 
     - Miguez Ian 
     - Esponda Emiliano
     - Villavedra Santiago
     - Alcaide Yamil 
- **Fecha:** 31-08-2026 
---
## Parte 1 - Análisis del dominio 

### Clases candidatas y responsabilidades 
|         Clase         |                          Responsabilidad                           | Tipo (dominio/servicio) |
|:---------------------:|:------------------------------------------------------------------:|:-----------------------:|
| `Persona` (abstracta) |        Datos comunes de identidad (legajo, nombre, correo)         |         Dominio         |
|       `Alumno`        |            Se inscribe en comisiones; conoce su carrera            |         Dominio         |
|       `Docente`       |               Dicta comisiones; conoce su categoría                |         Dominio         |
|       `Materia`       |           Agrupa las comisiones que ofrece en un período           |         Dominio         |
|      `Comisión`       |               Gestiona cupo, aula y sus evaluaciones               |         Dominio         |
|     `Inscripción`     |       Representa el vínculo alumno-comisión con su resultado       |         Dominio         |
|     `Evaluación`      | Instancia de evaluación (parcial, TP, etc.) dentro de una comisión |         Dominio         |
|     `Universidad`     |       Orquesta las validaciones del caso de uso "inscribir"        |        Servicio         |

### Generalización identificada 

`Persona → Alumno / Docente`. Se justifica porque ambos comparten legajo/nombre/correo, pero difieren en atributos y comportamiento (`carrera` vs `categoria`),  conviene tratarlos polimórficamente en cualquier lista de "personas registradas" sin duplicar código.

---
## Parte 2 - Diagrama de clases

![Diagrama de Clases](assets/diagrama_clases.png)

### Justificación Comisión-Evaluación 

La existencia de ``Evaluación`` está atada al ciclo de vida de `Comision`: si se borra la comisión, sus evaluaciones dejan de tener sentido.

### Justificación de multiplicidad elegida 

Docente 1.* en Comisión: refleja la regla de negocio "una comisión debe tener al menos un docente y puede tener hasta dos". No es una simple recomendación, es una restricción del modelo.

### Respuesta pregunta de control

Cada instancia de ``Comision`` tiene asignado como mínimo un docente y a lo sumo dos — nunca cero ni más de dos.

---
## Parte 3 - Diagrama de secuencia

![Diagrama de secuencia](assets/diagrama_secuencia.png)

### Matriz de trazabilidad

|             Mensaje              | Objeto receptor |             Método/responsabilidad              |
|:--------------------------------:|:---------------:|:-----------------------------------------------:|
| ``validarInscripcion(comision)`` |   Universidad   | `+validarInscripcion(comision: Comision): void` |
|         `existeComision`         |   Universidad   | `-existeComision(comision: Comision): Boolean`  |
|            `hayCupo`             |    Comision     |              `+hayCupo(): Boolean`              |
|       `estaAlumno(alumno)`       |    Comision     |     `+estaAlumno(alumno: Alumno): Boolean`      |
|      `RegistrarInscripcion`      |   Universidad   |            `-registrarInscripcion()`            |
|     <<create>>`Inscripcion`      |   Inscripcion   |     Constructor de la clase  `Inscripcion`      |


---
## Parte 4 - De modelo a Java

### A. Firmas de métodos

```java
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
```

### B. Tipos primitivos y wrappers

| Dato                      | Primitivo | Wrapper   | Por qué conviene                                                                 |
|---------------------------|-----------|-----------|----------------------------------------------------------------------------------|
| ``CupoMaximo``            | `int`     | `Integer` | Si se usa en ``List<Integer> ``genéricos                                         |
| `cantidadInscriptos`      | `int`     | `Integer` | Idem — colecciones que exigen objetos                                            |
| ``promedioFinal``         | `double`  | `Double`  | ``null`` representa "sin promedio todavía", que un 0.0 confundiría con nota real |
| ``regular``               | `boolean` | `Boolean` | Permite un tercer estado implícito ("no evaluado") con ``null``                  |

### Explicación de wrapper + ejemplo de conversión

Una clase wrapper envuelve un tipo primitivo en un objeto, para poder usarlo donde Java exige objetos (genéricos, colecciones, valores nulos). 

Ejemplo: 
```java
int cupo = 30;
Integer cupoObj = cupo; // autoboxing y a la inversa
int cupo2 = cupoObj; // unboxing
```
---
## Parte 5 - Revisión final

- [✓] Diagrama de clases con atributos, operaciones y relaciones legibles
- [✓] Multiplicidad en ambos extremos de cada asociación
- [✓] Generalización orientada de subclases a superclase
- [✓] Comisión-Evaluación representada y justificada
- [✓] Secuencia con líneas de vida, orden y alternativas de error
- [✓] Nombres consistentes entre diagramas
- [✓] Firmas Java compatibles con el modelo
- [✓] Tabla de wrappers completa
- [✓] Todos los integrantes figuran en la entrega

