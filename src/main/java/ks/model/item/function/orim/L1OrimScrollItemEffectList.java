package ks.model.item.function.orim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemEffectList")
public class L1OrimScrollItemEffectList implements Iterable<L1OrimScrollEnchant> {
    @XmlElement(name = "Item")
    private List<L1OrimScrollEnchant> _list;

    public Iterator<L1OrimScrollEnchant> iterator() {
        return this._list.iterator();
    }
}
