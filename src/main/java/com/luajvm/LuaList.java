package com.luajvm;

import java.util.ArrayList;

public class LuaList extends ArrayList<LuaValue> {
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
