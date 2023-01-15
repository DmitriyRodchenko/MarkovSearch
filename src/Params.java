import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Params", propOrder = {
        "Algorithm", "dimension", "NumberOfSteps", "NumberOfStageSteps", "nu", "Gamma", "MinimumOfFunction", "FormatFunctionSymbol","FormatFunctionPrecision"
        ,"FormatPointSymbol","FormatPointPrecision","GeneratorInitialize","GeneratorSeed","ShowProgressInformation","StepsIntervalForProgressInformation",
        "TimeOfCalculations","Comments","UseFormula","FormulaString"
})
public class Params
{
    //region Элементы
        @XmlElement(name = "Algorithm")
        int Algorithm;
        @XmlElement(name = "dimension")
        int dimension;
        @XmlElement(name = "NumberOfSteps")
        Long NumberOfSteps;
        @XmlElement(name = "NumberOfStageSteps")
        Long NumberOfStageSteps;
        @XmlElement(name = "nu")
        Double nu;
        @XmlElement(name = "Gamma")
        Double Gamma;
        @XmlElement(name = "MinimumOfFunction")
        Double MinimumOfFunction;
        @XmlElement(name = "FormatFunctionSymbol")
        String FormatFunctionSymbol;
        @XmlElement(name = "FormatFunctionPrecision")
        int FormatFunctionPrecision;
        @XmlElement(name = "FormatPointSymbol")
        String FormatPointSymbol;
        @XmlElement(name = "FormatPointPrecision")
        int FormatPointPrecision;
        @XmlElement(name = "GeneratorInitialize")
        boolean GeneratorInitialize;
        @XmlElement(name = "GeneratorSeed")
        int GeneratorSeed;
        @XmlElement(name = "ShowProgressInformation")
        boolean ShowProgressInformation;
        @XmlElement(name = "StepsIntervalForProgressInformation")
        Long StepsIntervalForProgressInformation;
        @XmlElement(name = "TimeOfCalculations")
        String TimeOfCalculations;
        @XmlElement(name = "Comments")
        String Comments;
        @XmlElement(name = "UseFormula")
        boolean UseFormula;
        @XmlElement(name = "FormulaString")
        String FormulaString;
    //endregion

    //region Сеттеры и Геттеры
        public void setAlgorithm(int a){Algorithm=a;}
        public void setDimension(int d){dimension=d;}
        public void setNumberOfSteps(Long n){NumberOfSteps=n;}
        public void setNumberOfStageSteps(Long m){NumberOfStageSteps=m;}
        public void setNu(Double d){nu=d;}
        public void setGamma(Double d){Gamma=d;}
        public void setMinimumOfFunction(Double d){MinimumOfFunction=d;}
        public void setFormatFunctionSymbol(String s){FormatFunctionSymbol=s;}
        public void setFormatFunctionPrecision(int d){FormatFunctionPrecision=d;}
        public void setFormatPointSymbol(String s){FormatPointSymbol=s;}
        public void setFormatPointPrecision(int d){FormatPointPrecision=d;}
        public void setGeneratorInitialize(boolean b){GeneratorInitialize=b;}
        public void setGeneratorSeed(int d){GeneratorSeed=d;}
        public void setShowProgressInformation(boolean b){ShowProgressInformation=b;}
        public void setStepsIntervalForProgressInformation(Long l){StepsIntervalForProgressInformation=l;}
        public void setTimeOfCalculations(String s){TimeOfCalculations=s;}
        public void setComments(String s){Comments=s;}
        public void setUseFormula(boolean b){UseFormula=b;}
        public void setFormulaString(String s){FormulaString=s;}

        public int getAlgorithm(){return Algorithm;}
        public int getDimension(){return dimension;}
        public Long getNumberOfSteps(){return NumberOfSteps;}
        public Long getNumberOfStageSteps(){return NumberOfStageSteps;}
        public Double getNu(){return nu;}
        public Double getGamma(){return Gamma;}
        public Double getMinimumOfFunction(){return MinimumOfFunction;}
        public String getFormatFunctionSymbol(){return FormatFunctionSymbol;}
        public int getFormatFunctionPrecision(){return FormatFunctionPrecision;}
        public String getFormatPointSymbol(){return FormatPointSymbol;}
        public int getFormatPointPrecision(){return FormatPointPrecision;}
        public boolean getGeneratorInitialize(){return GeneratorInitialize;}
        public int getGeneratorSeed(){return GeneratorSeed;}
        public boolean getShowProgressInformation(){return ShowProgressInformation;}
        public Long getStepsIntervalForProgressInformation(){return StepsIntervalForProgressInformation;}
        public String getTimeOfCalculations(){return TimeOfCalculations;}
        public String getComments(){return Comments;}
        public boolean getUseFormula(){return UseFormula;}
        public String getFormulaString(){return FormulaString;}
    //endregion
}