import java.util.Random;
import java.util.function.ToDoubleFunction;

//однородный  случайный поиск с распределением Ингбера

class RandomSearchIngber
{
    //region Переменные
        double[] xi; // Текущая точка поиска ξ
        Double fxi; // f(ξ)
        private ToDoubleFunction<double[]> objectiveFunctionF; // Целевая функция
        private Double nu; // Внутренний радиус v
        private Double Gamma; // Внешний радиус Г
        private Double lambda; // λ - нормирующая константа плотности
        private Double radiusMult; // Множитель при моделировании радиуса
        private int dimension; // Размерность пространства оптимизации
        private long numberOfSteps; // Число шагов поиска
        private double[] eta; // Новая точка η в пространстве оптимизации
        Random random; // Генератор псевдослучайных чисел

        // Информация о выполнении поиска
        boolean showProgressInformation; // Показывать информацию о выполнении поиска
        long stepsIntervalForProgressInformation; // Интервал для показа шагов поиска
    //endregion

    //region Конструктор
        RandomSearchIngber (int theDimension, ToDoubleFunction<double[]> theObjectiveFunctionF, Double theNu, Double theGamma,
                                  long theNumberOfSteps, Double[] xi0, boolean showProgressInfo,
                                  long stepsIntervalForProgressInfo, boolean generatorInitialize, int generatorSeed) {
            // Параметры поиска
            dimension = theDimension;
            objectiveFunctionF = theObjectiveFunctionF;
            nu = theNu;
            Gamma = theGamma;
            numberOfSteps = theNumberOfSteps;

            // Информация о выполнении поиска
            stepsIntervalForProgressInformation = stepsIntervalForProgressInfo;
            showProgressInformation = showProgressInfo;
            if (numberOfSteps < stepsIntervalForProgressInfo)
                showProgressInformation = false;

            // Точки поиска
            xi = new double[dimension]; // Текущая точка поиска ξ
            eta = new double[dimension]; // Новая точка η в пространстве оптимизации

            // Задать начальную точку поиска ξ
            for (int i = 0; i < dimension; ++i)
                xi[i] = xi0[i];
            fxi = objectiveFunctionF.applyAsDouble(xi); // f(ξ)

            // Вычисляем вспомогательные параметры
            lambda = 1.0 + 1.0 / nu;
            radiusMult = nu * Gamma;

            // Инициализация генератора псевдослучайных чисел
            if (!generatorInitialize)
                random = new Random();
            else
                random = new Random(generatorSeed);
        }
    //endregion

    //region Методы
        // Получение новой точки η в пространстве оптимизации
        private void etaSimulation() {
            double alpha; // α равномерно распределена на [0, 1)
            double degree; // Степень

            // Моделируем распределение Ингбера.
            for (int i = 0; i < dimension; ++i)
            {
                alpha = random.nextDouble(); // α равномерно распределена на [0, 1)
                degree = Math.abs(alpha + alpha - 1.0);
                if (alpha > 0.5)
                    eta[i] = xi[i] + radiusMult * (Math.pow(lambda, degree) - 1.0);
                else
                    eta[i] = xi[i] - radiusMult * (Math.pow(lambda, degree) - 1.0);
            }
        }

        // Выполняем случайный поиск
        void searchSimulation() {
            for (long n = 1; n <= numberOfSteps; ++n)
            {
                //Информация о выполнении поиска
                if (showProgressInformation && (n % stepsIntervalForProgressInformation == 0))
                {
                    int percent = (int)((double) n / numberOfSteps * 100.0);
                    GUI.textFieldProgressInfo.setText("Выполняется шаг " + n +"  (" + percent + "%)");
                    GUI.textFieldProgressInfo.requestFocus();
                }

                etaSimulation(); // Получаем новую точку η в пространстве оптимизации

                double feta = objectiveFunctionF.applyAsDouble(eta); // f(η)
                // Если f(η) ≤ f(ξ), то поиск переходит в точку η
                if (feta <= fxi) // Если f(η) ≤ f(ξ)
                {
                    // Поиск переходит в точку η
                    fxi = feta;
                    for (int i = 0; i < dimension; ++i)
                        xi[i] = eta[i];
                }
            }
        }
    //endregion
}