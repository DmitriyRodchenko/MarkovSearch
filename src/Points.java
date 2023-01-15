import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Points", propOrder = {
       "number","initialPoint", "lastPoint"
})
class Points
{
    //region Элементы
        @XmlElement(name = "number")
        String number;
        @XmlElement(name = "initialPoint")
        Double initialPoint;
        @XmlElement(name = "lastPoint")
        Double lastPoint;
    //endregion

    //region Сеттеры и Геттеры
        public void setNumber(String n)
        {
            number = n;
        }
        public void setInitialPoint(Double point)
        {
            initialPoint = point.doubleValue();
        }
        public void setLastPoint(Double point)
        {
            lastPoint = point.doubleValue();
        }
        public String getNumber() { return number; }
        public Double getInitialPoint()
        {
            return initialPoint;
        }
        public Double getLastPoint()
        {
            return lastPoint;
        }
    //endregion
}