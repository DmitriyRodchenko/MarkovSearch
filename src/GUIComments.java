import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class GUIComments
{
    //region Переменные
    JDialog dialog;
    JTextArea textArea;
    String tempText;
    //endregion

    //region Конструктор
    GUIComments(){
        Font font = new Font("Microsoft Sans Serif",Font.PLAIN,11);

        dialog = new JDialog();
        dialog.setTitle("Комментарии к задаче");

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        dialog.setSize(470, 300);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.addWindowListener(new WindowListener());
        dialog.setIconImage(new ImageIcon(getClass().getResource("/icons/New_Text_Document_32_n_i8.png")).getImage());

        JButton buttonOK = new JButton("OK");
        buttonOK.setFont(font);
        buttonOK.setIcon(new ImageIcon(getClass().getResource("/icons/Green_Checkmark_16_n_p.png")));
        buttonOK.addActionListener(new ButtonOKListener());

        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.setIcon(new ImageIcon(getClass().getResource("/icons/Red_Delete_16_n_p.png")));
        buttonCancel.addActionListener(new ButtonCancelListener());
        buttonCancel.setFont(font);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        Insets insets = new Insets(10,10,2,10);

        dialog.setLayout(layout);

        c.weightx = 1;
        c.weighty = 1;
        c.insets = insets;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        dialog.add(scrollPane,c);

        JPanel panel = new JPanel();
        insets = new Insets(2,10,10,2);
        c.insets = insets;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(buttonOK,c);

        c.gridx = 1;
        insets = new Insets(2,2,10,10);
        c.insets = insets;
        panel.add(buttonCancel,c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        dialog.add(panel,c);

        dialog.setResizable(false);
        dialog.getRootPane().setDefaultButton(buttonOK);
        //dialog.setVisible(true);
        buttonOK.requestFocus();
    }
    //endregion

    //region Слушатели
    class ButtonOKListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            if (!tempText.equals(textArea.getText()))
            {
                tempText = textArea.getText();
                GUI.paramsTableModel.setValueAt(tempText, 0, 15);
                GUI.setSaved(false);
            }
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }

    class ButtonCancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            textArea.setText(tempText);
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }

    class WindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e)
        {
            textArea.setText(tempText);
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }
    //endregion

    //region Методы

    //Установить временное значение текста
    void setTempText(String s) { tempText = s; }

    //Вернуть текст
    String getText()
    {
        return textArea.getText();
    }
    //endregion
}