# Guía de ejercitación

---

## Bloque 1 - La Java Virtual Machine — JDK, JRE, JVM, bytecode y Classpath

### Ejercicio 1.1 · Opción múltiple

1. **b)** El JDK, porque incluye a javac y demás herramientas.
2. **b)** .class
3. **b)** La JVM (Java Virtual Machine).
4. **b)** El mismo bytecode lo ejecuta la JVM de cada plataforma.
5. **b)** Una variable de entorno del SO que indica dónde buscar clases y librerías.

### Ejercicio 1.2 · Verdadero o Falso

* **V** - La JVM es parte del JRE.
* **V** - El JDK incluye al JRE.
* **F** - En Java se compila un binario distinto para cada sistema operativo. *(Corrección: Se compila a un código intermedio llamado bytecode que es igual para todos los sistemas).*
* **V** - El compilador de Java se llama javac.
* **F** - El bytecode se ejecuta directamente sobre el hardware, sin intermediarios. *(Corrección: El bytecode se ejecuta a través de la JVM, que sirve de intermediaria con el hardware).*
* **V** - El Garbage Collector se encarga de liberar la memoria que ya no se usa.

 ### Ejercicio 1.3 · Relacionar columnas

* **JDK** -> Kit de desarrollo: compilador, generador de documentación y herramientas.
* **JRE** -> Entorno de ejecución: lo mínimo para correr un programa Java ya compilado.
* **JVM** -> Máquina virtual que ejecuta el bytecode sobre cada plataforma.
* **javac** -> Programa que compila el código fuente a bytecode.
* **.class** -> Archivo que contiene el bytecode generado.
* **Classpath** -> Variable de entorno donde se buscan las clases y librerías.

### Ejercicio 1.4 · Ordenar el proceso

1. Archivo fuente (.java)
2. Compilador (javac)
3. Bytecode (.class)
4. JVM (ClassLoader, JIT, ejecución)
5. Sistema Operativo
6. Hardware

### Ejercicio 1.5: Respuesta breve
1. **"Write once, run anywhere":** Se dice esto porque en Java el código no se compila directamente al lenguaje de la máquina local. En su lugar, se compila a un lenguaje intermedio llamado "bytecode". Luego, cualquier sistema operativo que tenga instalada una JVM (Java Virtual Machine) puede interpretar y ejecutar este bytecode. A diferencia de lenguajes como C, donde el código fuente se debe volver a compilar específicamente para cada sistema operativo o arquitectura de hardware, en Java basta con compilarlo una vez.
2. **Problema de CLASSPATH mal configurado:** Si la variable no está bien configurada, la JVM o el compilador no sabrán dónde buscar las clases o librerías de las que depende el programa, lo que generará errores (como `ClassNotFoundException` o `NoClassDefFoundError`) e impedirá la ejecución o compilación.

---
## Bloque 2 Estructura de un programa y convenciones de nomenclatura

### Ejercicio 2.1 · Nombres autoexplicativos

* `int x;` -> `int edadUsuario;` (Representa la edad de una persona)
* `double d;` -> `double sensorTemperatura;` (Representa el valor que detecta un sensor de temperatura)
* `boolean flag;` -> `boolean estaActivo;`

### Ejercicio 2.2 · camelCase vs PascalCase

* **clase:** `cuenta_bancaria` -> `CuentaBancaria`
* **variable:** `SaldoTotal` -> `saldoTotal`
* **método:** `Calcular Interes` -> `calcularInteres`
* **clase:** `factura` -> `Factura`
* **variable:** `Nombre_Cliente` -> `nombreCliente`

### Ejercicio 2.3: Palabras reservadas

* `int public = 10;` -> **NO compila**. `public` es una palabra reservada del lenguaje (modificador de acceso) y no puede usarse como nombre de variable.
* `int contador = 0` -> **NO compila**. Falta el punto y coma `;` al final de la línea.
* `double for = 3.14;` -> **NO compila**. `for` es una palabra reservada (estructura de control) y no puede usarse como identificador.
* `String nombre = "Ana";` -> **COMPILA CORRECTAMENTE**.
* `boolean int = true;` -> **NO compila**. `int` es una palabra reservada (tipo de dato primitivo).

