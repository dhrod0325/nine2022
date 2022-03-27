package ks.model.item.function.option;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1Option {
    @XmlAttribute(name = "value")
    private int value;

    private int _chance;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getChance() {
        return _chance;
    }

    @XmlAttribute(name = "Chance")
    public void setChance(double chance) {
        this._chance = (int) (chance * 10000);
    }

    public int calcChance() {
        return (int) (getChance() / 10000);
    }
}
