# Guía de Trabajo Colaborativo y Flujo de Git

Este documento define las reglas de trabajo en equipo para el desarrollo de las actividades prácticas en Programación Orientada a Objetos en este repositorio.

---

## 1. Reglas

1. **La rama `main` no se toca directamente:** Queda prohibido hacer `git push` directamente a `main`. Todo cambio entra únicamente mediante Pull Request (PR) aprobado.
2. **Una rama por tarea o consigna:** Cada integrante trabaja en una rama aislada creada a partir de la última versión de `main`.
3. **Actualizar antes de empezar y antes de entregar:** Siempre sincronizar la copia local con el repositorio remoto antes de escribir código nuevo y antes de abrir un PR.
4. **Respetar la configuración de archivos ignorados:** El archivo `.gitignore` del proyecto ya omite archivos y directorios de entorno (`.idea`, `out`, `*.iml`). Nunca se deben versionar archivos binarios (`.class`) ni configuraciones personales del IDE.
5. **No usar `--force`:** Está prohibido el uso de `git push --force` en ramas compartidas.

---

## 2. Convención de Ramas

Las ramas se deben nombrar siguiendo este patrón:

- ``feature/<tema-o-ejercicio>``: Para el desarrollo de un ejercicio o una nueva funcionalidad (ej. `feature/bisiesto`, `feature/uml-comision`).
- ``fix/<problema>``: Para corregir errores en soluciones ya integradas a `main`.
- ``docs/<descripcion>``: Para redactar o actualizar documentación en archivos `.md`.

---

## 3. Flujo de Trabajo Paso a Paso

### Paso 1: Obtener la última version de ``main``

Antes de comenzar a trabajar:
````bash
git checkout main
git pull origin main
````

### Paso 2: Crear la rama de trabajo

````bash
git checkout -b feature/nombre-del-ejercicio
````

### Paso 3: Desarrollar y verificar localmente

- Escribir el código en la carpeta correspondiente dentro de `src`.
- Compilar y ejecutar las pruebas para asegurarse de que el programa funciona y no rompe dependencias.
- Verificar qué archivos fueron modificados:
   ````bash
   git status 
   ````

### Paso 4: Guardar los cambios (Commits)

Hacer commits pequeños y descriptivos siguiendo el estándar de mensajes:
- ``feat: agrega validación de anio bisiesto``.
- `fix: corrige condicion de parada en suma armonica`
- `docs: completa preguntas teoricas de la guia grupal`

comando:
   ````bash
  git add ruta/al/archivo/Modificado.java
  git commit -m "feat: implementa metodo calcularPromedio en RegistroTemperaturas"
   ````

### Paso 5: Sincronizar con ``main`` antes de subir

Mientras trabajas, otro compañero pudo haber integrado cambios en ``main``. Incorpóralos a tu rama:
````bash
git checkout main
git pull origin main
git checkout feature/nombre-del-ejercicio
git merge main
````

*Si surgen conflictos, deben resolverse en este paso antes de continuar*

### Paso 6: Publicar la rama en el repositorio remoto

````bash
git push -u origin feature/nombre-del-ejercicio
````

### Paso 7: Abrir Pull Request y Revisión de Pares

1. Ir a la plataforma del repositorio (GitHub) y abrir un Pull Request hacia `main`.
2. Asignar a al menos un compañero del grupo como revisor.
3. El revisor debe comprobar que:
    - El código compila sin errores.
    - Sigue las pautas de POO de la cátedra.
    - No sube archivos innecesarios ni borra trabajo previo.
4. Una vez aprobado, se realiza el **Merge** a `main` y se elimina la rama remota.

---

## 4. División de Tareas y Prevención de Conflictos

Para evitar colisiones en los mismos archivos:

- **Modularidad por archivo:** Cuando se trabaje en ejercicios con varias clases (como el paquete `Parte4UML` con `Alumno.java`, `Aula.java` y `Comision.java`), cada integrante debe tomar la responsabilidad inicial de clases diferentes.
- **Documentación compartida:** En archivos como `Resolucion.md` o `Actividad Grupal.md`, definir previamente qué punto responde cada uno para no editar los mismos párrafos al mismo tiempo.

---

## 5. Protocolo para Resolución de Conflictos

Si al hacer `git merge main` Git indica que hay conflictos (`CONFLICT (content)`):

1. **Identificar los archivos:** Ejecutar `git status` para ver los archivos marcados como *both modified*.
2. **Abrir el archivo:** Buscar los delimitadores generados por Git:
   ````text
   <<<<<<< HEAD (tus cambios en la rama actual)
   codigo_version_A();
   =======
   codigo_version_B();
   >>>>>>> main (cambios que vienen de main)
   ````
3. **Coordinar:** Hablar con el autor de las líneas en conflicto para decidir cuál versión conservar o cómo combinarlas.
4. **Limpiar y probar:** Eliminar los delimitadores (`<<<<<<<`, `=======`, `>>>>>>>`), guardar el archivo y compilar para validar que el proyecto funciona.
5. **Finalizar el merge:**

   ````bash
   git add <archivo-resuelto>
   git commit -m "merge: resuelve conflictos con main en <nombre-archivo>"
   ````
   
---

## 6. Comandos Rápidos de Referencia

| Acción                                      | Comando                           |
|---------------------------------------------|-----------------------------------|
| Ver estado de los archivos                  | `git status`                      |
| Ver diferencias entes de commitear          | `git diff`                        |
| Cambiar a una rama existente                | `git checkout <nombre-rama>`      |
| Crear y pasar a una rama nueva              | `git checkout -b <nombre-rama>`   |
| Traer últimos cambios de remoto             | `git pull origin <rama>`          |
| Deshacer cambios no guardados en un archivo | `git restore <archivo>`           |
| Ver historial simplificado                  | `git log --oneline --graph --all` |