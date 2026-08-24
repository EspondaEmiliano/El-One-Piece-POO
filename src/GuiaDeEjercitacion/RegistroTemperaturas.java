package GuiaDeEjercitacion;

import java.util.Scanner;

public class RegistroTemperaturas {
    private double[] temperaturaSemana;

    public RegistroTemperaturas() {
        temperaturaSemana = new double[7];
    }

    public void cargarTemperatura() {
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < 7; i++) {
            System.out.print("Ingrese la temperatura del día " + (i + 1) + ": ");
            temperaturaSemana[i] = sc.nextDouble();
        }
    }

    public void calcularPromedio() {
        double suma = 0;
        double promedio;
        for (int i = 0; i < temperaturaSemana.length; i++) {
            suma += temperaturaSemana[i];
        }
        promedio = suma / temperaturaSemana.length;
        System.out.println("El promedio de temperaturas es: " + promedio);
    }

}
