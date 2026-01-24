package com.luajvm;

public class LuaRuntimeException extends RuntimeException {

    public LuaRuntimeException(LuaValue value){
        super(value == null ? "nil" : value.toString());
        this.value_ = (value == null) ? LuaValue.NIL_VALUE : value;

        this.arg1_ = null;
        this.arg2_ = null;
        this.action_ = null;
    }

    public LuaRuntimeException(String action, LuaValue arg1, LuaValue arg2) {
        super("attempt to " + action + " a " + arg1.getTypeString() + " with a " + arg2.getTypeString());
        arg1_ = arg1;
        arg2_ = arg2;
        action_ = action;

    }

    public LuaRuntimeException(String action, LuaValue arg) {
        super("attempt to " + action + " a " + arg.getTypeString() + " value");
        arg1_ = arg;
        arg2_ = null;
        action_ = action;
    }

    public LuaValue getArg1() {
        return arg1_;
    }

    public LuaValue getArg2() {
        return arg2_;
    }

    public String getAction_() {
        return action_;
    }

    private LuaValue value_;
    private LuaValue arg1_;
    private LuaValue arg2_;
    private String action_;

    public LuaValue getValue() {
        return value_;
    }
}
