import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ErrorProvider extends JLabel
{
    //region Переменные
    private transient Icon icon;
    private Timer timer;
    private int   counter;
    //endregion

    //region Конструктор
    ErrorProvider() {
        super("", new ImageIcon(ErrorProvider.class.getResource("/icons/error.png")), SwingConstants.CENTER);

        setVisible(false);
        setPreferredSize(new Dimension(16, 16));

        this.icon = getIcon();
        this.timer = new Timer(
                500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getIcon() == null) {
                    setIcon(icon);
                }
                else {
                    setIcon(null);
                }

                counter++;

                if (counter == 6) {
                    timer.stop();
                }
            }
        });
    }
    //endregion

    //region Методы
    void clearError() {
        setVisible(false);
    }

    void setError(String error) {
        if (!this.timer.isRunning()) {
            setToolTipText(error == null || error.isEmpty() ? "Сообщение об ошибке не предоставлено" : error);
            setVisible(true);

            this.counter = 0;
            this.timer.start();
        }
        else
            setVisible(true);
    }
    //endregion
}