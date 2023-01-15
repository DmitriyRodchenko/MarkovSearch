import java.lang.Object;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.time.*;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.*;

public class GUI
{
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(GUI::new);
    }

    //region Переменные
        private static String programTitle = "Марковский случайный поиск"; // Заголовок программы

        private static File fileName;
        private String fileTextName = ""; // Имя текстового файла с данными

        private JFileChooser fileChooserXML;
        private JFileChooser fileChooserTXT;

        private FileNameExtensionFilter filterXML;
        private FileNameExtensionFilter filterTXT;

        public static int dimension = ObjectiveFunction.dimension; // Размерность пространства оптимизации

        // Использование формулы для вычисления целевой функции
        public static boolean useFormula = false; // Нужно ли использовать формулу
        public static String formulaString = ""; // Строка с формулой

        static Double[][] formattedPoints = new Double[dimension][3];

        private Double minimumOfFunction = null; // Минимальное значение целевой функции

        private Double nu = null; // Внутренний радиус v
        private Double Gamma = null; // Внешний радиус Г

        private long numberOfStepsInitial = 1000L; // Начальное число шагов поиска
        private long numberOfStageStepsInitial = 10L; // Начальное число шагов поиска на этапе
        // Точность форматирования чисел
        private int precisionInitial = 6;
        // Инициализация генератора псевдослучайных чисел
        // Число, используемое для вычисления начального значения последовательности псевдослучайных чисел
        private int generatorSeedInitial = 100;
        // Нужно ли задавать инициализатор генератора псевдослучайных чисел
        private boolean generatorInitializeInitial = false; // Нужно ли инициализировать генератор
        // Информация о выполнении поиска
        private boolean showProgressInfoInitial = false; // Показывать информацию о выполнении поиска
        private long stepsIntervalForProgressInfoInitial = 10000L; // Интервал для показа шагов поиска
        private String timeOfCalculations = ""; // Время выполнения поиска

        private ImageIcon GreenCheckMarkIcon = new ImageIcon(getClass().getResource("/icons/Green_Checkmark_16_n_p.png"));
        private ImageIcon RedCheckMarkIcon = new ImageIcon(getClass().getResource("/icons/Red_Delete_16_n_p.png"));
        private ImageIcon checkIcon = new ImageIcon(getClass().getResource("/icons/checkmark.png"));

        static JFrame frame = new JFrame();

        private final JMenuItem fileSaveAs;
        private final JMenuItem fileExportAs;

        static int algorithmNumber = 1;

        private static RandomSearchMono randomSearchMono;
        private static RandomSearchMonoNormal randomSearchMonoNormal;
        private static RandomSearchInhNormal randomSearchInhNormal;
        private static RandomSearchInhomogeneous randomSearchInh;
        private static RandomSearchIngber randomSearchIngber;

        private final JMenuItem algorithmMono;
        private final JMenuItem algorithmMonoNormal;
        private final JMenuItem algorithmInhNormal;
        private final JMenuItem algorithmInhomogeneous;
        private final JMenuItem algorithmIngber;

        private final JMenu searchMenu;
        private final JMenuItem contextSearch;

        private final JMenuItem helpPromptItem;
        private boolean helpPromptChecked = false;

        private final JPanel panelParameters;
        private final JPanel panelFormatFunc;
        private final JPanel panelFormatPoints;
        private final JPanel panelRandomGenerator;
        private final JPanel panelProgressInfo;

        private final JLabel labelMinFunc;
        private final JLabel labelNumberSteps;
        private final JLabel labelMStageSteps;

        private final JLabel labelV;
        private final JLabel labelGamma;
        private final JLabel labelDimension;
        private final JLabel labelPrecisionFunc;
        private final JLabel labelPrecisionPoints;

        private final JTextField textFieldMinFunc;
        private final JTextField textFieldV;
        private final JTextField textFieldGamma;
        static JTextField textFieldDimension;
        static JTextField textFieldUseFormula;
        static JTextField textFieldProgressInfo;

        private final JCheckBox checkBoxRandomGenerator;
        private final JCheckBox checkBoxProgressInfoSteps;

        private final JRadioButton radioButtonFuncE = new JRadioButton("E");
        private final JRadioButton radioButtonFuncF = new JRadioButton("F");
        private final JRadioButton radioButtonFuncG = new JRadioButton("G");

        private static final JRadioButton radioButtonPointsE = new JRadioButton("E");
        private static final JRadioButton radioButtonPointsF = new JRadioButton("F");
        private static final JRadioButton radioButtonPointsG = new JRadioButton("G");

        private final JSpinner spinnerNumberSteps;
        private final JSpinner spinnerMStageSteps;
        private final JSpinner spinnerPrecisionFunc;
        private static JSpinner spinnerPrecisionPoints;
        private final JSpinner spinnerRandomGenerator;
        private final JSpinner spinnerProgressInfo;

        private final JButton buttonCalculate;
        private final JButton buttonCheckParameters;
        private final JButton buttonSetFunc;

        private final JButton searchButton;

        private final ErrorProvider errorProviderV = new ErrorProvider();
        private final ErrorProvider errorProviderGamma = new ErrorProvider();

        private static Boolean isSaved = true;

        private Instant timeStart;

        static Object[] options = {"Да", "Нет"};

        private final String lineSeparator = System.lineSeparator();

        static JTable pointsTable;

        private JScrollPane scrollPanePointsTable;

        private DataModel dataModel = new DataModel();
        static PointsModel pointsTableModel;
        static ParamsModel paramsTableModel;

        private JSplitPane splitPane;
        private GUIComments commentsDialog;
        static GUIFormula formulaDialog;
        private GUIHelp helpDialog;
        private GUIAbout aboutDialog;
    //endregion

    //region Конструктор графического интерфейса
        private GUI() {

        // Новый шрифт
        Font font = new Font("Microsoft Sans Serif", Font.PLAIN, 11);

        // Размер экрана
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

        //region Главное Окно
            frame = new JFrame(programTitle);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowListener());

            ImageIcon searchIcon = new ImageIcon(getClass().getResource("/icons/Search16.png"));
            frame.setIconImage(searchIcon.getImage());

            frame.setLocation((int) (screenDimension.getWidth() * 0.25), (int) (screenDimension.getHeight() * 0.25));
            frame.setSize(new Dimension(665, 640));

            fileChooserXML = new JFileChooser();
            fileChooserTXT = new JFileChooser();
            filterXML = new FileNameExtensionFilter("XML-файлы", "xml");
            filterTXT = new FileNameExtensionFilter("Текстовые файлы", "txt");
            fileChooserXML.setFileFilter(filterXML);
            fileChooserTXT.setFileFilter(filterTXT);

            fileName = null;
        //endregion

        //region Остальные окна (Формула, Комментарии, Справка, О программе)
            formulaDialog = new GUIFormula();
            commentsDialog = new GUIComments();
            helpDialog = new GUIHelp();
            aboutDialog = new GUIAbout();
        //endregion

        //region Меню, инструменты, контекстное меню
            JMenuBar menuBar = new JMenuBar();

            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setRollover(true);

            JPopupMenu contextMenu = new JPopupMenu();

            menuBar.setComponentPopupMenu(contextMenu);
            toolBar.setComponentPopupMenu(contextMenu);
        //endregion

        //region Кнопки

            //region Меню
                JMenu fileMenu = new JMenu("Файл");
                fileMenu.setFont(font);
                JMenu algorithmMenu = new JMenu("Алгоритм поиска");
                algorithmMenu.setFont(font);
                JMenu funcMenu = new JMenu("Функция");
                funcMenu.setFont(font);
                searchMenu = new JMenu("Поиск");
                searchMenu.setFont(font);
                JMenu helpMenu = new JMenu("Справка");
                helpMenu.setFont(font);
            //endregion

                //region Файл
                    ImageIcon newIcon = new ImageIcon(getClass().getResource("/icons/New_Blank_Document_16_n_p.png"));
                    JMenuItem fileNew = new JMenuItem("Новый поиск", newIcon);
                    fileNew.setFont(font);
                    fileNew.addActionListener(new NewListener());
                    ImageIcon saveIcon = new ImageIcon(getClass().getResource("/icons/Save_Blue_16_n_p.png"));
                    JMenuItem fileSave = new JMenuItem("Сохранить", saveIcon);
                    fileSave.addActionListener(new SaveListener());
                    fileSave.setFont(font);
                    fileSaveAs = new JMenuItem("Сохранить как...");
                    fileSaveAs.addActionListener(new SaveListener());
                    fileSaveAs.setFont(font);
                    ImageIcon openIcon = new ImageIcon(getClass().getResource("/icons/Open_File_or_Folder_16_n_p.png"));
                    JMenuItem fileOpen = new JMenuItem("Открыть", openIcon);
                    fileOpen.addActionListener(new OpenListener());
                    fileOpen.setFont(font);
                    ImageIcon textIcon = new ImageIcon(getClass().getResource("/icons/Text_Document_16_n_p.png"));
                    JMenuItem fileExport = new JMenuItem("Экспортировать", textIcon);
                    fileExport.addActionListener(new ExportListener());
                    fileExport.setFont(font);
                    fileExportAs = new JMenuItem("Экспортировать как");
                    fileExportAs.addActionListener(new ExportListener());
                    fileExportAs.setFont(font);
                    ImageIcon copyIcon = new ImageIcon(getClass().getResource("/icons/Copy_16_n_p.png"));
                    JMenuItem fileCopy = new JMenuItem("Копировать в буфер обмена", copyIcon);
                    fileCopy.addActionListener(new CopyListener());
                    fileCopy.setFont(font);
                    JMenuItem fileExit = new JMenuItem("Выход");
                    fileExit.setFont(font);
                    fileExit.addActionListener(new ExitListener());
                //endregion

                //region Алгоритм
                    SetMethodListener algorithmListener = new SetMethodListener();
                    algorithmMono = new JMenuItem("Однородный монотонный");
                    algorithmMono.setFont(font);
                    algorithmMono.addActionListener(algorithmListener);
                    algorithmMonoNormal = new JMenuItem("Однор. с н.р.");
                    algorithmMonoNormal.setFont(font);
                    algorithmMonoNormal.addActionListener(algorithmListener);
                    algorithmInhNormal = new JMenuItem("Неоднородный с н.р.");
                    algorithmInhNormal.setFont(font);
                    algorithmInhNormal.addActionListener(algorithmListener);
                    algorithmInhomogeneous = new JMenuItem("Неоднородный");
                    algorithmInhomogeneous.setFont(font);
                    algorithmInhomogeneous.addActionListener(algorithmListener);
                    algorithmIngber = new JMenuItem("Однородный с р.Ингбера");
                    algorithmIngber.setFont(font);
                    algorithmIngber.addActionListener(algorithmListener);
                //endregion

                //region Функция
                    ImageIcon editIcon = new ImageIcon(getClass().getResource("/icons/Edit_16_n_p.png"));
                    JMenuItem setFormula = new JMenuItem("Задать формулу", editIcon);
                    setFormula.setFont(font);
                    FormulaListener formulaListener = new FormulaListener();
                    setFormula.addActionListener(formulaListener);
                //endregion

                //region Поиск
                    ImageIcon next16Icon = new ImageIcon(getClass().getResource("/icons/Forward_or_Next_16_n_p.png"));
                    JMenuItem searchCalculate = new JMenuItem("Выполнить поиск", next16Icon);
                    searchCalculate.setFont(font);
                    searchCalculate.addActionListener(new ButtonCalculateListener());
                    ImageIcon options16Icon = new ImageIcon(getClass().getResource("/icons/Options_1_16_n_p.png"));
                    JMenuItem searchCheckParamsItem = new JMenuItem("Проверить параметры поиска", options16Icon);
                    searchCheckParamsItem.setFont(font);
                    searchCheckParamsItem.addActionListener(new CheckParameters());
                    ImageIcon infoRoundIcon = new ImageIcon(getClass().getResource("/icons/Info_Round_Blue_16_n_p.png"));
                    JMenuItem comments = new JMenuItem("Комментарии к задаче", infoRoundIcon);
                    comments.setFont(font);
                    CommentsListener commentsListener = new CommentsListener();
                    comments.addActionListener(commentsListener);
                //endregion

                //region Справка
                    ImageIcon helpBlueIcon = new ImageIcon(getClass().getResource("/icons/Help_Blue_16_n_p.png"));
                    JMenuItem aboutItem = new JMenuItem("О программе", helpBlueIcon);
                    aboutItem.setFont(font);
                    aboutItem.addActionListener(new AboutListener());
                    ImageIcon helpGreenIcon = new ImageIcon(getClass().getResource("/icons/Help_Green_16_n_p.png"));
                    JMenuItem help = new JMenuItem("Просмотр справки", helpGreenIcon);
                    help.setFont(font);
                    help.addActionListener(new HelpListener());
                    helpPromptItem = new JMenuItem("Всплывающие подсказки");
                    helpPromptItem.setFont(font);
                    helpPromptItem.addActionListener(new HelpPromptListener());
                //endregion

            //region Панель инструментов
                JButton newButton = new JButton(newIcon);
                newButton.setToolTipText("Новый поиск (Ctrl+N)");
                newButton.addActionListener(new NewListener());
                JButton openButton = new JButton(openIcon);
                openButton.setToolTipText("Открыть (Ctrl+O)");
                openButton.addActionListener(new OpenListener());
                JButton saveButton = new JButton(saveIcon);
                saveButton.setToolTipText("Сохранить (Ctrl+S)");
                saveButton.addActionListener(new SaveListener());
                JButton exportButton = new JButton(textIcon);
                exportButton.setToolTipText("Экспортировать");
                exportButton.addActionListener(new ExportListener());
                JButton copyButton = new JButton(copyIcon);
                copyButton.setToolTipText("Копировать в буфер обмена");
                copyButton.addActionListener(new CopyListener());
                searchButton = new JButton(next16Icon);
                searchButton.setToolTipText("Выполнить поиск (F5)");
                searchButton.addActionListener(new ButtonCalculateListener());
                JButton commentsButton = new JButton(infoRoundIcon);
                commentsButton.setToolTipText("Комментарии к задаче (F2)");
                commentsButton.addActionListener(new CommentsListener());
                JButton setFuncButton = new JButton(editIcon);
                setFuncButton.setToolTipText("Задать формулу");
                setFuncButton.addActionListener(new FormulaListener());
            //endregion

            //region Контекстное меню
                contextSearch = new JMenuItem("Выполнить поиск",next16Icon);
                contextSearch.addActionListener(new ButtonCalculateListener());
                JMenuItem contextOpen = new JMenuItem("Открыть",openIcon);
                contextOpen.addActionListener(new OpenListener());
                JMenuItem contextSave = new JMenuItem("Сохранить",saveIcon);
                contextSave.addActionListener(new SaveListener());
                JMenuItem contextCopy = new JMenuItem("Копировать в буфер обмена",copyIcon);
                contextCopy.addActionListener(new CopyListener());
            //endregion

        //endregion

        //region Лейблы
            labelMinFunc = new JLabel("Минимум f");
            labelNumberSteps = new JLabel("Число шагов поиска N");
            labelMStageSteps = new JLabel("Число шагов на этапе m");
            labelV = new JLabel("Внутренний радиус v");
            labelGamma = new JLabel("Внешний радиус Г");
            labelDimension = new JLabel("Размерность пространства оптимизации");
            JLabel labelFormatFunc = new JLabel("Форматирование значения функции");
            JLabel labelFormatPoints = new JLabel("Форматирование точек");
            labelPrecisionFunc = new JLabel("Точность");
            labelPrecisionPoints = new JLabel("Точность");

            labelMinFunc.setFont(font);
            labelNumberSteps.setFont(font);
            labelMStageSteps.setFont(font);
            labelV.setFont(font);
            labelGamma.setFont(font);
            labelDimension.setFont(font);
            labelFormatFunc.setFont(font);
            labelFormatPoints.setFont(font);
            labelPrecisionFunc.setFont(font);
            labelPrecisionPoints.setFont(font);
        //endregion

        //region Текстовые поля
            textFieldMinFunc = new JTextField(10);
            textFieldMinFunc.setFont(font);
            textFieldMinFunc.setEditable(false);
            textFieldV = new JTextField(13);
            textFieldGamma = new JTextField(13);
            textFieldDimension = new JTextField(12);
            textFieldDimension.setText("" + ObjectiveFunction.dimension);
            textFieldDimension.setEditable(false);
            textFieldDimension.setFont(font);
            textFieldUseFormula = new JTextField(10);
            textFieldUseFormula.setEditable(false);
            textFieldUseFormula.setText("Используем код функции");
            textFieldProgressInfo = new JTextField(10);
            textFieldProgressInfo.setEditable(false);
        //endregion

        //region Чекбоксы
            // Чекбокс ГПЧ
            checkBoxRandomGenerator = new JCheckBox("Инициализировать генератор");
            checkBoxRandomGenerator.setFont(font);
            checkBoxRandomGenerator.addActionListener(new CheckBoxListener());

            // Чекбокс информации выполнения поиска
            checkBoxProgressInfoSteps = new JCheckBox("Выводить шаги поиска с интервалом:");
            checkBoxProgressInfoSteps.setFont(font);
            checkBoxProgressInfoSteps.addActionListener(new CheckBoxListener());
        //endregion

        //region Спиннеры(счетчики)

            // Счетчик N
            Long spinnerValue = 1000L;
            Long spinnerMin = 0L;
            Long spinnerMax = 100000000000L;
            Long spinnerStep = 1L;

            SpinnerModel spinnerModelNumberSteps = new SpinnerNumberModel(spinnerValue, spinnerMin, spinnerMax, spinnerStep);
            spinnerNumberSteps = new JSpinner(spinnerModelNumberSteps);

            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) spinnerNumberSteps.getEditor();
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            spinnerEditor.getTextField().setColumns(12);

            // Счетчик M
            spinnerValue = 10L;
            SpinnerModel spinnerModelMStageSteps = new SpinnerNumberModel(spinnerValue, spinnerMin, spinnerMax, spinnerStep);
            spinnerMStageSteps = new JSpinner(spinnerModelMStageSteps);
            spinnerMStageSteps.addChangeListener(new SpinnerM());

            spinnerEditor = (JSpinner.DefaultEditor) spinnerMStageSteps.getEditor();
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            spinnerEditor.getTextField().setColumns(12);

            // Счетчик форматирования Функции
            SpinnerModel spinnerModelPrecisionFunc = new SpinnerNumberModel(6, 0, 14, 1);
            spinnerPrecisionFunc = new JSpinner(spinnerModelPrecisionFunc);
            spinnerPrecisionFunc.addChangeListener(new spinnerFuncPrecisionListener());

            spinnerEditor = (JSpinner.DefaultEditor) spinnerPrecisionFunc.getEditor();
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            spinnerEditor.getTextField().setColumns(4);

            // Счетчик форматирования точек
            SpinnerModel spinnerModelPrecisionPoints = new SpinnerNumberModel(6, 0, 14, 1);
            spinnerPrecisionPoints = new JSpinner(spinnerModelPrecisionPoints);
            spinnerPrecisionPoints.addChangeListener(new spinnerPointsPrecisionListener());

            spinnerEditor = (JSpinner.DefaultEditor) spinnerPrecisionPoints.getEditor();
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            spinnerEditor.getTextField().setColumns(4);

            // Счетчик ГПЧ
            Integer spinnerValueInt = 100;
            Integer spinnerMinInt = 0;
            Integer spinnerMaxInt = 2147483646;
            Integer spinnerStepxInt = 1;
            SpinnerNumberModel spinnerModelRandomGenerator = new SpinnerNumberModel(spinnerValueInt, spinnerMinInt, spinnerMaxInt, spinnerStepxInt);
            spinnerRandomGenerator = new JSpinner(spinnerModelRandomGenerator);
            spinnerRandomGenerator.addChangeListener(new spinnerGeneratorListener());
            spinnerRandomGenerator.setEnabled(false);

            spinnerEditor = (JSpinner.DefaultEditor) spinnerRandomGenerator.getEditor();
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            spinnerEditor.getTextField().setColumns(7);

            // Счетчик информации выполнения поиска
            spinnerValue = 1000000L;
            spinnerMin = 1L;
            spinnerMax = 10000000000L;
            spinnerStep = 1L;
            SpinnerNumberModel spinnerModelProgressInfo = new SpinnerNumberModel(spinnerValue, spinnerMin, spinnerMax, spinnerStep);
            spinnerProgressInfo = new JSpinner(spinnerModelProgressInfo);
            spinnerProgressInfo.setEnabled(false);

            spinnerEditor = (JSpinner.DefaultEditor) spinnerProgressInfo.getEditor();
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            spinnerEditor.getTextField().setColumns(7);
        //endregion

        //region Кнопки на панели
            ImageIcon next48Icon = new ImageIcon(getClass().getResource("/icons/Forward_or_Next_48_n_p.png"));
            buttonCalculate = new JButton("Поиск", next48Icon);
            buttonCalculate.addActionListener(new ButtonCalculateListener());

            ImageIcon options24Icon = new ImageIcon(getClass().getResource("/icons/Options_1_24_n_p.png"));
            buttonCheckParameters = new JButton("Проверить параметры поиска", options24Icon);
            buttonCheckParameters.addActionListener(new CheckParameters());

            buttonSetFunc = new JButton("Задать формулу", editIcon);
            buttonSetFunc.addActionListener(new FormulaListener());

            buttonCalculate.setFont(font);
            buttonCheckParameters.setFont(font);
            buttonSetFunc.setFont(font);
        //endregion

        //region Радиокнопки

            // Радиокнопки для форматирования функции

            radioButtonFuncE.setFont(font);
            radioButtonFuncF.setFont(font);
            radioButtonFuncG.setFont(font);

            radioButtonFuncListener radioButtonsFuncListener = new radioButtonFuncListener();
            radioButtonFuncE.addActionListener(radioButtonsFuncListener);
            radioButtonFuncF.addActionListener(radioButtonsFuncListener);
            radioButtonFuncG.addActionListener(radioButtonsFuncListener);

            ButtonGroup groupRadioButtonsFunc = new ButtonGroup();
            groupRadioButtonsFunc.add(radioButtonFuncE);
            groupRadioButtonsFunc.add(radioButtonFuncF);
            groupRadioButtonsFunc.add(radioButtonFuncG);

            radioButtonFuncG.setSelected(true);

            // Радиокнопки для форматирования точек

            radioButtonPointsListener radioButtonsPointsListener = new radioButtonPointsListener();
            radioButtonPointsE.addActionListener(radioButtonsPointsListener);
            radioButtonPointsF.addActionListener(radioButtonsPointsListener);
            radioButtonPointsG.addActionListener(radioButtonsPointsListener);

            radioButtonPointsE.setFont(font);
            radioButtonPointsF.setFont(font);
            radioButtonPointsG.setFont(font);

            ButtonGroup groupRadioButtonsPoints = new ButtonGroup();
            groupRadioButtonsPoints.add(radioButtonPointsE);
            groupRadioButtonsPoints.add(radioButtonPointsF);
            groupRadioButtonsPoints.add(radioButtonPointsG);

            radioButtonPointsG.setSelected(true);
        //endregion

        //region Панели
            JPanel panelMain = new JPanel();
            JPanel panelSearch = new JPanel();
            panelParameters = new JPanel();
            panelFormatFunc = new JPanel();
            panelFormatPoints = new JPanel();
            panelRandomGenerator = new JPanel();
            panelProgressInfo = new JPanel();
        //endregion

        //region Таблицы
            pointsTableModel = new PointsModel();
            pointsTable = new JTable(pointsTableModel);



            //-----------------------

          //  TableColumn col = pointsTable.getColumnModel().getColumn(0);
           // col.setCellEditor(new PointsTableCellEditor());

            //-----------------------
            pointsTableRepaint();

            //Слушатель для выбора всех ячеек таблицы при нажатии на верхнюю левую ячейку
            pointsTable.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (pointsTable.getTableHeader().columnAtPoint(evt.getPoint()) == 0) pointsTable.selectAll();}});

            scrollPanePointsTable = new JScrollPane(pointsTable);
            scrollPanePointsTable.setComponentPopupMenu(contextMenu);

            paramsTableModel = new ParamsModel();
            paramsTableModel.setValueAt(dimension, 0, 0);
        //endregion

        //region Треугольники в таблице (пока Отключено)
            /*
            //Слушатель для рисования треугольника при выборе ячейки
            pointsTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = pointsTable.rowAtPoint(evt.getPoint());
                    repaint();
                    if (row < 10)
                        pointsTable.setValueAt("►      " + row + "   ", row, 0);
                    else if (row < 100)
                        pointsTable.setValueAt("►     " + row + "   ", row, 0);
                    else
                        pointsTable.setValueAt("►   " + row + "   ", row, 0);
                }
            });
            */
        //endregion

        //region Разделитель окна (в левой части таблица, в правой панель)
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanePointsTable, panelMain);
            splitPane.setResizeWeight(0.8);
            Dimension minimumSize = new Dimension(0, 0);
            splitPane.getLeftComponent().setMinimumSize(minimumSize);
            splitPane.getRightComponent().setMinimumSize(minimumSize);
            splitPane.setDividerLocation((int)(frame.getWidth()*0.437));
            //splitPane.setDividerLocation(290);
            splitPane.setComponentPopupMenu(contextMenu);
        //endregion

        //region Добавление элементов на окно
            frame.add(splitPane);

            fileMenu.add(fileNew);
            fileMenu.add(fileOpen);
            fileMenu.add(fileSave);
            fileMenu.add(fileSaveAs);
            fileMenu.addSeparator();
            fileMenu.add(fileExport);
            fileMenu.add(fileExportAs);
            fileMenu.add(fileCopy);
            fileMenu.addSeparator();
            fileMenu.add(fileExit);

            algorithmMenu.add(algorithmMono);
            algorithmMenu.add(algorithmMonoNormal);
            algorithmMenu.add(algorithmInhNormal);
            algorithmMenu.add(algorithmInhomogeneous);
            algorithmMenu.add(algorithmIngber);

            funcMenu.add(setFormula);

            searchMenu.add(searchCalculate);
            searchMenu.addSeparator();
            searchMenu.add(searchCheckParamsItem);
            searchMenu.add(comments);

            helpMenu.add(aboutItem);
            helpMenu.add(help);
            helpMenu.add(helpPromptItem);

            fileNew.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
            fileOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
            fileSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
            searchCalculate.setAccelerator(KeyStroke.getKeyStroke("F5"));
            help.setAccelerator(KeyStroke.getKeyStroke("F1"));
            comments.setAccelerator(KeyStroke.getKeyStroke("F2"));

            menuBar.add(fileMenu);
            menuBar.add(algorithmMenu);
            menuBar.add(funcMenu);
            menuBar.add(searchMenu);
            menuBar.add(helpMenu);

            toolBar.add(newButton);
            toolBar.add(openButton);
            toolBar.add(saveButton);
            toolBar.addSeparator();
            toolBar.add(exportButton);
            toolBar.add(copyButton);
            toolBar.addSeparator();
            toolBar.add(searchButton);
            toolBar.add(commentsButton);
            toolBar.add(setFuncButton);

            contextMenu.add(contextSearch);
            contextMenu.addSeparator();
            contextMenu.add(contextOpen);
            contextMenu.add(contextSave);
            contextMenu.addSeparator();
            contextMenu.add(contextCopy);

            frame.setJMenuBar(menuBar);
            frame.add(toolBar, BorderLayout.PAGE_START);
            frame.getRootPane().setDefaultButton(buttonCalculate);
        //endregion

        //region Расстановка элементов

            GridBagLayout layout = new GridBagLayout();

            panelMain.setLayout(layout);
            panelSearch.setLayout(layout);
            JPanel panel1 = new JPanel(layout);
            JPanel panel2 = new JPanel(layout);
            JPanel panel3 = new JPanel(layout);
            panelParameters.setLayout(layout);
            panelFormatFunc.setLayout(layout);
            panelFormatPoints.setLayout(layout);
            panelRandomGenerator.setLayout(layout);
            panelProgressInfo.setLayout(layout);

            TitledBorder border = BorderFactory.createTitledBorder("Параметры поиска");
            border.setTitleFont(font);
            panelParameters.setBorder(border);

            border = BorderFactory.createTitledBorder("Форматирование значения функции");
            border.setTitleFont(font);
            panelFormatFunc.setBorder(border);

            border = BorderFactory.createTitledBorder("Форматирование точек");
            border.setTitleFont(font);
            panelFormatPoints.setBorder(border);

            border = BorderFactory.createTitledBorder("Инициализация генератора псевдослучайных чисел");
            border.setTitleFont(font);
            panelRandomGenerator.setBorder(border);
            border = BorderFactory.createTitledBorder("Информация о выполнении поиска");
            border.setTitleFont(font);
            panelProgressInfo.setBorder(border);

            //region Добавление панелей и компонентов на панели
                GridBagConstraints c = new GridBagConstraints();
                c.insets = new Insets(3, 3, 3, 3);

                c.gridx = 0;
                c.gridy = 0;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.NORTHWEST;
                c.weightx = 0;
                c.weighty = 0;
                panelMain.add(panelSearch, c);

                c.gridy = 1;
                panelMain.add(panelParameters, c);

                c.gridy = 2;
                panelMain.add(panelFormatFunc, c);

                c.gridy = 3;
                panelMain.add(panelFormatPoints, c);

                c.gridy = 4;
                panelMain.add(panelRandomGenerator, c);

                c.gridy = 5;
                panelMain.add(panelProgressInfo, c);

                c.insets = new Insets(0, 1, 0, 1);
                c.gridy = 0;
                panelParameters.add(panel1,c);
                c.gridy = 1;
                panelParameters.add(panel2,c);
                c.gridy = 2;
                panelParameters.add(panel3,c);

            //region Строка минимума функции и кнопки вычислить
                c.insets = new Insets(3, 3, 3, 3);
                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = 1;
                c.weightx = 0;
                c.anchor = GridBagConstraints.WEST;
                c.fill = GridBagConstraints.NONE;
                panelSearch.add(labelMinFunc, c);

                c.gridx = 1;
                c.weightx = 1;
                c.anchor = GridBagConstraints.CENTER;
                c.fill = GridBagConstraints.HORIZONTAL;
                textFieldMinFunc.setMinimumSize(new Dimension(110,20));
                panelSearch.add(textFieldMinFunc, c);

                c.gridx = 2;
                c.weightx = 0;
                c.gridwidth = 1;
                c.anchor = GridBagConstraints.EAST;
                c.fill = GridBagConstraints.NONE;
                panelSearch.add(buttonCalculate, c);
            //endregion

            //region Панель параметров

                //region 1 строка (Шаги поиска)

                    c.insets = new Insets(3, 3, 3, 3);
                    c.gridx = 0;
                    c.gridy = 0;
                    c.gridwidth = 1;
                    c.weightx = 0;
                    c.weighty = 0;
                    c.anchor = GridBagConstraints.WEST;
                    c.fill = GridBagConstraints.NONE;
                    panel1.add(labelNumberSteps, c);

                    c.gridx = 1;
                    c.weightx = 1;
                    panel1.add(spinnerNumberSteps, c);

                //endregion

                //region 2 строка (Шаги на этапе поиска)
                    c.gridx = 0;
                    c.gridy = 1;
                    c.weightx = 0;
                    panel1.add(labelMStageSteps, c);

                    c.gridx = 1;
                    c.weightx = 1;
                    panel1.add(spinnerMStageSteps, c);
                //endregion

                //region 3 строка (Поле v)

                    c.gridx = 0;
                    c.gridy = 2;
                    c.weightx = 0;
                    panel1.add(labelV, c);

                    c.gridx = 1;
                    c.weightx = 0.01;
                    textFieldV.setMinimumSize(new Dimension(150,20));
                    panel1.add(textFieldV, c);

                    c.insets = new Insets(3, 150, 3, 0);
                    c.weightx = 0;
                    panel1.add(errorProviderV, c);
                //endregion

                //region 4 строка (Поле G)

                    c.insets = new Insets(3, 3, 3, 3);
                    c.gridx = 0;
                    c.gridy = 3;
                    c.weightx = 0;
                    panel1.add(labelGamma, c);

                    c.gridx = 1;
                    c.weightx = 0.01;
                    textFieldGamma.setMinimumSize(new Dimension(150,20));
                    panel1.add(textFieldGamma, c);

                    c.weightx = 0;
                    c.insets = new Insets(3, 150, 3, 0);
                    panel1.add(errorProviderGamma, c);
                //endregion

                //region 5 строка (Размерность)

                    c.insets = new Insets(3, 3, 3, 3);
                    c.gridx = 0;
                    c.gridy = 0;
                    c.weightx = 0;
                    panel2.add(labelDimension, c);

                    c.gridx = 1;
                    c.weightx = 0;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    textFieldDimension.setMinimumSize(new Dimension(150,20));
                    panel2.add(textFieldDimension, c);
                //endregion

                //region 6 (кнопка Проверить параметры)

                    c.gridx = 0;
                    c.gridy = 1;
                    c.gridwidth = GridBagConstraints.REMAINDER;
                    c.weightx = 0;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    panel2.add(buttonCheckParameters, c);
                //endregion

                //region 7 строка (кнопка Задать функцию)

                    c.gridx = 0;
                    c.gridy = 0;
                    c.weightx = 0.1;
                    c.gridwidth = 1;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    textFieldUseFormula.setMinimumSize(new Dimension(130,20));
                    textFieldUseFormula.setMaximumSize(new Dimension(130,20));
                    panel3.add(textFieldUseFormula, c);

                    c.gridx = 1;
                    c.weightx = 0;
                    c.anchor = GridBagConstraints.EAST;
                  //  c.fill = GridBagConstraints.NONE;
                    panel3.add(buttonSetFunc, c);
                //endregion

                //region Панель форматирования функции

                    c.gridx = 0;
                    c.gridy = 0;
                    c.gridwidth = 1;
                    c.weightx = 0;
                    c.anchor = GridBagConstraints.WEST;
                    c.fill = GridBagConstraints.NONE;
                    panelFormatFunc.add(radioButtonFuncE, c);

                    c.gridx = 1;
                    panelFormatFunc.add(radioButtonFuncF, c);

                    c.gridx = 2;
                    panelFormatFunc.add(radioButtonFuncG, c);

                    c.gridx = 3;
                    c.weightx = 0.2;
                    panelFormatFunc.add(labelPrecisionFunc, c);

                    c.gridx = 4;
                    c.weightx = 3;
                    panelFormatFunc.add(spinnerPrecisionFunc, c);
                //endregion

                //region Панель форматирования точек

                    c.gridx = 0;
                    c.gridy = 0;
                    c.weightx = 0;
                    panelFormatPoints.add(radioButtonPointsE, c);

                    c.gridx = 1;
                    panelFormatPoints.add(radioButtonPointsF, c);

                    c.gridx = 2;
                    panelFormatPoints.add(radioButtonPointsG, c);

                    c.gridx = 3;
                    c.weightx = 0.2;
                    panelFormatPoints.add(labelPrecisionPoints, c);

                    c.gridx = 4;
                    c.weightx = 3;
                    panelFormatPoints.add(spinnerPrecisionPoints, c);
                //endregion

                //region Панель гпч
                    c.gridx = 0;
                    c.gridy = 0;
                    c.gridwidth = 1;
                    c.weightx = 0;
                    panelRandomGenerator.add(checkBoxRandomGenerator, c);

                    c.gridx = 1;
                    c.weightx = 1;
                    c.anchor = GridBagConstraints.WEST;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    panelRandomGenerator.add(spinnerRandomGenerator, c);
                //endregion

                //region Панель информации

                    c.gridx = 0;
                    c.gridy = 0;
                    c.weightx = 0;
                    panelProgressInfo.add(checkBoxProgressInfoSteps, c);

                    c.gridx = 1;
                    c.weightx = 1;
                    panelProgressInfo.add(spinnerProgressInfo, c);

                    c.gridx = 0;
                    c.gridy = 2;
                    c.gridwidth = GridBagConstraints.REMAINDER;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    textFieldProgressInfo.setText("Готово");
                    panelProgressInfo.add(textFieldProgressInfo, c);
                //endregion
            //endregion
        //endregion
        //endregion

        //Начальное состояние
        New();

        frame.setVisible(true);
        buttonCalculate.requestFocus();
    }
    //endregion

    //region Обработчики основных действий
        class ButtonCalculateListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {

                // Задать и проверить параметры
                boolean isCorrect = setAndCheckParameters();
                if (!isCorrect) {
                    JOptionPane.showMessageDialog(frame, "Параметры поиска заданы неправильно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Число строк в таблице точек должно равняться размерности пространства
                if (pointsTable.getRowCount() != dimension) {
                    String info = "Число строк в таблице точек не равно размерности пространства, " + lineSeparator + pointsTable.getRowCount() + " ≠ " + dimension + "!";
                    JOptionPane.showMessageDialog(frame, info, "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Число строк в таблице точек должно равняться размерности пространства для целевой функции
                if (!useFormula && pointsTable.getRowCount() != ObjectiveFunction.dimension) {
                    String info = "Число строк в таблице точек не равно размерности пространства для целевой функции, " + pointsTable.getRowCount() + " ≠ " + ObjectiveFunction.dimension + "!";
                    JOptionPane.showMessageDialog(frame, info, "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Этот фрагмент кода может потребоваться для программной инициализации начальной точки поиска
                //for (int r = 0; r < pointsTable.getRowCount(); r++)
                //    if (pointsTableModel.getValueAt(r,1)==null)
                //        pointsTableModel.setValueAt(1.0, r,1);

                // Создать начальную точку поиска

                Double[] xi = new Double[dimension];

                formattedPoints = new Double[dimension][3];

                // Задать начальную точку поиска
                int i;
                int j = 0;
                for (i = 0; i < pointsTableModel.getRowCount(); i++) {
                    if (pointsTableModel.getValueAt(i, 1) != null)
                        xi[j++] = Double.parseDouble(pointsTableModel.getValueAt(i, 1).toString());
                    else xi[j++] = 0.0;
                    formattedPoints[i][1] = xi[i];
                }

                // Создать объект для выполнения вычислений
                CalculatorExpression calc;

                if (!useFormula)
                {
                    nu = Double.parseDouble(textFieldV.getText());
                    Gamma = Double.parseDouble(textFieldGamma.getText());

                    // Создать объект для выполнения поиска
                    switch (algorithmNumber)
                    {
                        case 1:
                            randomSearchMono = new RandomSearchMono(dimension, ObjectiveFunction.F, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 2:
                            randomSearchMonoNormal = new RandomSearchMonoNormal(dimension, ObjectiveFunction.F, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 3:
                            randomSearchInhNormal = new RandomSearchInhNormal(dimension, ObjectiveFunction.F, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), (long) spinnerMStageSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 4:
                            randomSearchInh = new RandomSearchInhomogeneous(dimension, ObjectiveFunction.F, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), (long) spinnerMStageSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 5:
                            randomSearchIngber = new RandomSearchIngber(dimension, ObjectiveFunction.F, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                    }
                }
                else
                {
                    nu = Double.parseDouble(textFieldV.getText());
                    Gamma = Double.parseDouble(textFieldGamma.getText());
                    // Создать объект для выполнения вычислений
                    calc = new CalculatorExpression(dimension, formulaString);

                    calc.handleFormula();
                    if (calc.hasErrors) {
                        JOptionPane.showMessageDialog(frame, calc.errorMessage, "Формула написана неправильно", JOptionPane.ERROR_MESSAGE);
                        // Настроить информацию о выполнении поиска
                        setProgressInformationStart();
                        timeOfCalculations = ""; // Время выполнения поиска
                        // Значение целевой функции
                        minimumOfFunction = null;
                        textFieldMinFunc.setText("");
                        return;
                    }

                    calc.makeReversePolishNotation();
                    if (calc.hasErrors) {
                        JOptionPane.showMessageDialog(frame, calc.errorMessage, "Формула написана неправильно", JOptionPane.ERROR_MESSAGE);
                        // Настроить информацию о выполнении поиска
                        setProgressInformationStart();
                        timeOfCalculations = ""; // Время выполнения поиска
                        // Значение целевой функции
                        minimumOfFunction = null;
                        textFieldMinFunc.setText("");
                        return;
                    }

                    switch (algorithmNumber)
                    {
                        case 1:
                            randomSearchMono = new RandomSearchMono(dimension, calc.f, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 2:
                            randomSearchMonoNormal = new RandomSearchMonoNormal(dimension, calc.f, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 3:
                            randomSearchInhNormal = new RandomSearchInhNormal(dimension, calc.f, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), (long) spinnerMStageSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 4:
                            randomSearchInh = new RandomSearchInhomogeneous(dimension, calc.f, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), (long) spinnerMStageSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                        case 5:
                            randomSearchIngber = new RandomSearchIngber(dimension, calc.f, nu, Gamma,
                                    (long) spinnerNumberSteps.getValue(), xi, checkBoxProgressInfoSteps.isSelected(),
                                    (long) spinnerProgressInfo.getValue(), checkBoxRandomGenerator.isSelected(), (int) spinnerRandomGenerator.getValue());
                            break;
                    }
                }

                // Отключить кнопки вычисления
                setButtonsCalculateEnabled(false);

                // Настроить рабочее состояние для Информации о выполнении поиска
                setProgressInformationWorking();

                timeStart = java.time.Instant.now();// Время начала поиска

                // Выполняем случайный поиск
                calculateBackground();
            }
        }

        class NewListener implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                if (!isSaved) {
                    int returnVal = JOptionPane.showOptionDialog(frame, "Сохранить результаты вычислений?", "Результаты вычислений не сохранены!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.YES_NO_CANCEL_OPTION, null, options, 1);
                    if (returnVal == JOptionPane.CANCEL_OPTION) return;
                    if (returnVal == JOptionPane.YES_OPTION) {
                        if (fileName == null || Objects.equals(fileName.getName(), "")) {
                            fileChooserXML.setFileFilter(filterXML);
                            returnVal = fileChooserXML.showSaveDialog(frame);
                            if (returnVal != JFileChooser.APPROVE_OPTION) return;
                            fileName = fileChooserXML.getSelectedFile();
                        }
                        writeData();
                        setSaved(true);
                    }
                }
                New();
                fileChooserXML = new JFileChooser();
                fileChooserXML.setFileFilter(filterXML);
                fileChooserTXT = new JFileChooser();
                fileChooserTXT.setFileFilter(filterTXT);
            }
        }

        class OpenListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                fileChooserXML.setFileFilter(filterXML);
                int returnVal = fileChooserXML.showOpenDialog(frame);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;

                while (!fileChooserXML.getSelectedFile().exists())
                {
                    JOptionPane.showMessageDialog(frame, "Файл не найден", "Open", JOptionPane.OK_OPTION, null);
                    returnVal = fileChooserXML.showOpenDialog(frame);
                    if (returnVal != JFileChooser.APPROVE_OPTION) return;
                }
                New();
                fileName = fileChooserXML.getSelectedFile();
                readData();
                buttonCalculate.requestFocus();
            }
        }

        class SaveListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (fileName == null || isWhitespace(fileName.toString()) || e.getSource() == fileSaveAs) {
                    fileChooserXML.setFileFilter(filterXML);
                    int returnVal = fileChooserXML.showSaveDialog(frame);
                    if (returnVal != JOptionPane.YES_OPTION) return;
                    String path;
                    int index = fileChooserXML.getSelectedFile().toString().lastIndexOf('.');
                    if (index > 1)
                    {
                        path = fileChooserXML.getSelectedFile().toString().substring(0, index);
                        fileName = new File(path + ".xml");
                    }
                    else
                    {
                        path = fileChooserXML.getSelectedFile().toString();
                        fileName = new File(path + ".xml");
                    }
                }
                writeData();
                buttonCalculate.requestFocus(); // Активирует элемент управления
            }
        }

        class ExportListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (fileTextName == null || fileTextName.equals("") || e.getSource() == fileExportAs) {
                    fileChooserTXT.setFileFilter(filterTXT);
                    int returnVal = fileChooserTXT.showSaveDialog(frame);
                    if (returnVal != JFileChooser.APPROVE_OPTION) return;
                    String path;
                    int index = fileChooserTXT.getSelectedFile().toString().lastIndexOf('.');
                    if (index > -1)
                    {
                        path = fileChooserTXT.getSelectedFile().toString().substring(0, index);
                        fileTextName = path + ".txt";
                    }
                    else
                    {
                        path = fileChooserTXT.getSelectedFile().toString();
                        fileTextName = path + ".txt";
                    }
                }
                try
                { writeDataToText(); }// Записывает данные в текстовый файл
                catch (IOException e1) {}
                buttonCalculate.requestFocus(); // Активирует элемент управления
            }
        }

        class CopyListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                try
                {
                    String info = convertDataToString() + lineSeparator; // Собираем информацию

                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable transferable = new StringSelection(info);
                    clipboard.setContents(transferable, null);
                } catch (Exception ex) {JOptionPane.showMessageDialog(frame,ex.getMessage(),"Ошибка копирования",JOptionPane.OK_OPTION,null);} // Перехват всех исключений
            }
        }

        class SetMethodListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (e.getSource()==algorithmMono) { setAlgorithmNumber(1); }
                if (e.getSource()==algorithmMonoNormal) { setAlgorithmNumber(2); }
                if (e.getSource()==algorithmInhNormal) { setAlgorithmNumber(3); }
                if (e.getSource()==algorithmInhomogeneous) { setAlgorithmNumber(4); }
                if (e.getSource()==algorithmIngber) { setAlgorithmNumber(5); }
            }
        }

        class FormulaListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {

                formulaDialog.dialog.setLocation(frame.getLocation().x - 15, frame.getLocation().y + 140);
                formulaDialog.dialog.setSize(new Dimension(700, 400));
                frame.setEnabled(false);
                formulaDialog.checkFormula();
                formulaDialog.requestFocus();
                formulaDialog.dialog.setVisible(true);
            }
        }

        class CommentsListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {

                frame.setEnabled(false);
                if (paramsTableModel.getRowCount() == 0) paramsTableModel.params.add(new Params());

                //Текущее значение в Окне комментариев
                String commentsText = commentsDialog.textArea.getText();// Комментарий к задаче

                commentsDialog.setTempText(commentsText);

                // Вывести диалоговое окно
                commentsDialog.dialog.setLocation(100 + frame.getLocation().x, 150 + frame.getLocation().y);
                commentsDialog.dialog.setVisible(true);

            }
        }

        class AboutListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(false);
                aboutDialog.dialog.setLocation(120 + frame.getLocation().x, 100 + frame.getLocation().y);
                aboutDialog.dialog.setVisible(true);
            }
        }

        class HelpListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(false);
                helpDialog.dialog.setTitle("Справка о программе");
                helpDialog.setHelpText();
                helpDialog.dialog.setLocation(frame.getLocation().x - 3, frame.getLocation().y + 140);
                helpDialog.dialog.setSize(670, 410);
                helpDialog.textArea.setCaretPosition(0);
                helpDialog.dialog.setVisible(true);
            }
        }

        class HelpPromptListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (!helpPromptChecked) {
                    helpPromptItem.setIcon(checkIcon);
                    helpPromptChecked = true;
                    setToolTip();
                    formulaDialog.setToolTip();
                } else {
                    helpPromptItem.setIcon(null);
                    helpPromptChecked = false;
                    unsetToolTip();
                    formulaDialog.unsetToolTip();
                }
            }

        }

        class ExitListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                windowClose();
            }
        }

        // Обработчик окна frame(Главного окна)
        class WindowListener extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent e) {
            if (!frame.isEnabled()) {
                if (commentsDialog.dialog.isVisible()){ commentsDialog.dialog.setVisible(true);return; }
                if (helpDialog.dialog.isVisible()){ helpDialog.dialog.setVisible(true);return; }
                if (aboutDialog.dialog.isVisible()){ aboutDialog.dialog.setVisible(true);return; }
                if (!formulaDialog.dialog.isEnabled())
                { formulaDialog.helpDialog.dialog.setVisible(true); return; }
                else
                {formulaDialog.dialog.setVisible(true); return;}
            }
        }

        public void windowClosing(WindowEvent e) {
            windowClose();
        }
    }
    //endregion

    //region Обработчики действий панели
        class SpinnerM implements ChangeListener{
        @Override
        public void stateChanged(ChangeEvent e) {
            if (Double.parseDouble(spinnerMStageSteps.getValue().toString()) > Double.parseDouble(spinnerNumberSteps.getValue().toString()))
                spinnerMStageSteps.setValue(spinnerNumberSteps.getValue());
        }
    }

        class CheckParameters implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setAndCheckParameters();
        }
    }

        class CheckBoxListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == checkBoxRandomGenerator) {
                    if (checkBoxRandomGenerator.isSelected()) {
                        checkBoxRandomGenerator.setSelected(true);
                        spinnerRandomGenerator.setEnabled(true);
                    } else {
                        checkBoxRandomGenerator.setSelected(false);
                        spinnerRandomGenerator.setEnabled(false);
                    }
                } else if (checkBoxProgressInfoSteps.isSelected()) {
                    checkBoxProgressInfoSteps.setSelected(true);
                    spinnerProgressInfo.setEnabled(true);
                } else {
                    checkBoxProgressInfoSteps.setSelected(false);
                    spinnerProgressInfo.setEnabled(false);
                }
            }
        }

        class radioButtonFuncListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (textFieldMinFunc.getText() == null || textFieldMinFunc.getText() == "") return;
                if (e.getSource() == radioButtonFuncE) {
                    radioButtonFuncE.setSelected(true);
                    setFormattedFunc();
                }
                if (e.getSource() == radioButtonFuncG) {
                    radioButtonFuncG.setSelected(true);
                    setFormattedFunc();
                }
                if (e.getSource() == radioButtonFuncF) {
                    radioButtonFuncF.setSelected(true);
                    setFormattedFunc();
                }
            }
        }

        class radioButtonPointsListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == radioButtonPointsE) {
                    radioButtonPointsE.setSelected(true);
                    setFormattedPoints();
                }
                if (e.getSource() == radioButtonPointsG) {
                    radioButtonPointsG.setSelected(true);
                    setFormattedPoints();
                }
                if (e.getSource() == radioButtonPointsF) {
                    radioButtonPointsF.setSelected(true);
                    setFormattedPoints();
                }
            }
        }

        class spinnerFuncPrecisionListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                if (Double.parseDouble(spinnerPrecisionFunc.getValue().toString()) != Math.floor(Double.parseDouble(spinnerPrecisionFunc.getValue().toString())))
                    spinnerPrecisionFunc.setValue(Math.floor(Double.parseDouble(spinnerPrecisionFunc.getValue().toString())));
                if (minimumOfFunction != null || minimumOfFunction.toString() != "")
                    textFieldMinFunc.setText(String.format(getFormatFunctionString(), minimumOfFunction));
                else textFieldMinFunc.setText("");
            }
        }

        class spinnerPointsPrecisionListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                if (Double.parseDouble(spinnerPrecisionPoints.getValue().toString()) != Math.floor(Double.parseDouble(spinnerPrecisionPoints.getValue().toString())))
                    spinnerPrecisionPoints.setValue(Math.floor((Double.parseDouble(spinnerPrecisionPoints.getValue().toString()))));
                setFormattedPoints();
            }
        }

        class spinnerGeneratorListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                Double value = 0.0;
                try {
                    value = Double.parseDouble(spinnerRandomGenerator.getValue().toString());
                } catch (Exception ignored) {
                }
                if (value > 2147483646) spinnerRandomGenerator.setValue(2147483646);
            }
        }
    //endregion

    //region  Класс для вычислений в отдельном потоке
        class CalculateWorker extends SwingWorker<Object,Object> {

        @Override
        // Задаем действия для работы в отдельном потоке
        public String doInBackground() {
            try {
                //Ждать 2 сек, не блокируя интерфейс
                //Thread.currentThread().sleep(2000);

                // Выполняем случайный поиск
                switch (algorithmNumber) {
                    case 1:
                        randomSearchMono.searchSimulation();
                        break;
                    case 2:
                        randomSearchMonoNormal.searchSimulation();
                        break;
                    case 3:
                        randomSearchInhNormal.searchSimulation();
                        break;
                    case 4:
                        randomSearchInh.searchSimulation();
                        break;
                    case 5:
                        randomSearchIngber.searchSimulation();
                        break;
                }
                setButtonsCalculateEnabled(false);
            } catch (IllegalThreadStateException exc) {}
            return null;
        }

        @Override
        // Задаем действия, когда поиск выполнится
        protected void done() {
            int i;
            Double[] xiL = new Double[dimension];

            Instant timeEnd = java.time.Instant.now();// Время завершения поиска

            long timeHour = ChronoUnit.HOURS.between(timeStart, timeEnd);
            long timeMin =  ChronoUnit.MINUTES.between(timeStart, timeEnd) % 60;
            long timeSec =  ChronoUnit.SECONDS.between(timeStart, timeEnd) % 60;
            long timeMS = ChronoUnit.MILLIS.between(timeStart, timeEnd) % 1000;

            // Получить найденное значение целевой функции
            switch (algorithmNumber) {
                case 1:
                    minimumOfFunction = randomSearchMono.fxi;break;
                case 2:
                    minimumOfFunction = randomSearchMonoNormal.fxi;break;
                case 3:
                    minimumOfFunction = randomSearchInhNormal.fxi;break;
                case 4:
                    minimumOfFunction = randomSearchInh.fxi;break;
                case 5:
                    minimumOfFunction = randomSearchIngber.fxi;break;
            }

            textFieldMinFunc.setText(minimumOfFunction.toString());

            // Получить конечную точку поиска
            for (i = 0; i < pointsTableModel.getRowCount(); i++) {
                switch (algorithmNumber) {
                    case 1:
                        xiL[i] = randomSearchMono.xi[i];break;
                    case 2:
                        xiL[i] = randomSearchMonoNormal.xi[i];break;
                    case 3:
                        xiL[i] = randomSearchInhNormal.xi[i];break;
                    case 4:
                        xiL[i] = randomSearchInh.xi[i];break;
                    case 5:
                        xiL[i] = randomSearchIngber.xi[i];break;
                }
                formattedPoints[i][2] = xiL[i];
                pointsTableModel.setValueAt(xiL[i], i, 2);
            }

            // Включить кнопки вычисления
            setButtonsCalculateEnabled(true);

            // Настроить стартовое состояние для Информации о выполнении поиска
            setProgressInformationStart();

            setFormattedFunc();
            setFormattedPoints();
            pointsTableRepaint();

            timeOfCalculations = String.format("%02d:%02d:%02d.%03d", timeHour, timeMin, timeSec, timeMS);// Время выполнения поиска

            textFieldProgressInfo.setText("Готово. Время выполнения " + timeOfCalculations);

            setParametersToTable();

            dataModel.points = pointsTableModel.points;
            dataModel.params = paramsTableModel.params;

            setSaved(false); // Результаты поиска не сохранены
            buttonCalculate.requestFocus();
        }
    }
    //endregion

    //region Методы

        // Задать начальные значения для проверки (Убрать " /* " и " */ " или поставить // перед ' /* ')
        private void setExample() {
            /*
            pointsTableModel.setValueAt(1, 0, 1);
            pointsTableModel.setValueAt(1, 1, 1);
            nu = 1E-100;
            Gamma = 1.0;
            textFieldV.setText("" + nu);
            textFieldGamma.setText("" + Gamma);
            spinnerNumberSteps.setValue(200000L);

            checkBoxProgressInfoSteps.setSelected(true);
            spinnerProgressInfo.setEnabled(true);
            spinnerProgressInfo.setValue(1000L);
            //*/
        }

        // Изначальное состояние
        private void New() {
            fileName = null; // Имя файла с данными
            fileTextName = ""; // Имя текстового файла с данными

            parametersReset();

            paramsTableModel = new ParamsModel();
            pointsTableModel = new PointsModel();

            pointsTable.setModel(pointsTableModel);

            dataModel = new DataModel();
            dataModel.points = pointsTableModel.points;
            dataModel.params = paramsTableModel.params;

            formattedPoints = new Double[dimension][3];

            for (int i = 0; i < dimension; i++) {
                formattedPoints[i][1] = (Double) pointsTable.getValueAt(i, 1);
            //    if (i%2==0)
            //        pointsTableModel.setValueAt(-1.2,i,1);
            //    else
            //        pointsTableModel.setValueAt(1.0,i,1);
            }
            setParametersToTable();
            setFormatInitial();

            // Добавить строку в таблицу параметров
            paramsTableModel.setValueAt(dimension, 0, 0);

            // Метод по умолчанию 1 (Монотонный поиск)
            setAlgorithmNumber(1);

            setExample();

            formulaDialog = new GUIFormula();
            formulaDialog.setInitialParams();
            commentsDialog = new GUIComments();
            helpDialog = new GUIHelp();

            // Убрать всплывающие подсказки
            helpPromptItem.setSelected(false);
            unsetToolTip();
            formulaDialog.unsetToolTip();// Убрать всплывающие подсказки в окне формулы

            errorProviderV.clearError();
            errorProviderGamma.clearError();

            setSaved(true); // Данные сохранены
        }

        // Считывает данные из файла
        private void readData() {
            try {
                //Записываем данные из файла в Общую таблицу
                dataModel = unmarshal(fileName);

                //Записываем параметры из Общей таблицы
                paramsTableModel.params = dataModel.params;

                //Берем параметры
                getParametersFromTable();

                formattedPoints = new Double[dimension][3];

                ////Записываем точки из Общей таблицы и перерисоываем
                pointsTableModel.points = dataModel.points;
                pointsTable.setModel(pointsTableModel);

                formulaDialog.setParams();
                for (int i = 0; i < dimension; i++)
                    for (int j = 1; j < 3; j++)
                        formattedPoints[i][j] = Double.parseDouble(pointsTableModel.getValueAt(i, j).toString());

                setFormattedPoints();
                setFormattedFunc();
            } catch (Exception exception) { JOptionPane.showMessageDialog(frame, exception.getMessage(),"Ошибка ввода", JOptionPane.ERROR_MESSAGE);}
            setSaved(true);
        }

        // Записывает данные в файл
        private void writeData() {
            setParametersToTable();
            if (dataModel.points.size() < pointsTableModel.points.size())
                dataModel = new DataModel();
            dataModel.points = pointsTableModel.points;
            dataModel.params = paramsTableModel.params;
            try
            {
                marshal(dataModel, fileName);
                setSaved(true);
            } catch (Exception exc) { JOptionPane.showMessageDialog(frame, "Ошибка вывода", null, JOptionPane.ERROR_MESSAGE);return; }
        }

        // Записывает данные в текстовый файл
        private void writeDataToText() throws IOException {

            // Записать данные в текстовый файл
            try (FileOutputStream fw = new FileOutputStream(fileTextName);  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fw, StandardCharsets.UTF_8))) {

                String info = convertDataToString(); // Собираем информацию
                writer.write(info);
                setSaved(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка вывода", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Записывает данные в строку
        private String convertDataToString() {

            //  Названия столбцов таблицы параметров
            java.util.List<String> columns = Arrays.asList(ParamsModel.columnNames);
            // Собираем информацию
            // Минимальное значение целевой функции
            String info = "Минимальное значение целевой функции = ";
            info += textFieldMinFunc.getText() + lineSeparator;

            // Размерность пространства оптимизации
            info += "Размерность пространства оптимизации = ";
            info += dimension + lineSeparator;

            String xi0 = ""; // Начальная точка поиска
            String xiN = ""; // Конечная точка поиска

            // Получаем начальную и конечную точки поиска
            // Число строк в таблице точек должно равняться размерности пространства
            if (pointsTable.getRowCount() != dimension) {
                info += "Ошибка. Число строк в таблице точек не равно размерности пространства, " + pointsTable.getRowCount() + " ≠ " + dimension + "!" + lineSeparator;
            } else {
                String formatPoint = getFormatPointString();

                // Задать начальную точку поиска

                for (int row = 0; row < pointsTable.getRowCount(); row++)
                    if (pointsTable.getValueAt(row, 1) != null)
                        xi0 += String.format(formatPoint, (double) pointsTable.getValueAt(row, 1)) + "; ";
                    else xi0 += String.format(formatPoint, 0.0) + "; ";

                xi0 = replaceLast(xi0, ";", "");
                xi0 = replaceLast(xi0, " ", "");

                // Конечная точка поиска
                info += "Конечная точка поиска:" + lineSeparator;
                // Задать конечную точку поиска
                for (int row = 0; row < pointsTable.getRowCount(); row++)
                    if (pointsTable.getValueAt(row, 2) != null)
                        xiN += String.format(formatPoint, (double) pointsTable.getValueAt(row, 2)) + "; ";
                    else xiN += String.format(formatPoint, 0.0) + "; ";
                xiN = replaceLast(xiN, ";", "");
                xiN = replaceLast(xiN, " ", "");
                info += xiN + lineSeparator;
            }

            // Параметры поиска
            info += "Параметры поиска:" + lineSeparator;

            // Число шагов поиска
            info += "Число шагов поиска = " + spinnerNumberSteps.getValue() + lineSeparator;

            // Число шагов поиска
            info += "Число шагов поиска на этапе = " + spinnerMStageSteps.getValue() + lineSeparator;

            // Внутренний радиус v
            info += "Внутренний радиус v = " + textFieldV.getText() + lineSeparator;

            // Внешний радиус Г
            info += "Внешний радиус Г = " + textFieldGamma.getText() + lineSeparator;

            // Начальная точка поиска
            if (xi0 != null) if (!Objects.equals(xi0, "")) {
                info += "Начальная точка поиска:" + lineSeparator;
                info += xi0 + lineSeparator;
            }
            // Использование формулы для вычисления целевой функции
            if (useFormula) info += "Используем формулу:" + lineSeparator + formulaString + lineSeparator;
            else info += "Используем код функции" + lineSeparator;

            // Инициализация генератора псевдослучайных чисел
            if (checkBoxRandomGenerator.isSelected())
                info += "Генератор псевдослучайных чисел инициализирован значением " + spinnerRandomGenerator.getValue() + lineSeparator;
            else info += "Генератор псевдослучайных чисел инициализирован зависящим от времени значением" + lineSeparator;

            // Время выполнения поиска
            if (timeOfCalculations != null) if (!Objects.equals(timeOfCalculations, "")) info += "Время выполнения поиска " + timeOfCalculations + lineSeparator;

            // Комментарий к программе

            if (paramsTableModel.getValueAt(0, columns.indexOf("Comments")) != null)
            {
                if (!paramsTableModel.getValueAt(0, columns.indexOf("Comments")).equals(""))
                {
                    String comments = paramsTableModel.getValueAt(0, columns.indexOf("Comments")).toString();
                    info += "Комментарии к задаче:" + lineSeparator + comments;
                }
            }

            return info;
        }

        // Проверить параметры
        private boolean setAndCheckParameters() {
            boolean isCorrect = true; // Значения v и Г заданы правильно
            isCorrect = setParametersToTable();
            if (!isCorrect) return false;

            if (nu == null)
            {
                errorProviderV.setError("Число v не задано!");
                isCorrect = false;
            }

            if (Gamma == null)
            {
                errorProviderGamma.setError("Число Г не задано!");
                isCorrect = false;
            }

            if ((nu!=null) && (Gamma!=null))
            {
                if (nu > Gamma)
                {
                    errorProviderV.setError("Число v должно быть меньше Г!");
                    errorProviderGamma.setError("Число Г должно быть больше v!");
                    isCorrect = false;
                }
                else if (Objects.equals(nu, Gamma) && (algorithmNumber != 3 && algorithmNumber != 4))
                {
                    errorProviderV.setError("Число v должно быть меньше Г!");
                    errorProviderGamma.setError("Число Г должно быть больше v!");
                    isCorrect = false;
                }
                else
                {
                    errorProviderV.clearError();
                    errorProviderGamma.clearError();
                }
            }
            return isCorrect;
        }

        // Занести параметры в таблицу параметров
        private boolean setParametersToTable() {
            boolean isCorrect = true; // Значения v и Г заданы правильно

            //  Названия столбцов таблицы параметров
            java.util.List<String> columns = Arrays.asList(ParamsModel.columnNames);

            // Выбор алгоритма
            paramsTableModel.setValueAt(algorithmNumber, 0, columns.indexOf("Algorithm"));

            // Размерность пространства оптимизации
            paramsTableModel.setValueAt(dimension, 0, columns.indexOf("dimension"));

            // Число шагов поиска
            paramsTableModel.setValueAt(spinnerNumberSteps.getValue(), 0, columns.indexOf("NumberOfSteps"));

            // Число шагов поиска на этапе
            paramsTableModel.setValueAt(spinnerMStageSteps.getValue(), 0, columns.indexOf("NumberOfStageSteps"));

            // Внутренний радиус v
            Double nuValue = null;
            try { nuValue = Double.parseDouble(textFieldV.getText()); } catch (Exception ignored) {}
            if (textFieldV.getText() == null)
            {
                nu = null;
                paramsTableModel.setValueAt(null, 0, columns.indexOf("nu"));
                errorProviderV.clearError();
            }
            else if(isWhitespace(textFieldV.getText()) || textFieldV.getText().equals(""))
            {   nu = null;
                paramsTableModel.setValueAt(null, 0, columns.indexOf("nu"));
                errorProviderV.clearError();
            }
            else if (nuValue != null)
            {
                if (nuValue > 0) {
                    nu = nuValue;
                    paramsTableModel.setValueAt(nuValue, 0, columns.indexOf("nu"));
                    errorProviderV.clearError();
                } else {
                    nu = null;
                    paramsTableModel.setValueAt(nuValue, 0, columns.indexOf("nu"));
                    errorProviderV.setError("Число v должно быть положительно!");
                    isCorrect = false;
                }
            }
            else
            {
                nu = null;
                paramsTableModel.setValueAt(nuValue, 0, columns.indexOf("nu"));
                errorProviderV.setError("Число v написано неправильно!");
                isCorrect = false;
            }

            // Внешний радиус Г
            Double GammaValue = null;
            try { GammaValue = Double.parseDouble(textFieldGamma.getText()); } catch (Exception ex) {
            }
            if (textFieldGamma.getText() == null)
            {
                    Gamma = null;
                    paramsTableModel.setValueAt(null, 0, columns.indexOf("Gamma"));
                    errorProviderGamma.clearError();
            }
            else if (isWhitespace(textFieldGamma.getText()) || textFieldGamma.getText().equals(""))
            {
                Gamma = null;
                paramsTableModel.setValueAt(null, 0, columns.indexOf("Gamma"));
                errorProviderGamma.clearError();
            }
            else if (GammaValue != null)
                {
                    if (GammaValue > 0)
                    {
                        Gamma = GammaValue;
                        paramsTableModel.setValueAt(Gamma, 0, columns.indexOf("Gamma"));
                        errorProviderGamma.clearError();
                    }
                    else
                    {
                        Gamma = null;
                        paramsTableModel.setValueAt(null, 0, columns.indexOf("Gamma"));
                        errorProviderGamma.setError("Число Г должно быть положительно!");
                        isCorrect = false;
                    }
                }
            else
            {
                Gamma = null;
                paramsTableModel.setValueAt(null, 0, columns.indexOf("Gamma"));
                errorProviderGamma.setError("Число Г написано неправильно!");
                isCorrect = false;
            }
            if (nu != null && Gamma != null)
                if ((nu > Gamma) || (Objects.equals(nu, Gamma) && algorithmNumber != 3 && algorithmNumber != 4))
                {
                        nu = null;
                        paramsTableModel.setValueAt(null, 0, columns.indexOf("nu"));
                        errorProviderV.setError("Число v должно быть меньше Г!");

                        Gamma = null;
                        paramsTableModel.setValueAt(null, 0, columns.indexOf("Gamma"));
                        errorProviderGamma.setError("Число Г должно быть больше v!");
                        isCorrect = false;
                }

            // Минимальное значение целевой функции
            if (minimumOfFunction != null) paramsTableModel.setValueAt(minimumOfFunction, 0, columns.indexOf("MinimumOfFunction"));
            else paramsTableModel.setValueAt(null, 0, columns.indexOf("MinimumOfFunction"));

            // Форматирование функции
            paramsTableModel.setValueAt(getFormatFunctionSymbol(), 0, columns.indexOf("FormatFunctionSymbol"));
            paramsTableModel.setValueAt(spinnerPrecisionFunc.getValue(), 0, columns.indexOf("FormatFunctionPrecision"));

            // Форматирование точек
            paramsTableModel.setValueAt(getFormatPointSymbol(), 0, columns.indexOf("FormatPointSymbol"));
            paramsTableModel.setValueAt(spinnerPrecisionPoints.getValue(), 0, columns.indexOf("FormatPointPrecision"));

            // Инициализация генератора псевдослучайных чисел
            paramsTableModel.setValueAt(checkBoxRandomGenerator.isSelected(), 0, columns.indexOf("GeneratorInitialize"));
            paramsTableModel.setValueAt(spinnerRandomGenerator.getValue(), 0, columns.indexOf("GeneratorSeed"));

            // Информация о выполнении поиска
            paramsTableModel.setValueAt(checkBoxProgressInfoSteps.isSelected(), 0, columns.indexOf("ShowProgressInformation"));
            paramsTableModel.setValueAt(spinnerProgressInfo.getValue(), 0, columns.indexOf("StepsIntervalForProgressInformation"));
            paramsTableModel.setValueAt(timeOfCalculations, 0, columns.indexOf("TimeOfCalculations"));

            paramsTableModel.setValueAt(commentsDialog.getText(), 0, columns.indexOf("Comments"));
            // Использование формулы для вычисления целевой функции
            paramsTableModel.setValueAt(formulaDialog.getUseFormula(), 0, columns.indexOf("UseFormula"));// Нужно ли использовать формулу
            paramsTableModel.setValueAt(formulaDialog.getFormulaString(), 0, columns.indexOf("FormulaString"));// Строка с формулой

            return isCorrect;
        }

        // Параметры из таблицы переносятся в элементы управления и переменные
        private void getParametersFromTable() {

            //  Названия столбцов таблицы параметров
            java.util.List<String> columns = Arrays.asList(ParamsModel.columnNames);

            if (paramsTableModel.getRowCount() == 0) paramsTableModel.params.add(0, new Params());
            Object[] row = new Object[columns.size()+1];
            for (int j = 0; j < paramsTableModel.getColumnCount(); j++)
                row[j] = paramsTableModel.getValueAt(0, j);

            // Алгоритм поиска
            if (row[columns.indexOf("Algorithm")] != null) {
                if (!Objects.equals(row[columns.indexOf("Algorithm")].toString(), "") && (int) row[columns.indexOf("Algorithm")] > 0)
                    setAlgorithmNumber((int) row[columns.indexOf("Algorithm")]);
            }

            // Размерность пространства оптимизации
            if (row[columns.indexOf("dimension")] != null) {
                if (!Objects.equals(row[columns.indexOf("dimension")].toString(), "") && (int) row[columns.indexOf("dimension")] > 0)
                    dimension = (int) row[columns.indexOf("dimension")];
            }
            textFieldDimension.setText(dimension + "");

            // Число шагов поиска N
            if (row[columns.indexOf("NumberOfSteps")] != null) {
                if (!Objects.equals(row[columns.indexOf("NumberOfSteps")].toString(), "")) spinnerNumberSteps.setValue(row[columns.indexOf("NumberOfSteps")]);
            } else spinnerNumberSteps.setValue(numberOfStepsInitial);

            // Число шагов поиска на этапе m
            if (row[columns.indexOf("NumberOfStageSteps")] != null) {
                if (!Objects.equals(row[columns.indexOf("NumberOfStageSteps")].toString(), "")) spinnerMStageSteps.setValue(row[columns.indexOf("NumberOfStageSteps")]);
            } else spinnerMStageSteps.setValue(numberOfStageStepsInitial);

            // Внутренний радиус v
            if (row[columns.indexOf("nu")] != null) {
                if (!Objects.equals(row[columns.indexOf("nu")].toString(), "")) {
                    nu = (double) row[columns.indexOf("nu")];
                    textFieldV.setText(nu.toString());
                }
            } else {
                nu = null;
                textFieldV.setText("");
            }
            // Внешний радиус Г
            if (row[columns.indexOf("Gamma")] != null) {
                if ((!Objects.equals(row[columns.indexOf("Gamma")].toString(), ""))) {
                    Gamma = (double) row[columns.indexOf("Gamma")];
                    textFieldGamma.setText(Gamma.toString());
                }
            } else {
                Gamma = null;
                textFieldGamma.setText("");
            }

            // Минимальное значение целевой функции
            if (row[columns.indexOf("MinimumOfFunction")] != null && !isWhitespace(row[columns.indexOf("MinimumOfFunction")].toString()) && row[columns.indexOf("MinimumOfFunction")].toString() != "") {
                minimumOfFunction = (double) row[columns.indexOf("MinimumOfFunction")];
                textFieldMinFunc.setText(String.format(getFormatFunctionString(), minimumOfFunction));
            } else {
                minimumOfFunction = null;
                textFieldMinFunc.setText("");
            }

            // Форматирование функции
            if (row[columns.indexOf("FormatFunctionSymbol")] != null && !isWhitespace(row[columns.indexOf("FormatFunctionSymbol")].toString()) && row[columns.indexOf("FormatFunctionSymbol")].toString() != "")
                setFormatFunctionSymbol(row[columns.indexOf("FormatFunctionSymbol")].toString());
            else setFormatFunctionSymbol("G");

            if (row[columns.indexOf("FormatFunctionPrecision")] != null && !isWhitespace(row[columns.indexOf("FormatFunctionPrecision")].toString()) && row[columns.indexOf("FormatFunctionPrecision")].toString() != "")
                spinnerPrecisionFunc.setValue(row[columns.indexOf("FormatFunctionPrecision")]);
            else spinnerPrecisionFunc.setValue(precisionInitial);

            // Форматирование точек
            if (row[columns.indexOf("FormatPointSymbol")] != null && !isWhitespace(row[columns.indexOf("FormatPointSymbol")].toString()) && row[columns.indexOf("FormatPointSymbol")].toString() != "")
                setFormatPointSymbol(row[columns.indexOf("FormatPointSymbol")].toString());
            else setFormatPointSymbol("G");

            if (row[columns.indexOf("FormatPointPrecision")] != null && !isWhitespace(row[columns.indexOf("FormatPointPrecision")].toString()) && row[columns.indexOf("FormatPointPrecision")].toString() != "")
                spinnerPrecisionPoints.setValue(row[columns.indexOf("FormatPointPrecision")]);
            else spinnerPrecisionPoints.setValue(precisionInitial);

            // Инициализация генератора псевдослучайных чисел
            // Нужно ли задавать инициализатор генератора псевдослучайных чисел
            if (row[columns.indexOf("GeneratorInitialize")] != null && !isWhitespace(row[columns.indexOf("GeneratorInitialize")].toString()) && row[columns.indexOf("GeneratorInitialize")].toString() != "") {
                checkBoxRandomGenerator.setSelected((boolean) row[columns.indexOf("GeneratorInitialize")]);
                spinnerRandomGenerator.setEnabled((boolean) row[columns.indexOf("GeneratorInitialize")]);
            } else {
                checkBoxRandomGenerator.setSelected(generatorInitializeInitial);
                spinnerRandomGenerator.setEnabled(generatorInitializeInitial);
            }
            // Число, используемое для вычисления начального значения последовательности псевдослучайных чисел
            if (row[columns.indexOf("GeneratorSeed")] != null && !isWhitespace(row[columns.indexOf("GeneratorSeed")].toString()) && row[columns.indexOf("GeneratorSeed")].toString() != "")
                spinnerRandomGenerator.setValue(row[columns.indexOf("GeneratorSeed")]);
            else spinnerRandomGenerator.setValue(generatorSeedInitial);

            // Информация о выполнении поиска
            // Нужно ли показывать информацию о выполнении поиска
            if (row[columns.indexOf("ShowProgressInformation")] != null) {
                if ((!Objects.equals(row[columns.indexOf("ShowProgressInformation")].toString(), ""))) {
                    checkBoxProgressInfoSteps.setSelected((boolean) row[columns.indexOf("ShowProgressInformation")]);
                    spinnerProgressInfo.setEnabled((boolean) row[columns.indexOf("ShowProgressInformation")]);
                }
            } else {
                checkBoxProgressInfoSteps.setSelected(showProgressInfoInitial);
                spinnerProgressInfo.setEnabled(showProgressInfoInitial);
            }
            // Интервал для показа шагов поиска
            if (row[columns.indexOf("StepsIntervalForProgressInformation")] != null) {
                if ((!Objects.equals(row[columns.indexOf("StepsIntervalForProgressInformation")].toString(), ""))) spinnerProgressInfo.setValue(row[columns.indexOf("StepsIntervalForProgressInformation")]);
            } else spinnerProgressInfo.setValue(stepsIntervalForProgressInfoInitial);

            // Время выполнения поиска
            if (row[columns.indexOf("TimeOfCalculations")] != null) timeOfCalculations = row[columns.indexOf("TimeOfCalculations")].toString();
            else timeOfCalculations = "";
            if (timeOfCalculations != null) {
                if ((!Objects.equals(timeOfCalculations, "")))
                    textFieldProgressInfo.setText("Готово. Время выполнения " + timeOfCalculations);
            } else textFieldProgressInfo.setText("Готово");

            //Комментарии
            if (row[columns.indexOf("Comments")] != null) {
                if ((!Objects.equals(row[columns.indexOf("Comments")].toString(), "")))
                {
                    commentsDialog.textArea.setText(row[columns.indexOf("Comments")].toString());
                    commentsDialog.setTempText(row[columns.indexOf("Comments")].toString());
                }
            }
            else
            {
                commentsDialog.textArea.setText("");
                commentsDialog.setTempText("");
            }

            // Использование формулы для вычисления целевой функции
            if (row[columns.indexOf("UseFormula")] != null) {
                if ((!Objects.equals(row[columns.indexOf("UseFormula")].toString(), ""))) // Нужно ли использовать формулу
                    useFormula = (boolean) row[columns.indexOf("UseFormula")];
            } else useFormula = false;

            if (useFormula) // Нужно ли использовать формулу
            {
                textFieldUseFormula.setText("Используем формулу");
                formulaDialog.setUseFormula(useFormula);
                formulaDialog.setDimension(dimension);
            }
            else
            {
                textFieldUseFormula.setText("Используем код функции");
                formulaDialog.setUseFormula(useFormula);
            }

            if (row[columns.indexOf("FormulaString")] != null) {
                if (!Objects.equals(row[columns.indexOf("FormulaString")].toString(), "")) // Строка с формулой
                {
                    formulaString = row[columns.indexOf("FormulaString")].toString();
                    formulaDialog.setFormulaString(formulaString);
                }
                else {formulaString = "";formulaDialog.setFormulaString(formulaString);}
            } else {formulaString = "";formulaDialog.setFormulaString(formulaString);}
        }

        // Устанавливает параметры в исходное состояние
        private void parametersReset() {
            dimension = ObjectiveFunction.dimension; // Размерность пространства оптимизации

            // Использование формулы для вычисления целевой функции
            useFormula = false; // Нужно ли использовать формулу
            formulaString = ""; // Строка с формулой

            setParametersInitial();

            minimumOfFunction = null; // Минимальное значение целевой функции
            textFieldMinFunc.setText("");

            nu = null; // Внутренний радиус v
            textFieldV.setText("");

            Gamma = null; // Внешний радиус Г
            textFieldGamma.setText("");

            errorProviderV.clearError();
            errorProviderGamma.clearError();

            // Настроить начальное состояние для Информации о выполнении поиска
            setProgressInformationInitial();
        }

        // Установить начальные значения параметров
        private void setParametersInitial() {
            spinnerNumberSteps.setValue(numberOfStepsInitial);// Число шагов поиска
            spinnerMStageSteps.setValue(numberOfStageStepsInitial); // Число шагов поиска на этапе

            textFieldDimension.setText(dimension + ""); // Размерность пространства оптимизации
            if (useFormula) // Нужно ли использовать формулу
                textFieldUseFormula.setText("Используем формулу");
            else textFieldUseFormula.setText("Используем код функции");

            // Инициализация генератора случайных чисел
            checkBoxRandomGenerator.setSelected(generatorInitializeInitial);
            spinnerRandomGenerator.setValue(generatorSeedInitial);
            spinnerRandomGenerator.setEnabled(checkBoxRandomGenerator.isSelected());
        }

        // Устанавливает числовые форматы в исходное состояние
        private void setFormatInitial() {
            radioButtonFuncG.setSelected(true);
            spinnerPrecisionFunc.setValue(precisionInitial);

            radioButtonPointsG.setSelected(true);
            spinnerPrecisionPoints.setValue(precisionInitial);

            setFormattedPoints();
            setFormattedFunc();
        }

        // Устанавливает символ числового формата для значения целевой функции
        private void setFormatFunctionSymbol(String symbol) {
            if (Objects.equals(symbol, "E") || Objects.equals(symbol, "e")) radioButtonFuncE.setSelected(true);
            else if (Objects.equals(symbol, "F") || Objects.equals(symbol, "f")) radioButtonFuncF.setSelected(true);
            else if (Objects.equals(symbol, "G") || Objects.equals(symbol, "g")) radioButtonFuncG.setSelected(true);
            else radioButtonFuncG.setSelected(true);
        }

        // Возвращает символ для формата функции
        private String getFormatFunctionSymbol() {
            if (radioButtonFuncE.isSelected()) return "e";
            else if (radioButtonFuncF.isSelected()) return "f";
            else if (radioButtonFuncG.isSelected()) return "g";
            else return "g";
        }

        // Устанавливает символ числового формата для координат точек
        private void setFormatPointSymbol(String symbol) {
            if (Objects.equals(symbol, "E") || Objects.equals(symbol, "e")) radioButtonPointsE.setSelected(true);
            else if (Objects.equals(symbol, "F") || Objects.equals(symbol, "f")) radioButtonPointsF.setSelected(true);
            else if (Objects.equals(symbol, "G") || Objects.equals(symbol, "g")) radioButtonPointsG.setSelected(true);
            else radioButtonPointsG.setSelected(true);
        }

        // Возвращает символ для формата точки
        private static String getFormatPointSymbol() {
            if (radioButtonPointsE.isSelected()) return "e";
            else if (radioButtonPointsF.isSelected()) return "f";
            else if (radioButtonPointsG.isSelected()) return "g";
            else return "g";
        }

        // Возвращает числовой формат для значения целевой функции
        private String getFormatFunctionString() {
            return "%." + (spinnerPrecisionFunc.getValue()) + getFormatFunctionSymbol();
        }

        // Возвращает числовой формат для координат точек
        private static String getFormatPointString() {
            return "%." + ((int)spinnerPrecisionPoints.getValue()+1) + getFormatPointSymbol();
        }

        // Метод для форматирования фукнции
        private void setFormattedFunc() {
            if (minimumOfFunction != null)
                textFieldMinFunc.setText(String.format(getFormatFunctionString(), minimumOfFunction));
        }

        // Метод для форматирования точек
        static void setFormattedPoints() {
            for (int i = 0; i < pointsTableModel.getRowCount(); i++) {
                for (int j = 1; j < 3; j++) {
                    if (formattedPoints[i][j] != null) {
                        if (!Objects.equals(formattedPoints[i][j].toString(), ""))
                            pointsTableModel.formatValue(String.format(getFormatPointString(), formattedPoints[i][j]), i, j);
                    }
                }
            }
            pointsTableRepaint();
        }

        // Настроить начальное состояние для Информации о выполнении поиска
        private void setProgressInformationInitial() {
            timeOfCalculations = ""; // Время выполнения поиска
            textFieldProgressInfo.setText("Готово"); // TextBox для информации о выполнении поиска
            checkBoxProgressInfoSteps.setSelected(showProgressInfoInitial); // Показывать информацию о выполнении поиска
            spinnerProgressInfo.setValue(stepsIntervalForProgressInfoInitial);  // Интервал для показа шагов поиска
            spinnerProgressInfo.setEnabled(checkBoxProgressInfoSteps.isSelected());
            buttonCalculate.requestFocus();
        }

        private void setProgressInformationStart() {
            textFieldProgressInfo.setText("Готово");
            buttonCalculate.requestFocus();
        }

        // Настроить рабочее состояние для Информации о выполнении поиска
        private void setProgressInformationWorking() {
            timeOfCalculations = ""; // Время выполнения поиска
            textFieldProgressInfo.setText("Выполняется поиск"); // TextBox для информации о выполнении поиска
            textFieldProgressInfo.setCaretPosition(0);
        }

        // Конвертирует из xml в данные
        private static DataModel unmarshal(File file) throws Exception {
            JAXBContext context = JAXBContext.newInstance(DataModel.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            DataModel model = (DataModel) unmarshaller.unmarshal(file);
            return model;
        }

        // Конвертирует данные в формат xml
        private static void marshal(DataModel model, File file) throws Exception {
            JAXBContext context = JAXBContext.newInstance(DataModel.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(model, file);
        }

        // Метод для закрытия окна
        private void windowClose() {
            if (!isSaved) {
                int n = JOptionPane.showOptionDialog(frame, "Вы уверены, что хотите закрыть приложение?",
                        "Результаты вычислений не сохранены!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, 1);
                if (n == 0) {
                    frame.setVisible(false);
                    System.exit(0);
                } else return;
            }
            frame.setVisible(false);
            System.exit(0);
        }

        // Формирует заголовок программы
        private static void setTitle() {

            String title = programTitle;
            String fileN = null;
            if (fileName != null) fileN = fileName.getName();
            if ((fileN != null && !Objects.equals(fileN, "")) || !isSaved) title += " - ";
            if (fileN != null && !Objects.equals(fileN, "")) title += fileN;
            if (!isSaved) title += "*";
            frame.setTitle(title);
        }

        // Изменить состояние сохранение
        static void setSaved(boolean saved) {
            isSaved = saved;
            setTitle();
        }

        // Включить или отключить кнопки вычисления
        private void setButtonsCalculateEnabled(boolean enabled) {
            buttonCalculate.setEnabled(enabled);
            searchMenu.setEnabled(enabled);
            searchButton.setEnabled(enabled);
            contextSearch.setEnabled(enabled);
        }

        // Задать Алгоритм поиска
        private void setAlgorithmNumber(int algorithmNumber) {
            switch (algorithmNumber)
            {
                case 1:
                    algorithmMono.setIcon(checkIcon);
                    algorithmMonoNormal.setIcon(null);
                    algorithmInhNormal.setIcon(null);
                    algorithmInhomogeneous.setIcon(null);
                    algorithmIngber.setIcon(null);
                    GUI.algorithmNumber = algorithmNumber;
                    spinnerMStageSteps.setEnabled(false);
                    break;
                case 2:
                    algorithmMono.setIcon(null);
                    algorithmMonoNormal.setIcon(checkIcon);
                    algorithmInhNormal.setIcon(null);
                    algorithmInhomogeneous.setIcon(null);
                    algorithmIngber.setIcon(null);
                    GUI.algorithmNumber = algorithmNumber;
                    spinnerMStageSteps.setEnabled(false);
                    break;
                case 3:
                    algorithmMono.setIcon(null);
                    algorithmMonoNormal.setIcon(null);
                    algorithmInhNormal.setIcon(checkIcon);
                    algorithmInhomogeneous.setIcon(null);
                    algorithmIngber.setIcon(null);
                    GUI.algorithmNumber = algorithmNumber;
                    spinnerMStageSteps.setEnabled(true);
                    break;
                case 4:
                    algorithmMono.setIcon(null);
                    algorithmMonoNormal.setIcon(null);
                    algorithmInhNormal.setIcon(null);
                    algorithmInhomogeneous.setIcon(checkIcon);
                    algorithmIngber.setIcon(null);
                    GUI.algorithmNumber = algorithmNumber;
                    spinnerMStageSteps.setEnabled(true);
                    break;
                case 5:
                    algorithmMono.setIcon(null);
                    algorithmMonoNormal.setIcon(null);
                    algorithmInhNormal.setIcon(null);
                    algorithmInhomogeneous.setIcon(null);
                    algorithmIngber.setIcon(checkIcon);
                    GUI.algorithmNumber = algorithmNumber;
                    spinnerMStageSteps.setEnabled(false);
                    break;
            }
        }

        // Всплывающие подсказки
        private void setToolTip() {
            ToolTipManager.sharedInstance().setInitialDelay(1000);
            ToolTipManager.sharedInstance().setReshowDelay(1000);
            labelMinFunc.setToolTipText("Значение целевой функции в конечной точке поиска");
            textFieldMinFunc.setToolTipText("Значение целевой функции в конечной точке поиска");
            buttonCalculate.setToolTipText("Выполнить случайный поиск");
            panelParameters.setToolTipText("От выбора параметров поиска зависят успех поиска и его трудоемкость");
            labelNumberSteps.setToolTipText("Число шагов поиска желательно взять достаточно большим");
            spinnerNumberSteps.setToolTipText("Число шагов поиска желательно взять достаточно большим");
            labelMStageSteps.setToolTipText("Число шагов поиска на этапе");
            spinnerMStageSteps.setToolTipText("Число шагов поиска на этапе");
            labelV.setToolTipText("Значение v > 0 можно взять близким к требуемой точности поиска");
            textFieldV.setToolTipText("Значение v > 0 можно взять близким к требуемой точности поиска");
            labelGamma.setToolTipText("Значение Г > v можно взять близким к точности начального приближения");
            textFieldGamma.setToolTipText("Значение Г > v можно взять близким к точности начального приближения");
            labelDimension.setToolTipText("Размерность пространства оптимизации указывается при задании функции");
            textFieldDimension.setToolTipText("Размерность пространства оптимизации указывается при задании функции");
            buttonCheckParameters.setToolTipText("Проверить правильность задания параметров v и Г и выполнения условия 0 < v < Г");

            textFieldUseFormula.setToolTipText("<html><p width=\"350\">" + "Использовать формулу при вычислении целевой функции " + "или использовать код функции из файла ObjectiveFunction.cs" + "</p></html>");
            buttonSetFunc.setToolTipText("Задать формулу для вычисления целевой функции");

            panelFormatFunc.setToolTipText("Форматирование значения целевой функции");
            radioButtonFuncE.setToolTipText("Экспоненциальное представление чисел");
            radioButtonFuncF.setToolTipText("Представление чисел с фиксированной запятой (точкой)");
            radioButtonFuncG.setToolTipText("Используется более короткий из двух форматов: Е или F");
            labelPrecisionFunc.setToolTipText("Количество цифр в дробной части для Е и F, и количество значащих цифр для G");
            spinnerPrecisionFunc.setToolTipText("Количество цифр в дробной части для Е и F, и количество значащих цифр для G");

            panelFormatPoints.setToolTipText("Форматирование координат точек");
            radioButtonPointsE.setToolTipText("Экспоненциальное представление чисел");
            radioButtonPointsF.setToolTipText("Представление чисел с фиксированной запятой (точкой)");
            radioButtonPointsG.setToolTipText("Используется более короткий из двух форматов: Е или F");
            labelPrecisionPoints.setToolTipText("Количество цифр в дробной части для Е и F, и количество значащих цифр для G");
            spinnerPrecisionPoints.setToolTipText("Количество цифр в дробной части для Е и F, и количество значащих цифр для G");

            panelRandomGenerator.setToolTipText("Инициализировать генератор псевдослучайных чисел или зависящим от времени значением или заданным числом");
            checkBoxRandomGenerator.setToolTipText("Инициализировать генератор псевдослучайных чисел заданным числом");
            spinnerRandomGenerator.setToolTipText("Инициализировать генератор псевдослучайных чисел заданным числом");

            panelProgressInfo.setToolTipText("Сведения о ходе выполнения поиска");
            checkBoxProgressInfoSteps.setToolTipText("Нужно ли выводить номера шагов поиска");
            spinnerProgressInfo.setToolTipText("Выводить номера шагов поиска с указанным интервалом");
            textFieldProgressInfo.setToolTipText("Сведения о выполнении поиска");
        }

        // Убрать всплывающие подсказки
        private void unsetToolTip() {
            labelMinFunc.setToolTipText(null);
            textFieldMinFunc.setToolTipText(null);
            buttonCalculate.setToolTipText(null);
            panelParameters.setToolTipText(null);
            labelNumberSteps.setToolTipText(null);
            spinnerNumberSteps.setToolTipText(null);
            labelMStageSteps.setToolTipText(null);
            spinnerMStageSteps.setToolTipText(null);
            labelV.setToolTipText(null);
            textFieldV.setToolTipText(null);
            labelGamma.setToolTipText(null);
            textFieldGamma.setToolTipText(null);
            labelDimension.setToolTipText(null);
            textFieldDimension.setToolTipText(null);
            buttonCheckParameters.setToolTipText(null);
            textFieldUseFormula.setToolTipText(null);
            buttonSetFunc.setToolTipText(null);
            panelFormatFunc.setToolTipText(null);
            radioButtonFuncE.setToolTipText(null);
            radioButtonFuncF.setToolTipText(null);
            radioButtonFuncG.setToolTipText(null);
            labelPrecisionFunc.setToolTipText(null);
            spinnerPrecisionFunc.setToolTipText(null);
            panelFormatPoints.setToolTipText(null);
            radioButtonPointsE.setToolTipText(null);
            radioButtonPointsF.setToolTipText(null);
            radioButtonPointsG.setToolTipText(null);
            labelPrecisionPoints.setToolTipText(null);
            spinnerPrecisionPoints.setToolTipText(null);
            panelRandomGenerator.setToolTipText(null);
            checkBoxRandomGenerator.setToolTipText(null);
            spinnerRandomGenerator.setToolTipText(null);
            panelProgressInfo.setToolTipText(null);
            checkBoxProgressInfoSteps.setToolTipText(null);
            spinnerProgressInfo.setToolTipText(null);
            textFieldProgressInfo.setToolTipText(null);
        }

        // Перерисовка ячеек таблицы точек
        static void pointsTableRepaint() {
            pointsTable.getTableHeader().setReorderingAllowed(false);
            pointsTable.setFont(new Font("Colibri", Font.PLAIN, 11));

            pointsTable.getTableHeader().setPreferredSize(new Dimension(50, 50));

            //Ширина колонны индексов
            TableColumn column = pointsTable.getColumnModel().getColumn(0);
            column.setMinWidth(50);
            column.setMaxWidth(50);
            column.setPreferredWidth(50);

            //Расположение в ячейке
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
            cellRenderer.setHorizontalAlignment(JLabel.RIGHT);
            for (int i = 0; i < 3; i++) {
                pointsTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
            for (int r = 0; r < dimension; r++) {
                pointsTableModel.setValueAt("  " + r + "   ", r, 0);
                if (r > 9)
                    pointsTableModel.setValueAt("  " + r + "  ", r, 0);
                if (r > 99)
                    pointsTableModel.setValueAt("  " + r + "  ", r, 0);
                pointsTable.setRowHeight(r, 25);
            }
            pointsTable.repaint();
        }

        //Замена последнего вхождения символа/строки в заданной строке
        private static String replaceLast(String string, String toReplace, String replacement) {
            int pos = string.lastIndexOf(toReplace);
            if (pos > -1) {
                return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length());
            } else {
                return string;
            }
        }

        // Проверка строки на пустоту
        private static boolean isWhitespace(String s) {
            int length = s.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    if (!Character.isWhitespace(s.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        // Выполнять поиск в другом потоке
        private void calculateBackground() {
            (new CalculateWorker()).execute();
        }
    //endregion
}