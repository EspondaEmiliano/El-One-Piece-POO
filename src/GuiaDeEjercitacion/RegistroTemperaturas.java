package GuiaDeEjercitacion;

import java.util.Scanner;

public class RegistroTemperaturas {
    private double[] temperaturaSemana;

    public RegistroTemperaturas() {
        temperaturaSemana = new double[7];
    }

    public void registrarTemperatura(double temperatura) {
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < 7; i++) {
            System.out.print("Ingrese la temperatura del día " + (i + 1) + ": ");
            temperaturaSemana[i] = sc.nextDouble();
        }
    }

}
