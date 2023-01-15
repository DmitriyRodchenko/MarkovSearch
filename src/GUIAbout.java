import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class GUIAbout
{
    JDialog dialog;

    //region Конструктор
    GUIAbout() {
        Font font = new Font("Microsoft Sans Serif",Font.PLAIN,11);
        dialog = new JDialog();

        dialog.setTitle("О программе");
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        dialog.setSize(430, 400);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowClosingListener());

        dialog.setLayout(layout);

        JButton buttonOK = new JButton("OK");
        buttonOK.setIcon(new ImageIcon(getClass().getResource("/icons/Green_Checkmark_16_n_p.png")));
        buttonOK.addActionListener(new ButtonOKListener());
        buttonOK.setFont(font);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setText("Программа \"Марковский случайный поиск\"." + System.lineSeparator() + System.lineSeparator() +
                "Родченко Д.Д. группа 9312." + System.lineSeparator() +
                "НовГУ, кафедра Прикладной математики и информатики." + System.lineSeparator() +
                "Великий Новгород." + System.lineSeparator() + System.lineSeparator() +
                "Программа предназначена для поиска точки глобального минимума целевой функции." + System.lineSeparator() +
                "Для поиска точки минимума используются алгоритмы Марковского случайного поиска экстремума функции.");
        
        Insets insets = new Insets(10,10,5,10);
        c.anchor = GridBagConstraints.WEST;
        c.insets = insets;

        dialog.add(new JLabel(new ImageIcon(getClass().getResource("/icons/Line_Graph_48_n_p.png"))),c);
        insets = new Insets(5,10,2,10);
        c.insets = insets;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        dialog.add(scrollPane,c);

        insets = new Insets(2,10,10,10);
        c.insets = insets;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        dialog.add(buttonOK, c);

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
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }

    class WindowClosingListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            GUI.frame.setEnabled(true);
            dialog.setVisible(false);
        }
    }
    //endregion
}