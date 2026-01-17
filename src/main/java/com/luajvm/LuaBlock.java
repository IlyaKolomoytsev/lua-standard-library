package com.luajvm;

public abstract class LuaBlock {
    public LuaBlock(LuaContext context) {
        this.context = new LuaContext(context);
    }

    public abstract void apply();

    protected final LuaContext context;
}
