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
}
