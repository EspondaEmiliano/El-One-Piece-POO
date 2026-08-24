package EjerciciosPropuestos3;

import java.util.Scanner;
public class ContarDigitos {
    public static void main(String [] args){
        int cantidad = 0,numero;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese un numero: ");
        numero = scanner.nextInt();

        if (numero == 0){
            cantidad = 1;
        } else{
            do {
                numero = numero / 10;
                cantidad++;
            }while(numero > 0);
        }
        System.out.println("El numero tiene "+cantidad+" digitos");
        scanner.close();
    }
}
