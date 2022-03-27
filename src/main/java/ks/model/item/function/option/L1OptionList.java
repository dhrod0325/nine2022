package ks.model.item.function.option;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OptionList")
public class L1OptionList implements Iterable<L1OptionItem> {
    @XmlElement(name = "OptionItem")
    private List<L1OptionItem> optionItems;

    public Iterator<L1OptionItem> iterator() {
        return optionItems.iterator();
    }
}
