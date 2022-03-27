package ks.model.item.function.orim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1OrimScrollEffect {
    @XmlAttribute(name = "Enchant")
    private int _enchantLevel;

    @XmlAttribute(name = "Prob")
    private String _probs;

    public int getEnchantLevel() {
        return this._enchantLevel;
    }

    public String getProbs() {
        return this._probs;
    }
}
