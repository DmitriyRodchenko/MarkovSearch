import java.util.function.ToDoubleFunction;

public class ObjectiveFunction
{
    //region Задать функцию

    //Пример 1
    //f(x) = x[0]^4 + x[0]^2 + x[0] * x[1] + x[1]^2
    //N = 10000, v = 2E-24, G = 0,7;
    //N = 1000000, v = 1E-165, G = 0,7; Макс точность

    public static int dimension = 2;
    public static ToDoubleFunction<double[]> F = xi ->
            xi[0]*xi[0]*xi[0]*xi[0] + xi[0]*xi[0] + xi[0] * xi[1] + xi[1]*xi[1];

    //endregion

    //region Примеры

        //Пример 1
        //f(x) = x[0]^4 + x[0]^2 + x[0] * x[1] + x[1]^2
        //N = 10000, v = 2E-24, G = 0,7;
        //N = 1000000, v = 1E-165, G = 0,7; Макс точность

        /*
        public static int dimension = 2;
        public static ToDoubleFunction<double[]> F = xi ->
        xi[0]*xi[0]*xi[0]*xi[0] + xi[0]*xi[0] + xi[0] * xi[1] + xi[1]*xi[1];
        */

        //Пример 2
        // f(x) = 1/2 * (( x[0]^4 - 16x[0]^2 + 5x[0]) + ( x[1]^4 - 16x[1]^2 + 5x[1] ))
        //N = 10000, v = 1E-08, G = 10;
        /*
        public static int dimension = 2;
        public static ToDoubleFunction<double[]> F = xi ->
        0.5 * ((xi[0]*xi[0]*xi[0]*xi[0] - 16.0 * xi[0]*xi[0] + 5.0 * xi[0]) +
        (xi[1]*xi[1]*xi[1]*xi[1] - 16.0 * xi[1]*xi[1] + 5.0 * xi[1]));
        */

        //Пример 3
        //f(x) = x[0]^2 + x[1]^2 +...+ x[d]^2
        //N = 300000, v = 1E-16, G = 10;
        /*
        public static int dimension = 100;
        public static ToDoubleFunction<double[]> F = xi ->
        {
            double sum = 0;
            for (int i = 0; i < dimension; i++)
            {
                sum += x[i]*x[i];
            }
            return sum;
        };
        */

        //Пример 4
        //N = 10000000, v = 1E-17, G = 4;
        /*
        public static int dimension = 10;
        public static ToDoubleFunction<double[]> F = xi ->
        {
            double num;
            double sum = 0;
            for (int i = 0; i < 5; ++i)
            {
            num = xi[2 * i + 1] - xi[2 * i] * xi[2 * i];
            sum += 100 * num * num + (1 - xi[2 * i]) * (1 - xi[2 * i]);
            }
            return sum; // Функция возвращает значение
        };
        */

    //endregion
}

