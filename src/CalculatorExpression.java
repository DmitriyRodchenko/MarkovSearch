import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.ToDoubleFunction;

public class CalculatorExpression
{
    //region Переменные
    public int dimension = ObjectiveFunction.dimension; // Размерность пространства оптимизации
    private String StringFormulaInitial; // Строка с начальной формулой
    private String StringFormula; // Строка с формулой

    // Строка с обратной польской записью формулы
    private String StringReversePolishNotation;
    boolean hasErrors = false; // Значение, указывающее, есть ли ошибки в формуле
    String errorMessage = ""; // Сообщение об ошибке
    private String info = ""; // Строка для вывода промежуточной информации. Используется при отладке
    private boolean writeInfo = false; // Записывать промежуточную информацию. Используется при отладке
    private final char charPointXInitial = (char)1000; // Обозначения координат точек начинаются с (char)1000 
    private final char charNumberInitial = (char)200; // Обозначения чисел формулы начинаются с (char)200
    private char[] charDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private final char unaryMinus = '_'; // Унарный минус
    private final char maximumOperation = '|'; // Максимум (бинарная операция)
    private final char minimumOperation = '&'; // Минимум (бинарная операция)
    private String operationsCodes = "+-*/%^" + unaryMinus + maximumOperation + minimumOperation; // Коды операций

    // Множество некоторых допустимых символов (которые можно использовать в формуле)
    private String somePermittedSymbols = "xX[]()+-*/%^., " + maximumOperation + minimumOperation;
    private char numberDecimalSeparator = ','; // Десятичный разделитель
    private List<Double> listInnerNumbers = new ArrayList<>(); // Список чисел формулы

    //public final int dimensionFormulaMaxSize = 200; // Максимально допустимая размерность пространства оптимизации (для формулы)
    private final int listInnerNumbersMaxSize = 500; // Максимально допустимое количество чисел в формуле

    // Стек с числами
    private Stack<Double> stackNumbers = new Stack<>();

    // Используются для проверки правильности последовательности элементов в формуле
    // Словарь для проверки правильности последовательности элементов в формуле
    private HashMap<String, String[]> permittedElements = new HashMap<>();
    private final char endFormula = '='; // Символ конца формулы

    // Функции и константы
    // Коды функций
    private final char charFunctionNameInitial = (char)945; // Обозначения имен функций начинаются с (char)945 (т.е. с 'α')
    private final char nameAbs = charFunctionNameInitial; // Абсолютное значение числа
    private final char nameExp = (char)(charFunctionNameInitial + 1); // Экспонента
    private final char nameLog10 = (char)(charFunctionNameInitial + 2); // Десятичный логарифм
    private final char nameLog = (char)(charFunctionNameInitial + 3); // Натуральный логарифм
    private final char nameSqrt = (char)(charFunctionNameInitial + 4); // Квадратный корень
    private final char nameAcos = (char)(charFunctionNameInitial + 5); // Арккосинус
    private final char nameAsin = (char)(charFunctionNameInitial + 6); // Арксинус
    private final char nameAtan = (char)(charFunctionNameInitial + 7); // Арктангенс
    private final char nameCosh = (char)(charFunctionNameInitial + 8); // Гиперболический косинус
    private final char nameSinh = (char)(charFunctionNameInitial + 9); // Гиперболический синус
    private final char nameTanh = (char)(charFunctionNameInitial + 10); // Гиперболический тангенс
    private final char nameCos = (char)(charFunctionNameInitial + 11); // Косинус
    private final char nameSin = (char)(charFunctionNameInitial + 12); // Синус
    private final char nameTan = (char)(charFunctionNameInitial + 13); // Тангенс
    private final char nameSign = (char)(charFunctionNameInitial + 14); // Знак числа
    private final char nameCeiling = (char)(charFunctionNameInitial + 15); // Наименьшее целое число, которое больше или равно заданному числу
    private final char nameFloor = (char)(charFunctionNameInitial + 16); // Наибольшее целое число, которое меньше или равно заданному числу
    private final char nameRound = (char)(charFunctionNameInitial + 17); // Целое число, ближайшее к значению параметра

    // Коды констант
    private final char charConstantNameInitial = (char)(charFunctionNameInitial + 18); // Обозначения констант следуют за обозначениями функций
    private final char namePi = charConstantNameInitial; // Число π
    private final char nameE = (char)(charConstantNameInitial + 1); // Число e = exp(1)

    // Имена и коды функций
    // Имена функций нужно написать в таком порядке, чтобы работал метод StringFormula.replace

