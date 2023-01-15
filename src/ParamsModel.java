import javax.swing.table.AbstractTableModel;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ParamsList")
public class ParamsModel extends AbstractTableModel
{
    //region Элементы
        static String[] columnNames = {"Algorithm","dimension", "NumberOfSteps", "NumberOfStageSteps", "nu", "Gamma", "MinimumOfFunction", "FormatFunctionSymbol", "FormatFunctionPrecision",
                "FormatPointSymbol", "FormatPointPrecision", "GeneratorInitialize", "GeneratorSeed", "ShowProgressInformation", "StepsIntervalForProgressInformation",
                "TimeOfCalculations", "Comments", "UseFormula", "FormulaString"};

        @XmlElement(name = "Params")
        protected List<Params> params;
    //endregion

    //region Конструктор
        ParamsModel() {
            params = new ArrayList<>();
            params.add(0,new Params());
        }
    //endregion

    //region Сеттеры и Геттеры
        @Override
        public int getRowCount() {
            return params.size();
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
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Params Params = params.get(rowIndex);
            Object value = null;
            switch (columnIndex) {
                case 0 : value = Params.getAlgorithm(); break;
                case 1 : value = Params.getDimension(); break;
                case 2 : value = Params.getNumberOfSteps(); break;
                case 3 : value = Params.getNumberOfStageSteps(); break;
                case 4 : value = Params.getNu(); break;
                case 5 : value = Params.getGamma(); break;
                case 6 : value = Params.getMinimumOfFunction(); break;
                case 7 : value = Params.getFormatFunctionSymbol(); break;
                case 8 : value = Params.getFormatFunctionPrecision(); break;
                case 9 : value = Params.getFormatPointSymbol(); break;
                case 10 : value = Params.getFormatPointPrecision(); break;
                case 11 : value = Params.getGeneratorInitialize(); break;
                case 12 : value = Params.getGeneratorSeed(); break;
                case 13 : value = Params.getShowProgressInformation(); break;
                case 14 : value = Params.getStepsIntervalForProgressInformation(); break;
                case 15 : value = Params.getTimeOfCalculations(); break;
                case 16 : value = Params.getComments(); break;
                case 17 : value = Params.getUseFormula(); break;
                case 18 : value = Params.getFormulaString(); break;
            }
            return value;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Params Params = params.get(rowIndex);
            Double value;
            switch (columnIndex) {
                case 0 : Params.setAlgorithm((Integer)(aValue));break;
                case 1 : Params.setDimension((Integer)(aValue));break;
                case 2 : Params.setNumberOfSteps((Long)(aValue));break;
                case 3 : Params.setNumberOfStageSteps((Long)(aValue));break;
                case 4 : value = null; Params.setNu(value);
                    try { value = Double.parseDouble(aValue.toString());
                        Params.setNu(value);}catch (Exception ignored){}
                    break;
                case 5 : value = null; Params.setGamma(value);
                    try { value = Double.parseDouble(aValue.toString());
                        Params.setGamma(value);}catch (Exception ignored){}
                    break;
                case 6 : value = null; Params.setMinimumOfFunction(value);
                    try { value = Double.parseDouble(aValue.toString());
                        Params.setMinimumOfFunction(value);}catch (Exception ignored){}
                    break;
                case 7 : Params.setFormatFunctionSymbol(aValue.toString()); break;
                case 8 : Params.setFormatFunctionPrecision((Integer)(aValue)); break;
                case 9 : Params.setFormatPointSymbol(aValue.toString()); break;
                case 10 : Params.setFormatPointPrecision((Integer)(aValue)); break;
                case 11 : Params.setGeneratorInitialize((boolean)aValue); break;
                case 12 : Params.setGeneratorSeed((Integer)(aValue)); break;
                case 13 : Params.setShowProgressInformation((boolean)aValue); break;
                case 14 : Params.setStepsIntervalForProgressInformation((Long)aValue); break;
                case 15 : Params.setTimeOfCalculations(aValue.toString()); break;
                case 16 : Params.setComments(aValue.toString()); break;
                case 17 : Params.setUseFormula((boolean)aValue); break;
                case 18 : Params.setFormulaString(aValue.toString()); break;
            }
        }
    //endregion
}