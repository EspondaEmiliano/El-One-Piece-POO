package EjerciciosPropuestos3;

import java.util.Scanner;
public class ContarNotas {
    public static void main(String [] args){
        Scanner scanner = new Scanner(System.in);
        int contador = 0;
        double nota;

        System.out.println("Ingrese las 5 notas finales:");

        for (int i = 1; i <= 5; i++){
            System.out.print("Nota " + i + ": ");
            nota = scanner.nextDouble();

            if (nota >= 3.0) {
                contador++;
            }
        }
        System.out.println("Hay "+contador+" notas mayores o iguales a 3.0");
        scanner.close();
    }
}