    //private Dictionary<Character,String> functionNames;
    private String[][] functionNames = new String[][]{
            {"math.sign", ""+nameSign}, // Знак числа
            {"sign", ""+nameSign},
            {"math.ceiling", ""+nameCeiling}, // Наименьшее целое число, которое больше или равно заданному числу
            {"ceiling", ""+nameCeiling},
            {"math.floor", ""+nameFloor}, // Наименьшее целое число, которое больше или равно заданному числу
            {"floor", ""+nameFloor},
            {"math.round", ""+nameRound}, // Целое число, ближайшее к значению параметра
            {"round", ""+nameRound},
            {"math.abs", ""+nameAbs}, // Абсолютное значение числа
            {"abs", ""+nameAbs},
            {"math.exp", ""+nameExp}, // Экспонента
            {"exp", ""+nameExp},
            {"math.log10", ""+nameLog10}, // Десятичный логарифм
            {"log10", ""+nameLog10},
            {"math.log", ""+nameLog}, // Натуральный логарифм
            {"log", ""+nameLog},
            {"math.sqrt", ""+nameSqrt}, // Квадратный корень
            {"sqrt", ""+nameSqrt},
            {"math.acos", ""+nameAcos}, // Арккосинус
            {"acos", ""+nameAcos},
            {"arccos", ""+nameAcos},
            {"math.asin", ""+nameAsin}, // Арксинус
            {"asin", ""+nameAsin},
            {"arcsin", ""+nameAsin},
            {"math.atan", ""+nameAtan}, // Арктангенс
            {"atan", ""+nameAtan},
            {"arctg", ""+nameAtan},
            {"math.cosh", ""+nameCosh}, // Гиперболический косинус
            {"cosh", ""+nameCosh},
            {"math.sinh", ""+nameSinh}, // Гиперболический синус
            {"sinh", ""+nameSinh},
            {"math.tanh", ""+nameTanh}, // Гиперболический тангенс
            {"tanh", ""+nameTanh},
            {"math.cos", ""+nameCos}, // Косинус
            {"cos", ""+nameCos},
            {"math.sin", ""+nameSin}, // Синус
            {"sin", ""+nameSin},
            {"math.tan", ""+nameTan}, // Тангенс
            {"tan", ""+nameTan},
            {"tg", ""+nameTan}, // Тангенс
            {"lg", ""+nameLog10}, // Десятичный логарифм
            {"ln", ""+nameLog}, // Натуральный логарифм
            {"ch", ""+nameCosh}, // Гиперболический косинус
            {"sh", ""+nameSinh}, // Гиперболический синус
            {"th", ""+nameTanh} // Гиперболический тангенс
    };

    // Коды функций
    private String functionCodes = "";

    private String[][] finalantNames =
    {
        {"math.pi", ""+namePi}, // Число π
        {"pi", ""+namePi},
        {"math.e", ""+nameE}, // Число e = exp(1)
        {"e", ""+nameE}
    };

    // Коды констант
    private String finalantCodes = "" + namePi + nameE;
    //endregion

    //region Конструктор
    public CalculatorExpression(int theDimension, String theStringFormula) {
        dimension = theDimension; // Размерность пространства оптимизации
        StringFormulaInitial = theStringFormula; // Строка с начальной формулой

        // Записать коды функций в functionCodes
        setFunctionCodes();

        // Задать словарь разрешенных операций
        setPermittedOperations();

        // Установить десятичный разделитель
        setDecimalSeparator();
    }
    //endregion

    //region Методы

    // Записать коды функций в functionCodes
    private void setFunctionCodes() {
        // Записать коды функций
        if (functionCodes==null|| functionCodes.equals("") || functionCodes.isEmpty())
            for (int row = 0; row < functionNames.length; ++row)
                if (!functionCodes.contains(functionNames[row][1]))
                    functionCodes += functionNames[row][1];
    }

    // Удалить Math из имен функций и констант
    private String removeMath(String str) {
        String longName;
        String shortName;
        // Удалить Math из имен функций
        for (int row = 0; row < functionNames.length; ++row)
        {
            longName = functionNames[row][1];
            if (longName.contains("math."))
            {
                shortName = longName.replace("math.", "");
                str = str.replace(longName, shortName);
            }
        }

        // Удалить Math из имен констант
        for (int row = 0; row < finalantNames.length; ++row)
        {
            longName = finalantNames[row][0];
            if (longName.contains("math."))
            {
                shortName = longName.replace("math.", "");
                str = str.replace(longName, shortName);
            }
        }
        return str;
    }

    // Заменить имя 
    private String replaceName(String str, String oldValue, String newValue) {
        if (oldValue==null|| oldValue.equals("") || oldValue.isEmpty())
            return str;

        str = endFormula + str + endFormula;

        for (int i = 1; i < (str.length() - oldValue.length()); i++)
        {
            if (str.substring(i, i + oldValue.length()) == oldValue)
                if ((str.toCharArray()[i-1] < 'a' || str.toCharArray()[i - 1] > 'z') && (str.toCharArray()[i + oldValue.length()] < 'a' || str.toCharArray()[i + oldValue.length()] > 'z'))
                    str = str.substring(0, i) + newValue + str.substring(i + oldValue.length());

        }

        str = str.replace(""+endFormula, "");
        return str;
    }

