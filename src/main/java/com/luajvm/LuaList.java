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

    @Override
    public LuaValue getFirst(){
        return get(0);
    }

    @Override
    public LuaValue getLast(){
        return get(Math.max(0, size() - 1));
    }

    public LuaList subList(int fromIndex) {
        int realFromIndex = Math.min(fromIndex, size());
        LuaList subList = new LuaList();
        for (int i = realFromIndex; i < size(); i++) {
            subList.add(get(i));
        }
        return subList;
    }
}
