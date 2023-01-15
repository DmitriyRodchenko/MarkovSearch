import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUIFormula
{
    //region Переменные
    JDialog dialog;
    private JCheckBox useFormula;
    private JSpinner dimensionSpinner;
    private JLabel label;
    private JLabel labelFunc;
    private JLabel labelErr;
    private JButton buttonCheck;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton buttonHelp;
    private JTextArea textAreaFunc;
    private JTextArea textAreaError;
    static GUIHelp helpDialog = new GUIHelp();

    private boolean tempUseFormula;
    private String tempTextFormula;
    private String tempTextError;
    private int tempDimension;
    //endregion

    //region Конструктор
    GUIFormula() {

        Font font = new Font("Microsoft Sans Serif", Font.PLAIN, 11);

        //region Диалог(Окно задания функции)
        dialog = new JDialog();
        dialog.setTitle("Целевая функция");

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        dialog.setLayout(layout);
        dialog.setSize(700, 400);

        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.addWindowListener(new WindowClosingListener());
        dialog.setIconImage(new ImageIcon(getClass().getResource("/icons/Edit_16_n_p.png")).getImage());
        //endregion

        //region Элементы
        label = new JLabel("Размерность пространства оптимизации");
        label.setFont(font);
        labelFunc = new JLabel("Целевая функция:");
        labelFunc.setFont(font);
        labelErr = new JLabel("Ошибки в формуле:");
        labelErr.setFont(font);

        buttonCheck = new JButton("Проверить формулу");
        buttonCheck.setIcon(new ImageIcon(getClass().getResource("/icons/Options_1_24_n_p.png")));
        buttonCheck.addActionListener(new CheckListener());
        buttonCheck.setFont(font);

        buttonHelp = new JButton("Справка");
        buttonHelp.setIcon(new ImageIcon(getClass().getResource("/icons/Help_Green_16_n_p.png")));
        buttonHelp.addActionListener(new HelpListener());
        buttonHelp.setFont(font);

        buttonOK = new JButton("OK");
        buttonOK.setIcon(new ImageIcon(getClass().getResource("/icons/Green_Checkmark_16_n_p.png")));
        buttonOK.addActionListener(new ButtonOKListener());
        buttonOK.setFont(font);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setIcon(new ImageIcon(getClass().getResource("/icons/Red_Delete_16_n_p.png")));
        buttonCancel.addActionListener(new ButtonCancelListener());
        buttonCancel.setFont(font);

        useFormula = new JCheckBox("Использовать формулу");
        useFormula.setFont(font);
        useFormula.addActionListener(new UseFormulaListener());
        SpinnerModel spinnerModel = new SpinnerNumberModel(ObjectiveFunction.dimension, 1, 200, 1);
        dimensionSpinner = new JSpinner(spinnerModel);

        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) dimensionSpinner.getEditor();
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        spinnerEditor.getTextField().setColumns(6);

        textAreaFunc = new JTextArea();
        textAreaFunc.setLineWrap(true);
        textAreaFunc.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textAreaFunc);

        textAreaError = new JTextArea();
        textAreaError.setLineWrap(true);
        textAreaError.setWrapStyleWord(true);
        textAreaError.setEditable(false);
        JScrollPane scrollPaneError = new JScrollPane(textAreaError);
        //endregion

        //region Панели и расстановка
        JPanel panelFunc = new JPanel();
        JPanel panelErr = new JPanel();

        panelFunc.setLayout(layout);
        panelErr.setLayout(layout);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelFunc, panelErr);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.7);

        Insets insets = new Insets(5, 10, 2, 10);

        //region Первая строка (Чекбокс, Размерность, Проверка)

            //region Чекбокс Использовать формулу
        c.gridx = 0;
        c.gridy = 0;
        c.insets = insets;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridheight = 1;
        panelFunc.add(useFormula, c);
        //endregion

            //region Размерность
        insets = new Insets(10, 170, 1, 2);
        c.insets = insets;
        panelFunc.add(label, c);

        insets = new Insets(10, 390, 1, 2);
        c.insets = insets;
        panelFunc.add(dimensionSpinner, c);
        //endregion

            //region Проверка формулы
        insets = new Insets(10, 8, 1, 10);
        c.insets = insets;
        c.anchor = GridBagConstraints.NORTHEAST;
        panelFunc.add(buttonCheck, c);
        //endregion

        //endregion

        //region Панель функции
        insets = new Insets(30, 15, 3, 1);
        c.insets = insets;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        panelFunc.add(labelFunc, c);

        insets = new Insets(2, 10, 5, 10);
        c.insets = insets;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        panelFunc.add(scrollPane, c);
        //endregion

        //region Панель ошибок
        insets = new Insets(2, 15, 5, 2);
        c.insets = insets;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        panelErr.add(labelErr, c);

        insets = new Insets(2, 10, 5, 10);
        c.insets = insets;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        panelErr.add(scrollPaneError, c);
        //endregion

        //region Кнопка справки
        insets = new Insets(2, 10, 2, 10);
        c.insets = insets;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.SOUTHWEST;
        panelErr.add(buttonHelp, c);
        //endregion

        //region Кнопки Ок/Отмена
        insets = new Insets(2, 10, 2, 105);
        c.insets = insets;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        panelErr.add(buttonOK, c);

        insets = new Insets(2, 10, 2, 10);
        c.insets = insets;
        panelErr.add(buttonCancel, c);
        //endregion

        //region Разделитель панелей
        insets = new Insets(10, 10, 10, 10);
        c.insets = insets;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        dialog.add(splitPane, c);
        //endregion

        //region Пример для проверки
        // textAreaFunc.setText("((x[0] * x[0] + 1) * x[0] +  x[1]) * x[0] + x[1] * x[1]");
        //endregion
        //endregion

        //Кнопка по умолчанию ОК
        dialog.getRootPane().setDefaultButton(buttonOK);

        //Начальное состояние
        setInitialParams();

        textAreaFunc.requestFocus();
    }
    //endregion

    //region Слушатели
    class UseFormulaListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (useFormula.isSelected())
                useFormula.setSelected(true);
            else
                useFormula.setSelected(false);
        }
    }

    class CheckListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            checkFormula(); // Проверить формулу
            textAreaFunc.requestFocus();
        }
    }

    class HelpListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            helpDialog.dialog.setLocation(dialog.getLocation().x + 15,dialog.getLocation().y - 2);
            helpDialog.dialog.setTitle("Справка о формуле");
            helpDialog.setHelpText();
            dialog.setEnabled(false);
            helpDialog.textArea.setCaretPosition(0);
            helpDialog.dialog.setVisible(true);
        }
    }

    class ButtonOKListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            if (checkFormula()) // Проверить формулу
            {
                int n = JOptionPane.showOptionDialog(dialog, "Продолжить редактирование формулы?", "Формула написана неправильно",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, GUI.options, 1);
                if (n == 0) {
                    return;
                } else {
                    setParams();
                    GUI.frame.setEnabled(true);
                    dialog.setVisible(false);
                    return;
                }
            }
            setParams();
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }

    class ButtonCancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            setOldParams();
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }

    class WindowClosingListener extends WindowAdapter {
        public void windowClosing(WindowEvent e)
        {
            setOldParams();
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }
    //endregion

    //region Методы

    //Фокус на текстовом поле ввода формулы
    void requestFocus()
    {
        textAreaFunc.requestFocus();
    }

    // Размерность пространства оптимизации
    public void setDimension(int value)   {
        dimensionSpinner.setValue(value);
    }

    public int getDimension() {
        return (int) dimensionSpinner.getValue();
    }

    //Задать состояние использования формулы
    void setUseFormula(boolean b) {
        useFormula.setSelected(b);
    }

    //Вернуть состояние использования формулы
    boolean getUseFormula() {
        return useFormula.isSelected();
    }

    //Записать строку с функцией
    void setFormulaString(String s) { textAreaFunc.setText(s); }

    //Вернуть строку с функцией
    String getFormulaString() {
        return textAreaFunc.getText();
    }

    // Проверить формулу. Вернуть true если есть ошибки
    boolean checkFormula() {
        textAreaError.setText("");

        if (!getUseFormula() && (getFormulaString() == null || getFormulaString().equals(""))) return false;

        // Создать объект для выполнения вычислений
        CalculatorExpression calculator = new CalculatorExpression(getDimension(), getFormulaString());

        calculator.handleFormula();
        if (calculator.hasErrors) {
            textAreaError.setText(calculator.errorMessage);
            return true;
        }

        calculator.makeReversePolishNotation();
        if (calculator.hasErrors) {
            textAreaError.setText(calculator.errorMessage);
            return true;
        }

        return false;
    }

    //Получить значения из главного окна
    public void setInitialParams() {
        useFormula.setSelected(GUI.useFormula);
        setDimension(GUI.dimension);
        tempDimension = (int)dimensionSpinner.getValue();
        textAreaFunc.setText(GUI.formulaString);
        if (useFormula.isSelected())
            checkFormula();
        setToolTip();
    }

    //Принять значения
    void setParams() {
        if (getUseFormula())
        {
            tempDimension = (int) dimensionSpinner.getValue();
            GUI.textFieldUseFormula.setText("Используем формулу");
            GUI.useFormula = true;
            GUI.formulaString = getFormulaString();
            GUI.dimension = tempDimension;
            ObjectiveFunction.dimension = tempDimension;
        }
        else
        {
            tempDimension = 2;
            dimensionSpinner.setValue(2);
            ObjectiveFunction.dimension = 2;
            GUI.dimension = 2;
            GUI.useFormula = false;
            GUI.textFieldUseFormula.setText("Используем код функции");
        }
        tempUseFormula = useFormula.isSelected();
        tempTextFormula = textAreaFunc.getText();
        tempTextError = textAreaError.getText();
        GUI.textFieldDimension.setText(""+tempDimension);
        PointsModel tempTableModelPoints = new PointsModel();
        int size;
        if (GUI.pointsTableModel.points.size() > tempTableModelPoints.points.size())
            size = tempTableModelPoints.points.size();
        else
            size = GUI.pointsTableModel.points.size();
        for (int i = 0; i < size; i++)
            tempTableModelPoints.points.set(i, GUI.pointsTableModel.points.get(i));
        GUI.pointsTableModel = new PointsModel();
        GUI.pointsTableModel.points = tempTableModelPoints.points;
        GUI.pointsTable.setModel(GUI.pointsTableModel);
        GUI.pointsTableRepaint();
    }

    //Вернуть значения
    private void setOldParams() {
        dimensionSpinner.setValue(tempDimension);
        useFormula.setSelected(tempUseFormula);
        textAreaFunc.setText(tempTextFormula);
        textAreaError.setText(tempTextError);
    }

    // Всплывающие подсказки
    void setToolTip() {
        useFormula.setToolTipText("Отметьте этот пункт, если хотите использовать формулу при вычислении целевой функции " + System.lineSeparator() + "(иначе будет использован код функции из файла ObjectiveFunction.java)");
        label.setToolTipText("Укажите размерность пространства оптимизации");
        dimensionSpinner.setToolTipText("Укажите размерность пространства оптимизации");
        buttonCheck.setToolTipText("Проверьте правильность написания формулы");
        labelFunc.setToolTipText("Введите формулу для вычисления целевой функции");
        textAreaFunc.setToolTipText("Формула для вычисления целевой функции");
        labelErr.setToolTipText("Сообщения об ошибках в формуле");
        textAreaError.setToolTipText("Сообщения об ошибках в формуле");
        buttonHelp.setToolTipText("Правила написания формулы");
        buttonOK.setToolTipText("Сохранить указанные значения");
        buttonCancel.setToolTipText("Отменить");
    }

    // Убрать всплывающие подсказки
    void unsetToolTip() {
        useFormula.setToolTipText(null);
        label.setToolTipText(null);
        dimensionSpinner.setToolTipText(null);
        buttonCheck.setToolTipText(null);
        labelFunc.setToolTipText(null);
        textAreaFunc.setToolTipText(null);
        labelErr.setToolTipText(null);
        textAreaError.setToolTipText(null);
        buttonHelp.setToolTipText(null);
        buttonOK.setToolTipText(null);
        buttonCancel.setToolTipText(null);
    }
    //endregion
}
