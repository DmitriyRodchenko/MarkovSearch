import java.util.Random;
import java.util.function.ToDoubleFunction;

//неоднородный  случайный поиск

// Простой алгоритм неоднородного поиска.
// Переходными функциями поиска служат ступеньки.

class RandomSearchInhomogeneous
{
    //region Переменные
        double[] xi; // Текущая точка поиска ξ
        double fxi; // f(ξ)
        private ToDoubleFunction<double[]> objectiveFunctionF; // Целевая функция
        private double nu; // Внутренний радиус v
        private double Gamma; // Внешний радиус Г
        private double radiusMult; // Множитель при вычислении радиуса (Коэффициент сжатия)
        private int dimension; // Размерность пространства оптимизации
        private long numberOfSteps; // Число шагов поиска
        private long numberOfStageSteps; // Число шагов поиска на этапе
        private long numberOfStages; // Количество этапов поиска
        private double[] eta; // Новая точка η в пространстве оптимизации
        Random random; // Генератор псевдослучайных чисел

        // Информация о выполнении поиска
        boolean showProgressInformation; // Показывать информацию о выполнении поиска
        long stepsIntervalForProgressInformation; // Интервал для показа шагов поиска
    //endregion

    //region Конструктор
        RandomSearchInhomogeneous(int theDimension, ToDoubleFunction<double[]> theObjectiveFunctionF,
                                     double theNu, double theGamma, long theNumberOfSteps, long theNumberOfStageSteps, Double[] xi0, boolean showProgressInfo,
                                     long stepsIntervalForProgressInfo, boolean generatorInitialize, int generatorSeed) {
            // Параметры поиска
            dimension = theDimension;
            objectiveFunctionF = theObjectiveFunctionF;
            nu = theNu;
            Gamma = theGamma;

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

            //Информация о выполнении поиска
            stepsIntervalForProgressInformation = stepsIntervalForProgressInfo;
            showProgressInformation = showProgressInfo;
            if (numberOfSteps < stepsIntervalForProgressInfo)
                showProgressInformation = false;
        }
    //endregion

    //region Метод
        // Выполняем случайный поиск
        void searchSimulation() {
            double radius = Gamma; // Радиус ступеньки

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
                // Моделируем равномерное распределение в шаре с полученным радиусом и центром ξ.
                // В выбранной метрике шар является кубом.
                for (int i = 0; i < dimension; ++i)
                    eta[i] = xi[i] + (radius + radius) * random.nextDouble() - radius;

                double feta = objectiveFunctionF.applyAsDouble(eta); // f(η)
                // Если f(η) ≤ f(ξ), то поиск переходит в точку η
                if (feta <= fxi) // Если f(η) ≤ f(ξ)
                {
                    // Поиск переходит в точку η
                    fxi = feta;
                    for (int i = 0; i < dimension; ++i)
                        xi[i] = eta[i];
                }

                // Пересчет параметров поиска (радиуса ступеньки radius)
                if (n % numberOfStageSteps == 0)
                    radius *= radiusMult;

            }
        }
    //endregion
}