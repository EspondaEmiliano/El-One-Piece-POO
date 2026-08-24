package EjerciciosPropuestos2;
import java.util.Scanner;
public class SumaArmonica {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int  numero;
        double suma= 0.0;
        int  i = 1;
        System.out.print("Ingrese el numero de la Armonica a Sumar: ");
        numero = scanner.nextInt();

        while (i <= numero) {
            suma += (1.0 /i);
            i++;
        }
        System.out.println("La suma armónica para n=" + numero + " es: " + suma);
        scanner.close();
    }
}
