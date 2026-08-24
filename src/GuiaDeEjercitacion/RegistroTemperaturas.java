package GuiaDeEjercitacion;

//Consigna:
// Escribí una clase RegistroTemperaturas que trabaje con las temperaturas de una semana (7 días)
// usando un arreglo de double. La clase debe permitir:

import java.util.Scanner;

public class RegistroTemperaturas {
    private double[] temperaturaSemana;

    public RegistroTemperaturas() {
        temperaturaSemana = new double[7];
    }

    // 1. Cargar las 7 temperaturas del arreglo.
    public void cargarTemperatura() {
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < 7; i++) {
            System.out.print("Ingrese la temperatura del día " + (i + 1) + ": ");
            temperaturaSemana[i] = sc.nextDouble();
        }
    }

    // 2. Calcular e imprimir el promedio de la semana
    public void calcularPromedio() {
        double suma = 0;
        double promedio;
        for (int i = 0; i < temperaturaSemana.length; i++) {
            suma += temperaturaSemana[i];
        }
        promedio = suma / temperaturaSemana.length;
        System.out.println("El promedio de temperaturas es: " + promedio);
    }

    // 3. Encontrar la temperatura máxima y la mínima.
    public void maximaTemperatura() {
        double max = temperaturaSemana[0];
        for (int i = 1; i < temperaturaSemana.length; i++) {
            if (temperaturaSemana[i] > max) {
                max = temperaturaSemana[i];
            }
        }
        System.out.println("La máxima temperatura es: " + max);
    }

    public void minimaTemperatura() {
        double min = temperaturaSemana[0];
        for (int i = 1; i < temperaturaSemana.length; i++) {
            if (temperaturaSemana[i] < min) {
                min = temperaturaSemana[i];
            }
        }
        System.out.println("La mínima temperatura es: " + min);
    }

    // 4. Contar cuántos días superaron un umbral dado (por ejemplo, 30.0 grados).
    public void contarDiasSuperiores(double umbral) {
        int contador = 0;
        for (int i = 0; i < temperaturaSemana.length; i++) {
            double temp = temperaturaSemana[i];
            if (temp > umbral) {
                contador++;
            }
        }
        System.out.println("Días que superaron el umbral de " + umbral + ": " + contador);
    }


}
