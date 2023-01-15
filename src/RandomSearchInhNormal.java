import java.util.Random;
import java.util.function.ToDoubleFunction;

//неоднородный  случайный поиск с нормальным распределением

// Простой алгоритм неоднородного поиска.
// Переходными функциями поиска служат распределения гауссовского случайного вектора
// с независимыми компонентами и одинаковым стандартным отклонением.

class RandomSearchInhNormal
{
    //region Переменные
        double[] xi; // Текущая точка поиска ξ
        double fxi; // f(ξ)
        private ToDoubleFunction<double[]> objectiveFunctionF; // Целевая функция
        private double nu; // Конечное стандартное отклонение v
        private double Gamma; // Начальное стандартное отклонение Г
        private double radiusMult; // Множитель при вычислении стандартного отклонения (Коэффициент сжатия)
        private int dimension; // Размерность пространства оптимизации
        private int dimensionMinus1; // Размерность пространства оптимизации - 1
        private long numberOfSteps; // Число шагов поиска
        private long numberOfStageSteps; // Число шагов поиска на этапе
        private long numberOfStages; // Количество этапов поиска
        private double[] eta; // Новая точка η в пространстве оптимизации
        private boolean isDimensionOdd; // Размерность пространства оптимизации нечетна
        private Double randomValue; // Нормальная случайная величина, когда размерность пространства нечетна
        Random random; // Генератор псевдослучайных чисел

        // Информация о выполнении поиска
        boolean showProgressInformation; // Показывать информацию о выполнении поиска
        long stepsIntervalForProgressInformation; // Интервал для показа шагов поиска
    //endregion

    //region Конструктор
        RandomSearchInhNormal(int theDimension, ToDoubleFunction<double[]> theObjectiveFunctionF,
                                        double theNu, double theGamma, long theNumberOfSteps, long theNumberOfStageSteps, Double[] xi0, boolean showProgressInfo,
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

               numberOfStageSteps = theNumberOfStageSteps;
               if (numberOfStageSteps == 0)
                    numberOfStageSteps = 1;

                if (numberOfSteps % numberOfStageSteps == 0)
                    numberOfStages = numberOfSteps / numberOfStageSteps;
                else
                    numberOfStages = numberOfSteps / numberOfStageSteps + 1;

                if (numberOfStages > 1)
                    radiusMult = Math.pow(nu / Gamma, 1.0 / (numberOfStages - 1));
                else
                    radiusMult = 1.0;

                // Точки поиска
                xi = new double[dimension]; // Текущая точка поиска ξ
                eta = new double[dimension]; // Новая точка η в пространстве оптимизации

                // Задать начальную точку поиска ξ
                for (int i = 0; i < dimension; ++i)
                    xi[i] = xi0[i];
                fxi = objectiveFunctionF.applyAsDouble(xi); // f(ξ)
                // Инициализация генератора псевдослучайных чисел
                if (!generatorInitialize)
                    random = new Random();
                else
                    random = new Random(generatorSeed);

                // Информация о выполнении поиска
                stepsIntervalForProgressInformation = stepsIntervalForProgressInfo;
                showProgressInformation = showProgressInfo;
                if (numberOfSteps < stepsIntervalForProgressInfo)
                    showProgressInformation = false;
        }
    //endregion

    //region Метод
        // Выполняем случайный поиск
        void searchSimulation() {
            double radius = Gamma; // Стандартное отклонение

            // Поиск. Выполняется numberOfSteps шагов поиска
            for (long n = 1; n <= numberOfSteps; ++n)
            {
                //Информация о выполнении поиска
                if (showProgressInformation && (n % stepsIntervalForProgressInformation == 0))
                {
                    int percent = (int)((double) n / numberOfSteps * 100.0);
                    GUI.textFieldProgressInfo.setText("Выполняется шаг " + n +"  (" + percent + "%)");
                    GUI.textFieldProgressInfo.requestFocus();
                }

                // Получаем новую точку η в пространстве оптимизации
                // Моделируем нормальное распределение в шаре с полученным радиусом и центром ξ.
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
                    if (randomValue!=null)
                    {
                        eta[dimensionMinus1] = xi[dimensionMinus1] + randomValue * radius;
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

                double feta = objectiveFunctionF.applyAsDouble(eta); // f(η)
                // Если f(η) ≤ f(ξ), то поиск переходит в точку η
                if (feta <= fxi) // Если f(η) ≤ f(ξ)
                {
                    // Поиск переходит в точку η
                    fxi = feta;
                    for (int i = 0; i < dimension; ++i)
                        xi[i] = eta[i];
                }

                // Пересчет параметров поиска (стандартного отклонения radius)
                if (n % numberOfStageSteps == 0)
                    radius *= radiusMult;

            }
        }
    //endregion
}