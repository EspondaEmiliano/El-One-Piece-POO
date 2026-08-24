package GuiaDeEjercitacion;
/*
Consigna:
Escribí una clase RegistroTemperaturas que trabaje con las temperaturas de una semana (7 días)
usando un arreglo de double. La clase debe permitir:
*/

public class RegistroTemperaturas {
    private double[] temperaturaSemana;

    // Creación del arreglo con tamaño fijo 7
    public RegistroTemperaturas() {
        temperaturaSemana = new double[7];
    }

    // 1. Cargar las 7 temperaturas del arreglo.
    public void cargarTemperaturas() {
        temperaturaSemana[0] = 26.5;
        temperaturaSemana[1] = 31.2;
        temperaturaSemana[2] = 29.8;
        temperaturaSemana[3] = 34.4;
        temperaturaSemana[4] = 28.6;
        temperaturaSemana[5] = 30.1;
        temperaturaSemana[6] = 24.5;
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

    // 5. Recorrer el arreglo usando length e imprimir cada día con su temperatura.
    public void imprimirTemperaturas() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (int i = 0; i < temperaturaSemana.length; i++) {
            System.out.println(dias[i] + ": " + temperaturaSemana[i]);
        }
    }

    static void main(String[] args) {
        RegistroTemperaturas registro = new RegistroTemperaturas();
        registro.cargarTemperaturas();
        registro.calcularPromedio();
        registro.maximaTemperatura();
        registro.minimaTemperatura();
        registro.contarDiasSuperiores(30.0);
        registro.imprimirTemperaturas();
    }

}
