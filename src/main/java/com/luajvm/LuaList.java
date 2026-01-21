package com.luajvm;

import java.util.ArrayList;
import java.util.List;

public class LuaList extends ArrayList<LuaValue> {
    public LuaList(List<LuaValue> other) {
        addAll(other);
    }

    public LuaList() {
    }

    @Override
    public LuaValue get(int index) {
        if (index < size()) {
            return super.get(index);
        } else {
            return new LuaValue();
        }
    }

    public LuaList subList(int fromIndex) {
        int realFromIndex = Math.min(fromIndex, size());
        LuaList subList = new LuaList();
        subList.addAll(realFromIndex, this);
        return subList;
    }
}
