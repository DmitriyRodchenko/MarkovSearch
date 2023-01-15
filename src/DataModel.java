import javax.swing.table.AbstractTableModel;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

@XmlRootElement(name = "DataList")
public class DataModel  extends AbstractTableModel
{
    //region Элементы
    String[] columnNames = {"", "initialPoint", "lastPoint",
            "Algorithm", "dimension", "NumberOfSteps", "NumberOfStageSteps", "nu", "Gamma", "MinimumOfFunction", "FormatFunctionSymbol", "FormatFunctionPrecision",
            "FormatPointSymbol", "FormatPointPrecision", "GeneratorInitialize", "GeneratorSeed", "ShowProgressInformation", "StepsIntervalForProgressInformation",
            "TimeOfCalculations", "Comments", "UseFormula", "FormulaString"};

    @XmlElement(name = "Points")
    protected List<Points> points;

    @XmlElement(name = "Params")
    protected List<Params> params;
    //endregion

    //region Конструктор
    DataModel() {
        points = new ArrayList<>();
        params = new ArrayList<>();

        for (int i=0; i<GUI.dimension;i++)
            points.add(i,new Points());
        params.add(0,new Params());
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Points Points = points.get(rowIndex);
        Params Params = params.get(rowIndex);
        Object value = null;
        switch (columnIndex) {
            case 0 : value = Points.getNumber(); break;
            case 1 : value = Points.getInitialPoint(); break;
            case 2 : value = Points.getLastPoint(); break;
            case 3 : value = Params.getAlgorithm(); break;
            case 4 : value = Params.getDimension(); break;
            case 5 : value = Params.getNumberOfSteps(); break;
            case 6 : value = Params.getNumberOfStageSteps(); break;
            case 7 : value = Params.getNu(); break;
            case 8 : value = Params.getGamma(); break;
            case 9 : value = Params.getMinimumOfFunction(); break;
            case 10 : value = Params.getFormatFunctionSymbol(); break;
            case 11 : value = Params.getFormatFunctionPrecision(); break;
            case 12 : value = Params.getFormatPointSymbol(); break;
            case 13 : value = Params.getFormatPointPrecision(); break;
            case 14 : value = Params.getGeneratorInitialize(); break;
            case 15 : value = Params.getGeneratorSeed(); break;
            case 16 : value = Params.getShowProgressInformation(); break;
            case 17 : value = Params.getStepsIntervalForProgressInformation(); break;
            case 18 : value = Params.getTimeOfCalculations(); break;
            case 19 : value = Params.getComments(); break;
            case 20 : value = Params.getUseFormula(); break;
            case 21 : value = Params.getFormulaString(); break;
        }
        return value;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Points Points = points.get(rowIndex);
        Params Params = params.get(rowIndex);
        Double value;
        switch (columnIndex) {
            case 0 : Points.setNumber(aValue.toString());break;
            case 1 :
                try{Points.setInitialPoint(Double.parseDouble(aValue.toString()));}
                catch(Exception ignored){}
                break;
            case 2 :
                try{Points.setLastPoint(Double.parseDouble(aValue.toString()));}
                catch(Exception ignored){}
                break;
            case 3 : Params.setAlgorithm((Integer)(aValue));break;
            case 4 : Params.setDimension((Integer)(aValue));break;
            case 5 : Params.setNumberOfSteps((Long)(aValue));break;
            case 6 : Params.setNumberOfStageSteps((Long)(aValue));break;
            case 7 : value = null; Params.setNu(value);
                     try { value = Double.parseDouble(aValue.toString());
                           Params.setNu(value);}catch (Exception ignored){}
                     break;
            case 8 : value = null; Params.setGamma(value);
                    try { value = Double.parseDouble(aValue.toString());
                            Params.setGamma(value);}catch (Exception ignored){}
                    break;
            case 9 : value = null; Params.setMinimumOfFunction(value);
                     try { value = Double.parseDouble(aValue.toString());
                         Params.setMinimumOfFunction(value);}catch (Exception ignored){}
                     break;
            case 10 : Params.setFormatFunctionSymbol(aValue.toString()); break;
            case 11 : Params.setFormatFunctionPrecision((Integer)(aValue)); break;
            case 12 : Params.setFormatPointSymbol(aValue.toString()); break;
            case 13 : Params.setFormatPointPrecision((Integer)(aValue)); break;
            case 14 : Params.setGeneratorInitialize((boolean)aValue); break;
            case 15 : Params.setGeneratorSeed((Integer)(aValue)); break;
            case 16 : Params.setShowProgressInformation((boolean)aValue); break;
            case 17 : Params.setStepsIntervalForProgressInformation((Long)(aValue)); break;
            case 18 : Params.setTimeOfCalculations(aValue.toString()); break;
            case 19 : Params.setComments(aValue.toString()); break;
            case 20 : Params.setUseFormula((boolean)aValue); break;
            case 21 : Params.setFormulaString(aValue.toString()); break;
        }
    }
    //endregion
}