    // Задать словарь для проверки правильности последовательности элементов в формуле
    private void setPermittedOperations() {
        permittedElements.put("начало формулы", new String[] { "(", "унарная операция", "число", "функция" });
        permittedElements.put("(", new String[] { "(", "унарная операция", "число", "функция" });
        permittedElements.put(")", new String[] { ")", "бинарная операция", "конец формулы" });
        permittedElements.put("бинарная операция", new String[] { "(", "число", "функция" });
        permittedElements.put("унарная операция", new String[] { "(", "число", "функция" });
        permittedElements.put("число", new String[] { ")", "бинарная операция", "конец формулы" });
        permittedElements.put("функция", new String[] { "(" });
    }

    // Установить десятичный разделитель
    private void setDecimalSeparator() {
        // Предоставляет сведения для конкретного языка и региональных параметров для форматирования числовых значений 
        //NumberFormatInfo currentNumberFormat = CultureInfo.CurrentCulture.NumberFormat;
        DecimalFormat currentNumberFormat = (DecimalFormat)NumberFormat.getNumberInstance();

        // Возвращает строку, используемую в качестве десятичного разделителя в числовых значениях
        if (""+currentNumberFormat.getDecimalFormatSymbols().getDecimalSeparator() == ",")
        {
            numberDecimalSeparator = ',';
            if (writeInfo)
                info += "Десятичный разделитель - запятая" + System.lineSeparator();
        }
        else if (""+currentNumberFormat.getDecimalFormatSymbols().getDecimalSeparator() == ".")
        {
            numberDecimalSeparator = '.';
            if (writeInfo)
                info += "Десятичный разделитель - точка" + System.lineSeparator();
        }
        else if (!(""+currentNumberFormat.getDecimalFormatSymbols().getDecimalSeparator()).equals("") || !(""+currentNumberFormat.getDecimalFormatSymbols().getDecimalSeparator()).isEmpty())
        {
            numberDecimalSeparator = currentNumberFormat.getDecimalFormatSymbols().getDecimalSeparator();
            if (writeInfo)
                info += "Десятичный разделитель - " + numberDecimalSeparator + System.lineSeparator();
        }
        else
        {
            hasErrors = true; // Проблема с десятичным разделителем в настройках региональных параметров
            errorMessage = "Проблема с десятичным разделителем в настройках региональных параметров";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
        }
    }

    // Заменить символы-разделители пробелами
    private String replaceWhiteSpace(String str) {
        str = str.replace(System.lineSeparator(), " ");
        str = str.replace('\n', ' ');
        str = str.replace('\r', ' ');
        str = str.replace('\t', ' ');
        return str;
    }

    // Удалить лишние пробелы
    private String removeExtraWhiteSpace(String str) {
        str = str.trim().replaceAll("\\s+", " ");
        str = str.replace("  ", " ");

        str = str.replace("( ", "(");
        str = str.replace(" (", "(");
        str = str.replace(" )", ")");
        str = str.replace(") ", ")");

        str = str.replace("[ ", "[");
        str = str.replace(" [", "[");
        str = str.replace(" ]", "]");
        str = str.replace("] ", "]");

        for (char op : operationsCodes.toCharArray())
        {
            str = str.replace(" " + op, ""+op);
            str = str.replace(op + " ", ""+op);
        }

        return str;
    }

    // Получить тип элемента по коду
    private String getElementType(char ch) {
        if (ch == unaryMinus) // Унарный минус
            return "унарная операция";
        else if (operationsCodes.contains(""+ch)) // Бинарная операция
            return "бинарная операция";
        else if (ch == '(')
            return "(";
        else if (ch == ')')
            return ")";
        else if (functionCodes.contains(""+ch)) // Функция
            return "функция";
        else if (isVariable(ch) || finalantCodes.contains(""+ch)) // Число
            return "число";
        else if (ch == endFormula) // Конец формулы
            return "конец формулы";
        else
            return "неизвестный элемент";
    }

    // Получить имя функции по коду
    private String getFunctionName(char ch) {
        String functionName = "";
        for (int row = 0; row < functionNames.length; ++row)
            if (functionNames[row][1] == ""+ch)
        {
            functionName = functionNames[row][0];
            functionName = functionName.replace("math.", "");
        }
        return functionName;
    }

    // Получить имя константы по коду
    private String getConstantName(char ch) {
        String finalantName = "";
        for (int row = 0; row < finalantNames.length; ++row)
            if (finalantNames[row][1] == ""+ch)
        {
            finalantName = finalantNames[row][0];
            finalantName = finalantName.replace("math.", "");
        }
        return finalantName;
    }

