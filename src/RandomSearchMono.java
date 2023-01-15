import java.util.Random;
import java.util.function.ToDoubleFunction;

//однородный монотонный поиск

class RandomSearchMono
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

        // Информация о выполнении поиска
        boolean showProgressInformation; // Показывать информацию о выполнении поиска
        long stepsIntervalForProgressInformation; // Интервал для показа шагов поиска
    //endregion

    //region Конструктор
         RandomSearchMono(int theDimension, ToDoubleFunction<double[]> theObjectiveFunctionF,
                            double theNu, double theGamma, long theNumberOfSteps, Double[] xi0, boolean showProgressInfo,
                            long stepsIntervalForProgressInfo, boolean generatorInitialize, int generatorSeed) {
            //Параметры поиска
            dimension = theDimension;
            objectiveFunctionF = theObjectiveFunctionF;
            nu = theNu;
            Gamma = theGamma;
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
            {
                xi[i] = xi0[i];
            }
            fxi = objectiveFunctionF.applyAsDouble(xi); // f(ξ)

            // Вычисляем промежуточный радиус γ
            gamma = Gamma / Math.pow(2.0, 1.0/dimension);

            // Если γ ≤ v, то моделируем равномерное распределение в шаре радиуса Г.
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
            double radius; // Моделируем радиус

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

            double diameter = radius + radius; // Диаметр

            // Моделируем равномерное распределение в шаре с полученным радиусом и центром ξ.
            // В выбранной метрике шар является кубом.
            for (int i = 0; i < dimension; ++i)
            {
                eta[i] = xi[i] + diameter * random.nextDouble() - radius;
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
