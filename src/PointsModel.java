import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "PointsList")
public class PointsModel extends AbstractTableModel
{
    //region Элементы
        //String[] columnNames ={"","initialPoint", "lastPoint"};
        String[] columnNames = {"", "<html><div style='text-align: center;'><p><font size='3' color='black' face='Tahoma'>Начальная точка</font></p></div></html>",
               "<html><div style='text-align: center;'><p><font size='3' color='black' face='Tahoma'>Конечная точка</div></html>"};

        @XmlElement(name = "Points")
        protected List<Points> points;
    //endregion

    //region Конструктор
        PointsModel() {
            points = new ArrayList<>();
            for (int i=0; i<GUI.dimension;i++)
                points.add(i,new Points());
        }
    //endregion

    //region Сеттеры и Геттеры
        @Override
        public int getRowCount() {
            return points.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int col) { return col==1; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Points Points = points.get(rowIndex);
            Object value = null;
            switch (columnIndex) {
                case 0 : value = Points.getNumber(); break;
                case 1 : value = Points.getInitialPoint(); break;
                case 2 : value = Points.getLastPoint(); break;
            }
            return value;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Points Points = points.get(rowIndex);
            switch (columnIndex) {
                case 0 : Points.setNumber(aValue.toString());break;
                case 1 :
                    if (aValue != null && !aValue.equals("") && !aValue.toString().isEmpty())
                    {
                        try
                        {
                            String format = aValue.toString().replace(",",".");
                            Double value = Double.parseDouble(format);
                            Points.setInitialPoint(value);
                            GUI.formattedPoints[rowIndex][columnIndex] = value;
                            GUI.setFormattedPoints();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Неправильный формат данных", "Ошибка ввода", JOptionPane.ERROR_MESSAGE, null);
                        }
                    }
                    break;
                case 2 :
                    try{Points.setLastPoint(Double.parseDouble(aValue.toString()));}
                    catch(Exception ignored){}
                    break;
            }
        }

        public void formatValue(Object aValue, int rowIndex, int columnIndex)
        {
            Points Points = points.get(rowIndex);
            switch (columnIndex) {
                case 0 : Points.setNumber(aValue.toString());break;
                case 1 :
                    if (aValue != null && !aValue.equals("") && !aValue.toString().isEmpty())
                    {
                        try{ String format = aValue.toString().replace(",",".");
                             Double value = Double.parseDouble(format);
                             Points.setInitialPoint(value); }
                        catch (Exception ignored) {}
                    }
                    break;
                case 2 :
                    try{Points.setLastPoint(Double.parseDouble(aValue.toString()));}
                    catch(Exception ignored){}
                    break;
            }
        }
    //endregion
}