### Ejercicio 2.4: Estructura básica

```java
class Hola {
    public static void main(String[] args) {
        System.out.println("Hola mundo");
    }
}
```

### Ejercicio 2.5: Comentarios

```java
/**
 * Clase que representa un Circulo geométrico.
 * Proporciona el radio y calcula el área del mismo.
 */
public class Circulo {
    double radio = 5;
    
    // Calcula el área utilizando la aproximación de Pi (3.14) por el radio al cuadrado.
    double area = 3.14 * radio * radio; 
}
```
---
## Bloque 3 Arreglos — el arreglo como objeto, posiciones, acceso y recorrido

### Ejercicio 3.1 · Declaración
1. Un arreglo de 8 números enteros `int [] arregloNumeros = new int[8];`.
2. Un arreglo de 5 objetos de tipo Producto `Producto [] productos = new Producto[5];`.
3. Un arreglo de 3 valores booleanos `boolean [] valoresBooleanos = new boolean[3];`.

### Ejercicio 3.2 · ¿Qué imprime?
* **Fragmento 1:**
```java
int[] valores = new int[4];
valores[1] = 10;
valores[3] = 25;
for (int i = 0; i < valores.length; i++) {
    System.out.println(valores[i]);
}
```
* **Fragmento 2:**
````java
String[] nombres = new String[3];
nombres[0] = "Ana";
for (int i = 0; i < nombres.length; i++) {
    System.out.println(nombres[i]);
}
````
### Ejercicio 3.3 · Detectá el error
````java
int[] a = new int[5]; a[5] = 3;
System.out.println(a.length());
Auto[] autos = new Auto[2]; autos[0].acelerar();

````
### Ejercicio 3.4 · Escribí el código

Dado `int[]` números ya cargado, escribí el fragmento que resuelve cada punto:

````java
int [] numeros;
// 1. Sumar todos los elementos e imprimir el total
int suma = 0;
for(int i = 0; i < numeros.lenght; i++){
    suma += numeros[i];
}

// 2) Encontrar e imprimir el valor máximo
int maximo = numeros[0];
for (int i = 1; i < numeros.length; i++) {
        if (numeros[i] > maximo) {
            maximo = numeros[i];
        }
}

System.out.println("Valor máximo: " + maximo);

// 3) Contar cuántos elementos son mayores a 10
int mayoresADiez = 0;
for (int i = 0; i < numeros.length; i++) {
        if (numeros[i] > 10) {
            mayoresADiez++;
        }
}

System.out.println("Mayores a 10: " + mayoresADiez);
````


### Ejercicio 3.5 · Primitivos vs. referencia

1. Indicá el valor por defecto de cada posición recién creado el arreglo: `int[], double[], boolean[], String[].`
2. En un arreglo de objetos, ¿qué guarda realmente cada posición: el objeto o una referencia?. Explicá.

---
## Bloque 4 - Registro de temperaturas

### Consigna
Escribí una clase RegistroTemperaturas que trabaje con las temperaturas de una semana (7 días) usando un arreglo de double. 

La clase debe permitir:

1. Cargar las 7 temperaturas del arreglo.
2. Calcular e imprimir el promedio de la semana.
3. Encontrar la temperatura máxima y la mínima.
4. Contar cuántos días superaron un umbral dado (por ejemplo, 30.0 grados).
5. Recorrer el arreglo usando length e imprimir cada día con su temperatura.

### Requisitos de calidad
* Nombres autoexplicativos y pronunciables; clase en PascalCase, variables y métodos en camelCase.
* Ningún identificador que sea palabra reservada.
* Al menos un comentario de bloque (descripción de la clase) y comentarios de línea donde aporten.
* Todo el recorrido de arreglos debe usar length (no números fijos).