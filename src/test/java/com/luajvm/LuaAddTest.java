package com.luajvm;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LuaAddTest {

    @Test
    public void addIntegers() {
        LuaValue val1 = new LuaValue(1);
        LuaValue val2 = new LuaValue(2);
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.integer, sum.getType());
        assertEquals(3, sum.getIntegerValue());
    }

    @Test
    public void addReals() {
        LuaValue val1 = new LuaValue(1.0);
        LuaValue val2 = new LuaValue(2.0);
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.real, sum.getType());
        assertEquals(3.0, sum.getRealValue());
    }

    @Test
    public void addIntegerToReal() {
        LuaValue val1 = new LuaValue(1);
        LuaValue val2 = new LuaValue(2.0);
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.real, sum.getType());
        assertEquals(3.0, sum.getRealValue());
    }

    @Test
    public void addRealToInteger() {
        LuaValue val1 = new LuaValue(1.0);
        LuaValue val2 = new LuaValue(2);
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.real, sum.getType());
        assertEquals(3.0, sum.getRealValue());
    }

    @Test
    public void addStringsWithIntegerValues() {
        LuaValue val1 = new LuaValue("1");
        LuaValue val2 = new LuaValue("2");
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.integer, sum.getType());
        assertEquals(3, sum.getIntegerValue());
    }

    @Test
    public void addStringsWithRealValues() {
        LuaValue val1 = new LuaValue("1.0");
        LuaValue val2 = new LuaValue("2.0");
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.real, sum.getType());
        assertEquals(3.0, sum.getRealValue());
    }

    @Test
    public void addIntegerStringToRealString() {
        LuaValue val1 = new LuaValue("1");
        LuaValue val2 = new LuaValue("2.1");
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.real, sum.getType());
        assertEquals(3.1, sum.getRealValue());
    }

    @Test
    public void addRealStringToIntegerString() {
        LuaValue val1 = new LuaValue("1.2");
        LuaValue val2 = new LuaValue("2");
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.real, sum.getType());
        assertEquals(3.2, sum.getRealValue());
    }

    @Test
    public void addStringToIntegerString() {
        LuaValue val1 = new LuaValue("a");
        LuaValue val2 = new LuaValue("1");
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> LuaValue.add(val1, val2).getFirst());
        assertEquals(val1, exception.getArg1());
    }


    @Test
    public void addIntegerStringToString() {
        LuaValue val1 = new LuaValue("1");
        LuaValue val2 = new LuaValue("a");
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> LuaValue.add(val1, val2).getFirst());
        assertEquals(val2, exception.getArg1());
    }

    @Test
    public void addIncorrectTypes() {
        List<LuaValue> incorrectValues = List.of(
                new LuaValue(),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of()),
                new LuaValue(Map.of())
        );
        for (LuaValue val1 : incorrectValues) {
            for (LuaValue val2 : incorrectValues) {
                LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> LuaValue.add(val1, val2).getFirst());
                assertEquals(val1, exception.getArg1());
            }
        }
    }

    private LuaValue createTableWithAddMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaMetatable.ADD_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return LuaValue.add(val, arg2);
            } else if (arg2.getType() == LuaValue.Type.table) {
                Map<LuaValue,LuaValue> t = arg2.getTableValue();
                LuaValue val = t.get(new LuaValue("value"));
                return LuaValue.add(val, arg1);
            } else {
                return List.of(new LuaValue());
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    @Test
    public void addIntegerToTableWithMetatableAction() {
        LuaValue val1 = new LuaValue(1);
        LuaValue val2 = createTableWithAddMetatableAction(new LuaValue(2));
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.integer, sum.getType());
        assertEquals(3, sum.getIntegerValue());
    }

    @Test
    public void addTableWithMetatableActionToInteger() {
        LuaValue val1 = createTableWithAddMetatableAction(new LuaValue(2));
        LuaValue val2 = new LuaValue(1);
        LuaValue sum = LuaValue.add(val1, val2).getFirst();
        assertEquals(LuaValue.Type.integer, sum.getType());
        assertEquals(3, sum.getIntegerValue());
    }

}
