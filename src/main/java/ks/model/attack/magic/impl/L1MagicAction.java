package ks.model.attack.magic.impl;

import ks.model.attack.magic.impl.action.vo.L1MagicActionVo;

public interface L1MagicAction {
    void commit(L1MagicActionVo vo);
}
