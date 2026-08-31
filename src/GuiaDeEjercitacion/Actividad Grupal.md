# Actividad grupal - UML y Java 

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
| :-------------------: | :----------------------------------------------------------------: | :---------------------: |
| `Persona` (abstracta) |        Datos comunes de identidad (legajo, nombre, correo)         |         Dominio         |
|       `Alumno`        |            Se inscribe en comisiones; conoce su carrera            |         Dominio         |
|       `Docente`       |               Dicta comisiones; conoce su categoría                |         Dominio         |
|       `Materia`       |           Agrupa las comisiones que ofrece en un período           |         Dominio         |
|      `Comisión`       |               Gestiona cupo, aula y sus evaluaciones               |         Dominio         |
|     `Inscripción`     |       Representa el vínculo alumno-comisión con su resultado       |         Dominio         |
|     `Evaluación`      | Instancia de evaluación (parcial, TP, etc.) dentro de una comisión |         Dominio         |
|                       |                                                                    |                         |
### Generalización identificada 

`Persona → Alumno / Docente`. Se justifica porque ambos comparten legajo/nombre/correo pero difieren en atributos y comportamiento (`carrera` vs `categoria`),  conviene tratarlos polimórficamente en cualquier lista de "personas registradas" sin duplicar código.

---
## Parte 2 - Diagrama de clases
![Diagrama de Clases](assets/diagrama_clases.png)
### Justificación

### Justificación Comisión-Evaluación 
### Justificación de multiplicidad elegida 
### Respuesta pregunta de control

---
## Parte 3 - Diagrama de secuencia

![Diagrama de secuencia](assets/diagrama_secuencia.png)

### Matriz de trazabilidad

| Mensaje | Objeto receptor | Método/responsabilidad |
| :-----: | :-------------: | :--------------------: |
|         |                 |                        |

---
## Parte 4 - De modelo a Java

### A. Firmas de métodos

```java

```

### B. Tipos primitivos y wrappers

| Dato | Primitivo | Wrapper | Por qué conviene |
| :--: | :-------: | :-----: | :--------------: |
|      |           |         |                  |

### Explicación de wrapper + ejemplo de conversión

---
## Parte 5 - Revisión final

- [ ] Diagrama de clases con atributos, operaciones y relaciones legibles
- [ ] Multiplicidad en ambos extremos de cada asociación
- [ ] Generalización orientada de subclases a superclase
- [ ] Comisión-Evaluación representada y justificada
- [ ] Secuencia con líneas de vida, orden y alternativas de error
- [ ] Nombres consistentes entre diagramas
- [ ] Firmas Java compatibles con el modelo
- [ ] Tabla de wrappers completa
- [ ] Todos los integrantes figuran en la entrega