    // Получить информацию об элементе по коду
    private String getElementInfo(char ch) {
        if (ch == unaryMinus) // Унарный минус
            return "унарный минус";
        else if (operationsCodes.contains(""+ch)) // Бинарная операция
            return "бинарная операция " + ch;
        else if (ch == '(')
            return "(";
        else if (ch == ')')
            return ")";
        else if (functionCodes.contains(""+ch)) // Функция
            return "функция " + getFunctionName(ch);
        else if (isCoordinateX(ch)) // Координата точки
            return "число x[" + (ch - charPointXInitial) + "]";
        else if (isInnerNumber(ch)) // Число
            return "число " + listInnerNumbers.get(ch - charNumberInitial);
        else if (finalantCodes.contains(""+ch)) // Число
            return "число " + getConstantName(ch);
        else if (ch == endFormula) // Конец формулы
            return "конец формулы";
        else
            return "неизвестный элемент";
    }

    // Проверить правильность последовательности элементов в формуле
    private void checkPermittedElements() {
        if (StringFormula==null|| StringFormula.equals("") || StringFormula.isEmpty())
        {
            // Формула не написана
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула не задана";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }

        String StringFormulaToTest = StringFormula + endFormula;
        String element;
        String elementPrevious = "начало формулы";
        String elementInfo;
        String elementPreviousInfo = "начало формулы";
        char ch;
        for (int i = 0; i < StringFormulaToTest.length(); ++i)
        {
            ch = StringFormulaToTest.toCharArray()[i];
            element = getElementType(ch);
            elementInfo = getElementInfo(ch);
            if (writeInfo)
                info += ch + " - " + elementInfo + System.lineSeparator();
            if (!permittedElements.containsKey(elementPrevious))
            {
                // Неизвестный элемент в формуле
                hasErrors = true; // Формула написана неправильно
                errorMessage = "Неизвестный элемент в формуле";
                if (writeInfo)
                    info += errorMessage + System.lineSeparator();
                return;
            }

            if(Arrays.asList(permittedElements.get(elementPrevious)).indexOf(element)== -1)
            {
                // Неправильная последовательность элементов в формуле
                hasErrors = true; // Формула написана неправильно
                errorMessage = "Неправильная последовательность элементов в формуле. Элемент <" +
                        elementInfo + "> не может следовать за элементом <" +
                        elementPreviousInfo + ">";
                if (writeInfo)
                    info += errorMessage + System.lineSeparator();
                return;
            }
            elementPrevious = element;
            elementPreviousInfo = elementInfo;
        }
    }

    // Проверить правильность расстановки скобок в формуле
    private void checkBrackets() {
        Stack<Character> bracketsStack = new Stack<>();

        for (char ch : StringFormula.toCharArray())
        {
            if (ch == '(' || ch == '[')
            {
                bracketsStack.push(ch);
                continue;
            }

            if (ch != ')' && ch != ']')
                continue;

            char bracket;

            if (bracketsStack.size() > 0)
                bracket = bracketsStack.pop();
            else
            {
                hasErrors = true; // Формула написана неправильно
                errorMessage = "Неправильно расставлены скобки";
                if (writeInfo)
                    info += errorMessage + System.lineSeparator();
                return;
            }

            switch (bracket)
            {
                case '(':
                    if (ch != ')')
                    {
                        hasErrors = true; // Формула написана неправильно
                        errorMessage = "Неправильно расставлены скобки";
                        if (writeInfo)
                            info += errorMessage + System.lineSeparator();
                        return;
                    }
                    break;
                case '[':
                    if (ch != ']')
                    {
                        hasErrors = true; // Формула написана неправильно
                        errorMessage = "Неправильно расставлены скобки";
                        if (writeInfo)
                            info += errorMessage + System.lineSeparator();
                        return;
                    }
                    break;
            }
        }

        if (bracketsStack.size() != 0)
        {
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Неправильно расставлены скобки";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }
    }

    // Проверить наличие недопустимых символов в формуле
    private void checkProhibitedSymbols(String str) {
        // Сформировать множество допустимых символов
        HashSet<Character> permittedSymbols = new HashSet<>(); // Множество допустимых символов

        // Добавить символы из имен функций
        for (int row = 0; row < functionNames.length; ++row)
            for (char ch : functionNames[row][0].toCharArray())
        permittedSymbols.add(ch);

        // Добавить символы из имен констант
        for (int row = 0; row < finalantNames.length; ++row)
            for (char ch : finalantNames[row][0].toCharArray())
        permittedSymbols.add(ch);

        // Добавить цифры
        for (char ch : charDigits)
        permittedSymbols.add(ch);

        // Добавить разные символы
        for (char ch : somePermittedSymbols.toCharArray())
        permittedSymbols.add(ch);

        // Добавить десятичный разделитель
        permittedSymbols.add(numberDecimalSeparator);

        // Заменить символы-разделители пробелами
        str = replaceWhiteSpace(str);

        // Проверить наличие недопустимых символов в формуле
        for (char ch : str.toCharArray())
        {
            if (!permittedSymbols.contains(ch)) {
                // Формула содержит недопустимые символы
                hasErrors = true; // Формула написана неправильно
                errorMessage = "Формула содержит недопустимый символ <" + ch + ">";
                if (writeInfo) info += errorMessage + System.lineSeparator();
                return;
            }
        }
    }

    // Проверить наличие недопустимых элементов в формуле
    private void checkProhibitedElements() {
        String StringToCheck = StringFormulaInitial.toLowerCase(); // Строка в нижнем регистре;

        // Заменить символы-разделители пробелами
        StringToCheck = replaceWhiteSpace(StringToCheck);

        // Удалить лишние пробелы
        StringToCheck = removeExtraWhiteSpace(StringToCheck);

        // Удалить Math из имен функций и констант
        StringToCheck = removeMath(StringToCheck);

        // Проверить вхождения Math
        if (StringToCheck.contains("math"))
        {
            // Формула содержит недопустимые элементы
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула содержит недопустимое использование элемента <Math>";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }

        // Проверить вхождения .. и т.п.
        if (StringToCheck.contains(".."))
        {
            // Формула содержит недопустимые элементы
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула содержит недопустимый элемент <..>";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }
        if (StringToCheck.contains(",."))
        {
            // Формула содержит недопустимые элементы
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула содержит недопустимый элемент <,.>";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }
        if (StringToCheck.contains(".,"))
        {
            // Формула содержит недопустимые элементы
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула содержит недопустимый элемент <.,>";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }
        if (StringToCheck.contains(",,"))
        {
            // Формула содержит недопустимые элементы
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула содержит недопустимый элемент <,,>";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }

        // Заменить "log10" на "lg" (из-за 10 в имени)
        StringFormula = StringFormula.replace("math.log10", "lg");
        StringFormula = StringFormula.replace("log10", "lg");

        // Удалить имена функций
        for (int row = 0; row < functionNames.length; ++row)
            StringToCheck = replaceName(StringToCheck, functionNames[row][0], "");

        // Удалить имена констант
        for (int row = 0; row < finalantNames.length; ++row)
            StringToCheck = replaceName(StringToCheck, finalantNames[row][0], "");

        // Удалить x[i]
        String point;
        for (int i = 0; i < dimension; ++i)
        {
            point = "x[" + i + "]";
            StringToCheck = StringToCheck.replace(point, ""); // Удалить x[i]
        }

        // Удалить символы операций
        for (char ch : operationsCodes.toCharArray())
        StringToCheck = StringToCheck.replace(""+ch, "");

        // Удалить цифры
        for (char ch : charDigits)
        StringToCheck = StringToCheck.replace(""+ch, "");

        // Удалить десятичный разделитель
        StringToCheck = StringToCheck.replace(",", "");
        StringToCheck = StringToCheck.replace(".", "");
        StringToCheck = StringToCheck.replace(""+numberDecimalSeparator, "");

        // Удалить скобки
        StringToCheck = StringToCheck.replace("(", "");
        StringToCheck = StringToCheck.replace(")", "");

        if (!StringToCheck.equals(""))
        {
            // Формула содержит недопустимые элементы
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула содержит недопустимые элементы <" + StringToCheck + ">";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
        }
    }

    // Обработать формулу. Заменить координаты точек, числа и функции на символы
    public void handleFormula() {
        hasErrors = false; // Значение, указывающее, есть ли ошибки в формуле
        errorMessage = ""; // Сообщение об ошибке

        if (StringFormulaInitial==null || StringFormulaInitial.equals("") || StringFormulaInitial.isEmpty())
        {
            // Формула не написана
            hasErrors = true; // Формула написана неправильно
            errorMessage = "Формула не задана";
            if (writeInfo)
                info += errorMessage + System.lineSeparator();
            return;
        }

        // Коды функций
        if (writeInfo)
            info += "Коды функций: " + functionCodes + " (всего " + functionCodes.length() + ")" + System.lineSeparator();

        StringFormula = StringFormulaInitial;

        // Заменить символы-разделители пробелами
        StringFormula = replaceWhiteSpace(StringFormula);

        // Удалить лишние пробелы
        StringFormula = removeExtraWhiteSpace(StringFormula);

        // Проверить правильность расстановки скобок в формуле
        checkBrackets();
        if (hasErrors)
            return;

        // Проверить наличие недопустимых символов в формуле
        checkProhibitedSymbols(StringFormula);
        if (hasErrors)
            return;

        // Проверить наличие недопустимых элементов в формуле
        checkProhibitedElements();
        if (hasErrors)
            return;

        StringFormula = StringFormula.toLowerCase(); // Строка в нижнем регистре
        if (writeInfo)
            info += StringFormula + System.lineSeparator();

        // Заменить имена функций и констант их кодами
        // Заменить "log10" на "lg" (из-за 10 в имени)
        StringFormula = StringFormula.replace("math.log10", "lg");
        StringFormula = StringFormula.replace("log10", "lg");

        // Заменить имена функций их кодами
        for (int row = 0; row < functionNames.length; ++row)
            StringFormula = replaceName(StringFormula, functionNames[row][0], functionNames[row][1]);

        // Заменить имена констант их кодами
        for (int row = 0; row < finalantNames.length; ++row)
            StringFormula = replaceName(StringFormula, finalantNames[row][0], finalantNames[row][1]);

        // Заменить x[i] символами
        String point;
        char pointChar;
        String pointCharString;
        for (int i = 0; i < dimension; ++i)
        {
            point = "x[" + i + "]";
            pointChar = (char)(charPointXInitial + i);
            pointCharString = ""+pointChar;
            StringFormula = StringFormula.replace(point, pointCharString); // Заменить x[i] символами
        }
        if (writeInfo)
            info += StringFormula + System.lineSeparator();

        // Заменить числа символами
        // Установить десятичный разделитель в формуле
        if (numberDecimalSeparator == ',')
        {
            StringFormula = StringFormula.replace('.', numberDecimalSeparator);
        }
        else if (numberDecimalSeparator == '.')
        {
            StringFormula = StringFormula.replace(',', numberDecimalSeparator);
        }
        else
        {
            StringFormula = StringFormula.replace(',', numberDecimalSeparator);
            StringFormula = StringFormula.replace('.', numberDecimalSeparator);
        }

        // Унарный минус
        // Заменить унарные минусы символом unaryMinus
        if (StringFormula.toCharArray()[0] == '-' && StringFormula.length() > 1)
            StringFormula = unaryMinus + StringFormula.substring(1, StringFormula.length() - 1);
        StringFormula = StringFormula.replace("(-", "(" + unaryMinus); // Заменить унарные минусы
        StringFormula = StringFormula.replace("*-", "*" + unaryMinus); // Заменить унарные минусы
        StringFormula = StringFormula.replace("/-", "/" + unaryMinus); // Заменить унарные минусы
        StringFormula = StringFormula.replace("%-", "%" + unaryMinus); // Заменить унарные минусы
        StringFormula = StringFormula.replace("^-", "^" + unaryMinus); // Заменить унарные минусы
        StringFormula = StringFormula.replace(maximumOperation + "-", ""+maximumOperation + unaryMinus); // Заменить унарные минусы
        StringFormula = StringFormula.replace(minimumOperation + "-", ""+minimumOperation + unaryMinus); // Заменить унарные минусы

        // Заменить числа символами
        int indexBegin = 0;
        int indexEnd;
        int numbersCount = 0; // Количество найденных чисел в формуле
        String StringNumber;
        String StringFormulaBegin;
        String StringFormulaEnd;
        char charNumber;
        Double numberInner;
        // Заменить отрицательные числа символами
        while (indexBegin < StringFormula.length() - 1)
        {
            if (StringFormula.toCharArray()[indexBegin] != unaryMinus)
            {
                ++indexBegin;
                continue;
            }

            indexEnd = indexBegin + 1;
            if (!Character.isDigit(StringFormula.toCharArray()[indexEnd]))
            {
                ++indexBegin;
                continue;
            }
            ++indexEnd;
            while (indexEnd < StringFormula.length() && (Character.isDigit(StringFormula.toCharArray()[indexEnd]) || StringFormula.toCharArray()[indexEnd] == numberDecimalSeparator))
                ++indexEnd;
            StringNumber = StringFormula.substring(indexBegin, indexEnd - indexBegin);
            charNumber = (char)(charNumberInitial + numbersCount);

            // Заменить число символом
            if (indexBegin > 0)
                StringFormulaBegin = StringFormula.substring(0, indexBegin);
            else
                StringFormulaBegin = "";
            if (indexEnd < StringFormula.length())
                StringFormulaEnd = StringFormula.substring(indexEnd);
            else
                StringFormulaEnd = "";
            StringFormula = StringFormulaBegin + charNumber + StringFormulaEnd; // Заменить число символом

            ++numbersCount;
            StringNumber = StringNumber.replace(unaryMinus, '-');
            if (writeInfo)
            {
                info += "Число " + charNumber + " = " + StringNumber + System.lineSeparator();
                info += "Формула - <" + StringFormula + ">" + System.lineSeparator();
            }
            numberInner = Double.parseDouble(StringNumber);
            if (numberInner!=null)
            {
                if (listInnerNumbers.size() < listInnerNumbersMaxSize)
                    listInnerNumbers.add(numberInner);
                else
                {
                    hasErrors = true; // Есть ошибка в формуле
                    errorMessage = "Превышено максимально допустимое количество чисел в формуле " + listInnerNumbersMaxSize; // Сообщение об ошибке
                    if (writeInfo)
                        info += errorMessage + System.lineSeparator();
                    return;
                }
            }
            else
            {
                hasErrors = true; // Есть ошибка в формуле
                errorMessage = "Число <" + StringNumber + "> написано неправильно"; // Сообщение об ошибке
                if (writeInfo)
                    info += errorMessage + System.lineSeparator();
                return;
            }
            ++indexBegin;
        }

        // Заменить неотрицательные числа символами
        //indexBegin = stringFormula.IndexOfAny(charDigits);
        indexBegin = indexOfAny(StringFormula,charDigits);
        while (indexBegin != -1)
        {
            indexEnd = indexBegin + 1;
            while (indexEnd < StringFormula.length() && (Character.isDigit(StringFormula.toCharArray()[indexEnd]) || StringFormula.toCharArray()[indexEnd] == numberDecimalSeparator))
                ++indexEnd;
            StringNumber = StringFormula.substring(indexBegin, indexEnd);
            charNumber = (char)(charNumberInitial + numbersCount);
            if (writeInfo)
                info += "Число " + charNumber + " = " + StringNumber + System.lineSeparator();

            // Заменить число символом
            if (indexBegin > 0)
                StringFormulaBegin = StringFormula.substring(0, indexBegin);
            else
                StringFormulaBegin = "";
            if (indexEnd < StringFormula.length())
                StringFormulaEnd = StringFormula.substring(indexEnd);
            else
                StringFormulaEnd = "";
            StringFormula = StringFormulaBegin + charNumber + StringFormulaEnd; // Заменить число символом
            if (writeInfo)
                info += "Формула - <" + StringFormula + ">" + System.lineSeparator();

            ++numbersCount;
            numberInner = null;
            try
            {
                numberInner = Double.parseDouble(StringNumber);
            }
            catch (Exception ex){}

            if (numberInner!=null)
            {
                if (listInnerNumbers.size() < listInnerNumbersMaxSize)
                    listInnerNumbers.add(numberInner);
                else
                {
                    hasErrors = true; // Есть ошибка в формуле
                    errorMessage = "Превышено максимально допустимое количество чисел в формуле " + listInnerNumbersMaxSize; // Сообщение об ошибке
                    if (writeInfo)
                        info += errorMessage + System.lineSeparator();
                    return;
                }
            }
            else
            {
                hasErrors = true; // Есть ошибка в формуле
                errorMessage = "Число <" + StringNumber + "> написано неправильно"; // Сообщение об ошибке
                if (writeInfo)
                    info += errorMessage + System.lineSeparator();
                return;
            }
            indexBegin = indexOfAny(StringFormula,charDigits);
        }
        if (writeInfo)
            info += StringFormula + System.lineSeparator();

        // Удалить пробелы
        StringFormula = StringFormula.replace(" ", ""); // Удалить пробелы
        if (writeInfo)
            info += StringFormula + System.lineSeparator();

        checkPermittedElements();
    }

    // Приоритет операции
    private int priorityOfOperation(char op) {
        if (finalantCodes.contains(op+""))
            return 7;
        else if (functionCodes.contains(op+""))
            return 6;
        else if (op == unaryMinus)
            return 5;
        else if (op == '^')
            return 4;
        else if (op == '*' || op == '/' || op == '%')
            return 3;
        else if (op == '+' || op == '-')
            return 2;
        else if (op == maximumOperation || op == minimumOperation)
            return 1;
        else
            return 0;
    }

    // Является ли координатой X
    public boolean isCoordinateX(char var)
    {
        return var >= charPointXInitial && var < charPointXInitial + dimension;
    }

    // Является ли внутренним числом формулы
    public boolean isInnerNumber(char var) {
        return var >= charNumberInitial && var < charNumberInitial + listInnerNumbers.size();
    }

    // Является ли переменной
    public boolean isVariable(char var)
    {
        return isCoordinateX(var) || isInnerNumber(var);
    }

    public void makeReversePolishNotation() {
        // Строка с обратной польской записью формулы
        StringReversePolishNotation = "";

        if (hasErrors) // Есть ошибка в формуле
            return;

        // Стек с обратной польской записью формулы
        Stack<Character> stackFormula = new Stack<>();
        // Вспомогательный стек для построения обратной польской записи формулы
        Stack<Character> stackAdditional = new Stack<>();

        int i = 0; // Номер символа в строке с формулой
        char ch; // Символ в строке с формулой
        while (i < StringFormula.length())
        {
            ch = StringFormula.charAt(i);
            if (isVariable(ch))
                stackFormula.push(ch);
            else if (ch == '(')
                stackAdditional.push(ch);
            else if (ch == ')')
            {
                while (stackAdditional.size() > 0 && stackAdditional.peek() != '(')
                    stackFormula.push(stackAdditional.pop());

                if (stackAdditional.size() == 0 || stackAdditional.pop() != '(')
                {
                    hasErrors = true; // Есть ошибка в формуле
                    errorMessage = "Не хватает ("; // Сообщение об ошибке
                    return;
                }
            }
            else if (operationsCodes.contains(ch+"") || functionCodes.contains(ch+"") || finalantCodes.contains(ch+""))
            {
                while (stackAdditional.size() > 0 && priorityOfOperation(stackAdditional.peek()) >= priorityOfOperation(ch))
                    stackFormula.push(stackAdditional.pop());
                stackAdditional.push(ch);
            }
            ++i;
        }

        while (stackAdditional.size() > 0)
            stackFormula.push(stackAdditional.pop());

        // Строка с обратной польской записью формулы
        StringReversePolishNotation = "";
        while (stackFormula.size() > 0)
            StringReversePolishNotation = stackFormula.pop() + StringReversePolishNotation;
        if (writeInfo)
            info += StringReversePolishNotation + System.lineSeparator();

        stackFormula.clear();
        stackAdditional.clear();
    }

    // Вычислить операцию
    private void calculateOperation(char op) {
        double operand;

        switch (op)
        {
            case unaryMinus: // Унарный минус
                stackNumbers.push(-stackNumbers.pop());
                break;
            case '+':
                stackNumbers.push(stackNumbers.pop() + stackNumbers.pop());
                break;
            case '-':
                operand = stackNumbers.pop();
                stackNumbers.push(stackNumbers.pop() - operand);
                break;
            case '*':
                stackNumbers.push(stackNumbers.pop() * stackNumbers.pop());
                break;
            case '/':
                operand = stackNumbers.pop();
                stackNumbers.push(stackNumbers.pop() / operand);
                break;
            case '%':
                operand = stackNumbers.pop();
                stackNumbers.push(stackNumbers.pop() % operand);
                break;
            case '^':
                operand = stackNumbers.pop();
                stackNumbers.push(Math.pow(stackNumbers.pop(), operand));
                break;
            case namePi: // Число π
                stackNumbers.push(Math.PI);
                break;
            case nameE: // Число e = exp(1)
                stackNumbers.push(Math.E);
                break;
            case nameAbs: // Абсолютное значение числа
                stackNumbers.push(Math.abs(stackNumbers.pop()));
                break;
            case nameExp: // Экспонента
                stackNumbers.push(Math.exp(stackNumbers.pop()));
                break;
            case nameLog: // Натуральный логарифм
                stackNumbers.push(Math.log(stackNumbers.pop()));
                break;
            case nameLog10: // Десятичный логарифм
                stackNumbers.push(Math.log10(stackNumbers.pop()));
                break;
            case nameSqrt: // Квадратный корень
                stackNumbers.push(Math.sqrt(stackNumbers.pop()));
                break;
            case nameCos: // Косинус
                stackNumbers.push(Math.cos(stackNumbers.pop()));
                break;
            case nameSin: // Синус
                stackNumbers.push(Math.sin(stackNumbers.pop()));
                break;
            case nameTan: // Тангенс
                stackNumbers.push(Math.tan(stackNumbers.pop()));
                break;
            case nameAcos: // Арккосинус
                stackNumbers.push(Math.acos(stackNumbers.pop()));
                break;
            case nameAsin: // Арксинус
                stackNumbers.push(Math.asin(stackNumbers.pop()));
                break;
            case nameAtan: // Арктангенс
                stackNumbers.push(Math.atan(stackNumbers.pop()));
                break;
            case nameCosh: // Гиперболический косинус
                stackNumbers.push(Math.cosh(stackNumbers.pop()));
                break;
            case nameSinh: // Гиперболический синус
                stackNumbers.push(Math.sinh(stackNumbers.pop()));
                break;
            case nameTanh: // Гиперболический тангенс
                stackNumbers.push(Math.tanh(stackNumbers.pop()));
                break;
            case nameSign: // Знак числа
                operand = stackNumbers.pop();
                if (operand == Double.NaN)
                    stackNumbers.push(Double.NaN);
                else
                    stackNumbers.push(Math.signum(operand));
                break;
            case nameCeiling: // Наименьшее целое число, которое больше или равно заданному числу
                stackNumbers.push(Math.ceil(stackNumbers.pop()));
                break;
            case nameFloor: // Наибольшее целое число, которое меньше или равно заданному числу
                stackNumbers.push(Math.floor(stackNumbers.pop()));
                break;
            case nameRound: // Целое число, ближайшее к значению параметра
                stackNumbers.push((double)Math.round(stackNumbers.pop()));
                break;
            case maximumOperation: // Максимум
                stackNumbers.push(Math.max(stackNumbers.pop(), stackNumbers.pop()));
                break;
            case minimumOperation: // Минимум
                stackNumbers.push(Math.min(stackNumbers.pop(), stackNumbers.pop()));
                break;
        }
    }

    //Найти индекс символа в строке
    public static int indexOfAny(String str, char[] searchChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    //Проверка массива символов на пустоту
    public static boolean isEmpty(char[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        return false;
    }

    //Проверка строки на пустоту
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    //endregion

    //region Обработчик функции
    public ToDoubleFunction<double[]> f = xi ->
    {
        double fValue = 0.0;

        stackNumbers.clear(); // Стек с числами
        for (char ch : StringReversePolishNotation.toCharArray()) {
            if (isCoordinateX(ch)) stackNumbers.push(xi[ch - charPointXInitial]);
            else if (isInnerNumber(ch)) stackNumbers.push(listInnerNumbers.get(ch - charNumberInitial));
            else calculateOperation(ch);
        }

        if (stackNumbers.size() > 0) fValue = stackNumbers.pop();
        return fValue; // Функция возвращает значение
    };
    //endregion
}