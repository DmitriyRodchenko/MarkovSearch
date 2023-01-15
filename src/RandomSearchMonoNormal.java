import java.util.Random;
import java.util.function.ToDoubleFunction;

//однородный случайный поиск с нормальным распределением

class RandomSearchMonoNormal
{
    //region Переменные
        double[] xi; // Текущая точка поиска ξ
        Double fxi; // f(ξ)
        private ToDoubleFunction<double[]> objectiveFunctionF; // Целевая функция
        private Double nu; // Внутренний радиус v
        private Double gamma; // Промежуточный радиус γ. Выполняются неравенства v < γ < Г.
        private Double Gamma; // Внешний радиус Г
        private Double lambda; // λ - нормирующая константа плотности
        private boolean isUniform; // Если γ ≤ v, то моделируем равномерное распределение в шаре радиуса Г.
        private Double radiusBorder; // Граничное значение при моделировании радиуса
        private Double radiusMult; // Множитель при моделировании радиуса
        private int dimension; // Размерность пространства оптимизации
        private long numberOfSteps; // Число шагов поиска
        private double[] eta; // Новая точка η в пространстве оптимизации
        Random random; // Генератор псевдослучайных чисел

        private int dimensionMinus1; // Размерность пространства оптимизации - 1
        private boolean isDimensionOdd; // Размерность пространства оптимизации нечетна
        private Double randomValue; // Нормальная случайная величина, когда размерность пространства

        // Информация о выполнении поиска
        boolean showProgressInformation; // Показывать информацию о выполнении поиска
        long stepsIntervalForProgressInformation; // Интервал для показа шагов поиска
    //endregion

    //region Конструктор
        RandomSearchMonoNormal(int theDimension, ToDoubleFunction<double[]> theObjectiveFunctionF,
                                      double theNu, double theGamma, long theNumberOfSteps, Double[] xi0, boolean showProgressInfo,
                                      long stepsIntervalForProgressInfo, boolean generatorInitialize, int generatorSeed) {
            // Параметры поиска
            dimension = theDimension;
            dimensionMinus1 = dimension - 1;
            objectiveFunctionF = theObjectiveFunctionF;
            nu = theNu;
            Gamma = theGamma;

            // Размерность пространства оптимизации нечетна
            if (dimension % 2 == 0)
                isDimensionOdd = false;
            else
                isDimensionOdd = true;

            // Нормальная случайная величина, когда размерность пространства нечетна
            randomValue = null;

            numberOfSteps = theNumberOfSteps;

            //Информация о выполнении поиска
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

            // Вычисляем промежуточный радиус γ
            gamma = Gamma / Math.pow(2.0, 1.0 / dimension);

            // Если γ ≤ v, то моделируем нормальное распределение со стандартным отклонением Г.
            if (gamma > nu)
                isUniform = false;
            else
                isUniform = true;
            if(isUniform)
            {
                // В случае равномерного распределения вспомогательные параметры не нужны.
                // Сделаем их равными 1.
                lambda = 1.0;
                radiusBorder = 1.0;
                radiusMult = 1.0;
            }
            else
            {
                // Вычисляем вспомогательные параметры
                lambda = dimension * Math.log(gamma / nu) + 2.0;
                radiusBorder = dimension * Math.log(gamma / nu) / lambda;
                radiusMult = lambda / dimension;
            }

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
            double radius; // Моделируем радиус (стандартное отклонение)
            if (isUniform)
            {
                radius = Gamma;
            }
            else
            {
                double alpha = random.nextDouble(); // α равномерно распределена на [0, 1)
                if (alpha >= radiusBorder)
                    radius = Gamma;
                else
                    radius = nu * Math.exp(radiusMult * alpha);
            }

            // Моделируем нормальное распределение с полученным радиусом (стандартным отклонением) и центром ξ.
            // Используем модифицированный полярный метод.
            double alpha1, alpha2;
            double beta1, beta2;
            double d, t;
            for (int i = 0, iPlus1 = 1; i < dimensionMinus1; i += 2, iPlus1 += 2)
            {
                do
                {
                    alpha1 = random.nextDouble();
                    alpha2 = random.nextDouble();
                    beta1 = alpha1 + alpha1 - 1.0;
                    beta2 = alpha2 + alpha2 - 1.0;
                    d = beta1 * beta1 + beta2 * beta2;
                }
                while (d > 1.0);

                t = Math.sqrt(-2.0 * Math.log(d) / d);
                eta[i] = xi[i] + beta1 * t * radius;
                eta[iPlus1] = xi[iPlus1] + beta2 * t * radius;
            }

            // Размерность пространства оптимизации нечетна
            if (isDimensionOdd)
            {
                if (!randomValue.isNaN())
                {
                    eta[dimensionMinus1] = xi[dimensionMinus1] + randomValue.doubleValue() * radius;
                    randomValue = null;
                }
                else
                {
                    // Моделируем стандартное нормальное распределение.
                    // Используем модифицированный полярный метод.
                    do
                    {
                        alpha1 = random.nextDouble();
                        alpha2 = random.nextDouble();
                        beta1 = alpha1 + alpha1 - 1.0;
                        beta2 = alpha2 + alpha2 - 1.0;
                        d = beta1 * beta1 + beta2 * beta2;
                    }
                    while (d > 1.0);

                    t = Math.sqrt(-2.0 * Math.log(d) / d);
                    eta[dimensionMinus1] = xi[dimensionMinus1] + beta1 * t * radius;
                    randomValue = beta2 * t;
                }
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