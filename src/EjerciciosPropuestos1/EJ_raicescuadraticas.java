package EjerciciosPropuestos1;

import java.util.Scanner;

public class EJ_raicescuadraticas {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a;
        int b;
        int c;
        System.out.print("Ingrese el valor de a: ");
        a = sc.nextInt();
        System.out.print("Ingrese el valor de b: ");
        b = sc.nextInt();
        System.out.print("Ingrese el valor de c: ");
        c = sc.nextInt();
        // Calcular las raíces de la ecuación cuadrática
        double discriminante = b*b - 4*a*c;
        if (discriminante < 0) {
            System.out.println("La ecuación no tiene raíces reales.");
        } else if (discriminante == 0) {
            double raiz = (double) -b / (2*a);
            System.out.println("La ecuación tiene una raíz real: " + raiz);
        } else {
            double raiz1 = (-b + Math.sqrt(discriminante)) / (2*a);
            double raiz2 = (-b - Math.sqrt(discriminante)) / (2*a);
            System.out.println("La ecuación tiene dos raíces reales: " + raiz1 + " y " + raiz2);
        }
    }
}
