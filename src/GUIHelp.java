import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class GUIHelp
{
    //region Переменные
    private String strFormulaHelp = ""; // Справка о формуле
    private String strProgramHelpMono = ""; // Справка о программе
    private String strProgramHelpMonoNormal = ""; // Справка о программе
    private String strProgramHelpInhNormal = ""; // Справка о программе
    private String strProgramHelpInhomogeneous = ""; // Справка о программе
    private String strProgramHelpIngber = ""; // Справка о программе

    JDialog dialog;
    JTextArea textArea = new JTextArea();
    //endregion

    //region Конструктор
    GUIHelp() {
        Font font = new Font("Microsoft Sans Serif",Font.PLAIN,11);
        setHelp();// Задать текст справки

        dialog = new JDialog();

        GridBagLayout layout = new GridBagLayout();

        GridBagConstraints c = new GridBagConstraints();

        dialog.setSize(670, 410);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowClosingListener());
        dialog.setIconImage(new ImageIcon(getClass().getResource("/icons/Help_Green_16_n_p.png")).getImage());

        JButton buttonOK = new JButton("OK");
        buttonOK.setIcon(new ImageIcon(getClass().getResource("/icons/Green_Checkmark_16_n_p.png")));
        buttonOK.addActionListener(new ButtonOKListener());
        buttonOK.setFont(font);

        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText("");
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        Insets insets = new Insets(10,10,2,10);

        dialog.setLayout(layout);
        c.weightx = 1;
        c.weighty = 1;
        c.insets = insets;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        dialog.add(scrollPane,c);

        insets = new Insets(2,10,10,10);
        c.insets = insets;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        dialog.add(buttonOK,c);

        dialog.setResizable(true);
        dialog.getRootPane().setDefaultButton(buttonOK);

        buttonOK.requestFocus();
    }
    //endregion

    //region Слушатели
    class ButtonOKListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            if (dialog.getTitle() == "Справка о программе")
                GUI.frame.setEnabled(true);
            if (dialog.getTitle() == "Справка о формуле")
                GUI.formulaDialog.dialog.setEnabled(true);
            dialog.setVisible(false);
        }
    }

    class WindowClosingListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            if (dialog.getTitle() == "Справка о программе")
                GUI.frame.setEnabled(true);
            if (dialog.getTitle() == "Справка о формуле")
                GUI.formulaDialog.dialog.setEnabled(true);
            dialog.setVisible(false);
        }
    }
    //endregion

    //region Методы

    // Занести текст справки в окно
    void setHelpText() {
        if (dialog.getTitle()=="Справка о формуле") // Справка о формуле
        {
            textArea.setText(strFormulaHelp);
        }
        else// Справка о программе
        {
            if (GUI.algorithmNumber == 1)
                textArea.setText(strProgramHelpMono);
            if (GUI.algorithmNumber == 2)
                textArea.setText(strProgramHelpMonoNormal);
            if (GUI.algorithmNumber == 3)
                textArea.setText(strProgramHelpInhNormal);
            if (GUI.algorithmNumber == 4)
                textArea.setText(strProgramHelpInhomogeneous);
            if (GUI.algorithmNumber == 5)
                textArea.setText(strProgramHelpIngber);
        }
    }

    // Задать текст справки
    private void setHelp() {

        // Справка о формуле
        strFormulaHelp = "Правила написания формулы:" + System.lineSeparator() +
                "Формула может содержать числа, координаты точки, круглые скобки, " +
                "знаки арифметических операций, вызовы функций и константы." + System.lineSeparator() +
                "При вычислениях используется формат double." + System.lineSeparator() +
                "Применяется десятичная запись чисел с десятичным разделителем (можно использовать и запятую и точку)." + System.lineSeparator() +
                "Координаты точки записываются в виде x[k] (или X[k]). " +
                "Индекс k должен быть неотрицательной целочисленной константой, которая меньше размерности пространства оптимизации. " +
                "Например, можно написать x[0] + x[1]." + System.lineSeparator() +
                "Используются восемь символов арифметических операций: +, - (унарный и бинарный минус), *, /, " +
                "% (оператор % вычисляет остаток после деления первого операнда на второй), ^ (возведение в степень), " +
                "бинарная операция | вычисляет максимум двух чисел, бинарная операция & вычисляет минимум двух чисел." +
                System.lineSeparator() +
                "Операции вычисления максимума и минимума имеют самый низкий приоритет, " +
                "следом идут операции сложения и вычитания, потом операции умножения, деления и вычисления остатка, " +
                "затем идет возведение в степень, потом – унарный минус." + System.lineSeparator() +
                "Можно использовать 18 функций. Используются имена языка C# (например, Math.Exp), " +
                "сокращенные варианты имен (без указания класса Math) и принятые в России варианты написания имен функций. " +
                "В именах можно использовать как прописные, так и строчные буквы (они эквивалентны). " +
                "Аргументы функций записываются в круглых скобках." + System.lineSeparator() +
                "1. Абсолютное значение числа: Math.Abs, abs" + System.lineSeparator() +
                "2. Экспонента: Math.Exp, exp" + System.lineSeparator() +
                "3. Десятичный логарифм: Math.Log10, log10, lg" + System.lineSeparator() +
                "4. Натуральный логарифм: Math.Log, log, ln" + System.lineSeparator() +
                "5. Квадратный корень: Math.Sqrt, sqrt" + System.lineSeparator() +
                "6. Арккосинус: Math.Acos, acos, arccos" + System.lineSeparator() +
                "7. Арксинус: Math.Asin, asin, arcsin" + System.lineSeparator() +
                "8. Арктангенс: Math.Atan, atan, arctg" + System.lineSeparator() +
                "9. Гиперболический косинус: Math.Cosh, cosh, ch" + System.lineSeparator() +
                "10. Гиперболический синус: Math.Sinh, sinh, sh" + System.lineSeparator() +
                "11. Гиперболический тангенс: Math.Tanh, tanh, th" + System.lineSeparator() +
                "12. Косинус: Math.Cos, cos" + System.lineSeparator() +
                "13. Синус: Math.Sin, sin" + System.lineSeparator() +
                "14. Тангенс: Math.Tan, tan, tg" + System.lineSeparator() +
                "15. Знак числа: Math.Sign, sign" + System.lineSeparator() +
                "16. Наименьшее целое число, которое больше или равно заданному числу: Math.Ceiling, ceiling" + System.lineSeparator() +
                "17. Наибольшее целое число, которое меньше или равно заданному числу: Math.Floor, floor" + System.lineSeparator() +
                "18. Целое число, ближайшее к значению параметра: Math.Round, round" + System.lineSeparator() +
                "Доступны две константы:" + System.lineSeparator() +
                "1. Число π: Math.PI, pi" + System.lineSeparator() +
                "2. Число e (т.е. exp(1)): Math.E, e" + System.lineSeparator() +
                "Как обычно, в формуле можно использовать пробелы и символы \"новой строки\"." +
                System.lineSeparator() + System.lineSeparator() +

                "При использовании формулы действуют два дополнительных ограничения. " +
                "Максимально допустимая размерность пространства оптимизации не превосходит 200. " +
                "Максимально допустимое количество чисел в формуле не превосходит 500." +
                System.lineSeparator() + System.lineSeparator() +

                "При установке значения параметра \"Число шагов поиска N\" равным нулю " +
                "программа вычислит значение функции в начальной точке поиска. " +
                "Этим можно воспользоваться для вычисления значения целевой функции в заданной точке.";

        String strHead = "Программа \"Марковский случайный поиск\"" + System.lineSeparator() + System.lineSeparator() +

                "Родченко Д.Д. гр.9312" + System.lineSeparator() +

                "НовГУ, кафедра Прикладной математики и информатики" + System.lineSeparator() +
                "Великий Новгород, 2021" + System.lineSeparator() + System.lineSeparator() +

                "Программа предназначена для поиска точки глобального минимума целевой функции." + System.lineSeparator() +
                "Пусть целевая функция f принимает минимальное значение в единственной точке xₒ. " +
                "Рассмотрим задачу поиска точки глобального минимума xₒ с требуемой точностью ε " +
                "(аппроксимация по аргументу). " +
                "Хотя глобальный минимум у функции один, локальных минимумов у нее может быть много." + System.lineSeparator();

        String strSearch = "Для применения поиска нужно задать целевую функцию, параметры поиска, начальную точку поиска и число шагов поиска. " +
                "Результатами поиска будут конечная точка поиска (приближающая точку глобального минимума xₒ) " +
                "и значение целевой функции в конечной точке поиска." + System.lineSeparator() + System.lineSeparator() +
                "Параметрами поиска служат положительные числа v и Г, для которых должны выполняться неравенства 0 < v < Г. " +
                "От выбора параметров поиска зависят успех поиска и его трудоемкость. Значение v можно выбрать близким к требуемой точности ε. " +
                "Значение Г можно выбрать близким к предполагаемой точности начального приближения " +
                "(расстоянию от начальной точки поиска до точки минимума xₒ). " +
                "При выборе Г можно воспользоваться оценкой сверху." + System.lineSeparator() +
                "Число шагов поиска желательно взять достаточно большим." + System.lineSeparator() + System.lineSeparator() +

                "Целевую функцию можно задать двумя способами. " +
                "Во-первых, можно написать код функции непосредственно в коде программы (на языке Java). " +
                "Для этого нужно открыть исходный проект в IntelliJ IDEA 2016 (или совместимой версии). " +
                "В проекте нужно открыть файл ObjectiveFunction.java. В этом файле нужно задать размерность пространства оптимизации и код, " +
                "вычисляющий значение целевой функции. После чего нужно перекомпилировать и запустить программу " +
                "(например, нажав комбинацию клавиш Shift + F10)." + System.lineSeparator() +
                "Во-вторых, целевую функцию можно задать в самой программе поиска (без использования IntelliJ IDEA). " +
                "Для этого в программе нужно нажать кнопку \"Задать формулу\", " +
                "и в открывшемся диалоговом окне отметить пункт \"Использовать формулу\", указать " +
                "размерность пространства оптимизации и написать формулу, определяющую целевую функцию." + System.lineSeparator() +
                "Способ задания целевой функции показан в текстовом поле надписями " +
                "\"Используем код функции\" или \"Используем формулу\"." + System.lineSeparator() +
                "Размерность пространства оптимизации указывается при задании функции " +
                "(или константой в файле ObjectiveFunction.java или параметром при задании формулы)."
                + System.lineSeparator() + System.lineSeparator() +

                strFormulaHelp + System.lineSeparator() + System.lineSeparator() +

                "В программе можно задать форматы вывода значения целевой функции и координат точек " +
                "(используются правила языка Java)." + System.lineSeparator() +
                "Формат E задает экспоненциальное представление чисел. " +
                "Формат F задает представление чисел с фиксированной запятой (точкой). " +
                "Формат G задает более короткий из двух форматов: Е или F. " +
                "Точность задает количество цифр в дробной части для Е и F, и количество значащих цифр для G."
                + System.lineSeparator() + System.lineSeparator() +

                "Можно написать комментарии к решаемой задаче." + System.lineSeparator() + System.lineSeparator() +

                "Для выполнения поиска программа использует генератор псевдослучайных чисел. " +
                "Его можно инициализировать или значением, зависящим от времени, или заданным значением." +
                System.lineSeparator() + System.lineSeparator() +

                "Программа может сохранять данные в формате XML, " +
                "и экспортировать ключевые характеристики поиска в текстовом формате." +
                System.lineSeparator() + System.lineSeparator();

        // Справка о программе
        strProgramHelpMono = strHead +

                "Для поиска глобального минимума используется однородный марковский " +
                "монотонный случайный поиск, описанный в [1]." + System.lineSeparator() + System.lineSeparator() +

                strSearch +

                "Литература" + System.lineSeparator() +
                "1.  Tikhomirov  A S 2018  On the program implementation of a Markov homogeneous monotonous random " +
                "search algorithm of an extremum " + System.lineSeparator() +
                "// IOP Conference Series: Materials Science and Engineering Vol 441 012055 1–8.";

        // Справка о программе
        strProgramHelpMonoNormal =  strHead +

                "Для поиска глобального минимума используется однородный марковский монотонный случайный поиск " +
                "с использованием нормального распределения вероятностей, описанный в [1]."
                + System.lineSeparator() + System.lineSeparator() +

                strSearch +

                "Литература" + System.lineSeparator() +
                "1.  Tikhomirov A S 2019 On the program implementation of a Markov homogeneous random search algorithm " +
                "of an extremum with normal distributions " + System.lineSeparator() +
                "// Journal of Physics: Conference Series, 1352, 012052, P. 1-7.";

        // Справка о программе
        strProgramHelpInhNormal = strHead +

                "Для поиска глобального минимума используется неоднородный марковский монотонный случайный поиск " +
                "с использованием нормального распределения вероятностей, описанный в [1]."
                + System.lineSeparator() + System.lineSeparator() +

                strSearch +

                "Литература" + System.lineSeparator() +
                "1.  Tikhomirov  A S 2019 On the program implementation of a Markov inhomogeneous random  search " +
                "algorithm with normal distributions " + System.lineSeparator() +
                "// Journal of Physics: Conference Series, 1352, 012053, P. 1–8.";

        // Справка о программе
        strProgramHelpInhomogeneous = strHead +

                "Для поиска глобального минимума используется неоднородный марковский монотонный случайный поиск, " +
                "описанный в [1]." + System.lineSeparator() + System.lineSeparator() +

                strSearch +

                "Литература" + System.lineSeparator() +
                "1.  Tikhomirov  A S 2019 On the program implementation of one inhomogeneous Markov  algorithm of " +
                "search for extremum" + System.lineSeparator() +
                "// Journal of Physics: Conference Series, 1352, 012054, P. 1–9.";

        // Справка о программе
        strProgramHelpIngber = strHead +

                "Для поиска глобального минимума используется однородный марковский монотонный случайный поиск " +
                "с использованием распределения Ингбера, описанный в [1]."
                + System.lineSeparator() + System.lineSeparator() +

                strSearch +

                "Литература" + System.lineSeparator() +
                "1.  Tikhomirov  A S 2019 On the program implementation of a Markov homogeneous random  search " +
                "algorithm of an extremum with Ingber's distribution " + System.lineSeparator() +
                "// Journal of Physics: Conference Series, 1352, 012055, P. 1–8.";
    }
    //endregion
}