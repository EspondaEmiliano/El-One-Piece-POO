package EjerciciosPropuestos4;
import java.util.Scanner;

public class CalcularFactorial {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numero;
        long factorial = 1;
        System.out.print("Ingrese el numero para calcular factorial: ");
        numero = scanner.nextInt();

        for (int i = 1; i <= numero; i++) {
            factorial = factorial * i;
        }
        System.out.println("El factorial de " + numero + "! es: " + factorial);
    }
}
