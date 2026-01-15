package com.luajvm;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;

public class LuaArithmeticTest {
    public <T1, T2, E> void arithmeticTest(T1 val1, T2 val2, LuaValue.Type type, E expected, BiFunction<LuaValue, LuaValue, LuaValue> action) {
        LuaValue arg1 = LuaValue.create(val1);
        LuaValue arg2 = LuaValue.create(val2);
        LuaValue sum = action.apply(arg1, arg2);
        assertEquals(type, sum.getType());
        switch (type) {
            case bool -> {
                assertEquals(expected, sum.getBoolValue());
            }
            case integer -> {
                assertEquals(((Number) expected).longValue(), sum.getIntegerValue());
            }
            case real -> {
                assertEquals(expected, round(sum.getRealValue() * 1000000) / 1000000d);
            }
            case string -> {
                assertEquals(expected, sum.getStringValue());
            }
            case function -> {
                assertEquals(expected, sum.getFunctionValue());
            }
            case table -> {
                assertEquals(expected, sum.getTableValue());
            }
        }
    }

    enum Arg {
        none,
        first,
        second,
    }

    static <T1, T2> void arithmeticExceptionTest(T1 val1, T2 val2, Arg exceptionArg1, Arg exceptionArg2, BiFunction<LuaValue, LuaValue, LuaValue> action) {
        LuaValue arg1 = LuaValue.create(val1);
        LuaValue arg2 = LuaValue.create(val2);
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> action.apply(arg1, arg2));
        switch (exceptionArg1) {
            case first -> {
                assertEquals(arg1, exception.getArg1());
            }
            case second -> {
                assertEquals(arg2, exception.getArg1());
            }
        }
        switch (exceptionArg2) {
            case first -> {
                assertEquals(arg1, exception.getArg2());
            }
            case second -> {
                assertEquals(arg2, exception.getArg2());
            }
        }
    }

    static <T1, T2> void incorrectMetamethodTypeTest(T1 val1, T2 val2, LuaValue metamethod, BiFunction<LuaValue, LuaValue, LuaValue> action) {
        LuaValue arg1 = LuaValue.create(val1);
        LuaValue arg2 = LuaValue.create(val2);
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> action.apply(arg1, arg2));
        assertEquals(metamethod, exception.getArg1());
    }

    @ParameterizedTest
    @MethodSource("addArguments")
    public <T1, T2, E> void addTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::add);
    }

    @ParameterizedTest
    @MethodSource("subArguments")
    public <T1, T2, E> void subTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::sub);
    }

    @ParameterizedTest
    @MethodSource("mulArguments")
    public <T1, T2, E> void mulTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::mul);
    }

    @ParameterizedTest
    @MethodSource("divArguments")
    public <T1, T2, E> void divTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::div);
    }

    @ParameterizedTest
    @MethodSource("modArguments")
    public <T1, T2, E> void modTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::mod);
    }

    @ParameterizedTest
    @MethodSource("powArguments")
    public <T1, T2, E> void powTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::pow);
    }

    @ParameterizedTest
    @MethodSource("unmArguments")
    public <T1, T2, E> void unmTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::unm);
    }

    @ParameterizedTest
    @MethodSource("idivArguments")
    public <T1, T2, E> void idivTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::idiv);
    }

    @ParameterizedTest
    @MethodSource("concatArguments")
    public <T1, T2, E> void concatTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::concat);
    }

    @ParameterizedTest
    @MethodSource("lenArguments")
    public <T1, T2, E> void lenTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::len);
    }

    @ParameterizedTest
    @MethodSource("eqArguments")
    public <T1, T2, E> void eqTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::eq);
    }

    @ParameterizedTest
    @MethodSource("ltArguments")
    public <T1, T2, E> void ltTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::lt);
    }

    @ParameterizedTest
    @MethodSource("leArguments")
    public <T1, T2, E> void leTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::le);
    }

    @ParameterizedTest
    @MethodSource("indexArguments")
    public <T1, T2, E> void indexTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::index);
    }

    @ParameterizedTest
    @MethodSource("newIndexArguments")
    public <T, K, V> void newIndexTest(T tableArg, K keyArg, V valueArg) {
        LuaValue table = LuaValue.create(tableArg);
        LuaValue key = LuaValue.create(keyArg);
        LuaValue value = LuaValue.create(valueArg);
        table.newIndex(key, value);

        assertEquals(table.index(key), value);
    }

    @ParameterizedTest
    @MethodSource("callArguments")
    public <T> void callTest(T valueArg, List<LuaValue> args, List<LuaValue> expectedResults) {
        LuaValue value = LuaValue.create(valueArg);
        List<LuaValue> actualResults = value.call(args);
        assertEquals(expectedResults.size(), actualResults.size());
        for (int i = 0; i < expectedResults.size(); i++) {
            assertEquals(expectedResults.get(i), actualResults.get(i));
        }
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void addExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::add);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void subExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::sub);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void mulExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::mul);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void divExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::div);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void modExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::mod);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void powExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::pow);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void unmExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::pow);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void idivExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::idiv);
    }

    @ParameterizedTest
    @MethodSource("concatExceptionArguments")
    public <T1, T2> void concatExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::concat);
    }

    @ParameterizedTest
    @MethodSource("lenExceptionArguments")
    public <T1, T2> void lenExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::len);
    }

    @ParameterizedTest
    @MethodSource("compareExceptionArguments")
    public <T1, T2> void ltExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::lt);
    }

    @ParameterizedTest
    @MethodSource("compareExceptionArguments")
    public <T1, T2> void leExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::le);
    }

    @ParameterizedTest
    @MethodSource("indexExceptionArguments")
    public <T1, T2> void indexExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::index);
    }

    @ParameterizedTest
    @MethodSource("newIndexExceptionArguments")
    public <T, K, V> void newIndexExceptionTest(T tableArg, K keyArg, V valueArg) {
        LuaValue table = LuaValue.create(tableArg);
        LuaValue key = LuaValue.create(keyArg);
        LuaValue value = LuaValue.create(valueArg);
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> table.newIndex(key, value));
        assertEquals(exception.getArg1(), table);
    }

    @ParameterizedTest
    @MethodSource("callExceptionArguments")
    public <T> void callExceptionTest(T function, List<LuaValue> arguments) {
        LuaValue functionVal = LuaValue.create(function);
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> functionVal.call(arguments));
        assertEquals(exception.getArg1(), functionVal);
    }

    @ParameterizedTest
    @MethodSource("addIncorrectMetamethodArguments")
    public <T1, T2> void addIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::add);
    }

    @ParameterizedTest
    @MethodSource("subIncorrectMetamethodArguments")
    public <T1, T2> void subIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::sub);
    }

    @ParameterizedTest
    @MethodSource("mulIncorrectMetamethodArguments")
    public <T1, T2> void mulIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::mul);
    }

    @ParameterizedTest
    @MethodSource("divIncorrectMetamethodArguments")
    public <T1, T2> void divIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::div);
    }

    @ParameterizedTest
    @MethodSource("modIncorrectMetamethodArguments")
    public <T1, T2> void modIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::mod);
    }

    @ParameterizedTest
    @MethodSource("powIncorrectMetamethodArguments")
    public <T1, T2> void powIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::pow);
    }

    @ParameterizedTest
    @MethodSource("unmIncorrectMetamethodArguments")
    public <T1, T2> void unmIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::unm);
    }

    @ParameterizedTest
    @MethodSource("idivIncorrectMetamethodArguments")
    public <T1, T2> void idivIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::idiv);
    }

    @ParameterizedTest
    @MethodSource("lenIncorrectMetamethodArguments")
    public <T1, T2> void lenIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::len);
    }

    @ParameterizedTest
    @MethodSource("eqIncorrectMetamethodArguments")
    public <T1, T2> void eqIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::eq);
    }

    @ParameterizedTest
    @MethodSource("ltIncorrectMetamethodArguments")
    public <T1, T2> void ltIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::lt);
    }

    @ParameterizedTest
    @MethodSource("leIncorrectMetamethodArguments")
    public <T1, T2> void leIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::le);
    }

    @ParameterizedTest
    @MethodSource("indexIncorrectMetamethodArguments")
    public <T1, T2> void indexIncorrectMetamethodExceptionTest(T1 val1, T2 val2, LuaValue metamethod) {
        incorrectMetamethodTypeTest(val1, val2, metamethod, LuaValue::index);
    }

    @ParameterizedTest
    @MethodSource("newIndexIncorrectMetamethodArguments")
    public <T, K, V> void newIndexIncorrectMetamethodExceptionTest(T tableArg, K keyArg, V valueArg, LuaValue metamethod) {
        LuaValue table = LuaValue.create(tableArg);
        LuaValue key = LuaValue.create(keyArg);
        LuaValue value = LuaValue.create(valueArg);
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> table.newIndex(key, value));
        assertEquals(metamethod, exception.getArg1());
    }

    @ParameterizedTest
    @MethodSource("callIncorrectMetamethodArguments")
    public <T> void callIncorrectMetamethodExceptionTest(T value, List<LuaValue> arguments, LuaValue metamethod) {
        LuaValue tableVal = LuaValue.create(value);
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> tableVal.call(arguments));
        assertEquals(metamethod, exception.getArg1());
    }

    private static Stream<Arguments> addArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.integer, 0),
                Arguments.of(0, 1, LuaValue.Type.integer, 1),
                Arguments.of(1, 0, LuaValue.Type.integer, 1),
                Arguments.of(1, 2, LuaValue.Type.integer, 3),
                Arguments.of(Long.MAX_VALUE / 2, Long.MAX_VALUE / 2, LuaValue.Type.integer, Long.MAX_VALUE - 1),
                Arguments.of(0, -1, LuaValue.Type.integer, -1),
                Arguments.of(-1, 0, LuaValue.Type.integer, -1),
                Arguments.of(-1, -4, LuaValue.Type.integer, -5),
                Arguments.of(-10, 10, LuaValue.Type.integer, 0),
                Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, LuaValue.Type.integer, round((Long.MAX_VALUE + Long.MIN_VALUE) * 1000000) / 1000000d),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.real, 0.0d),
                Arguments.of(0d, 1d, LuaValue.Type.real, 1d),
                Arguments.of(1d, 0d, LuaValue.Type.real, 1d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 3d),
                Arguments.of(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2, LuaValue.Type.real, round((Double.MAX_VALUE) * 1000000) / 1000000d),
                Arguments.of(0d, -1d, LuaValue.Type.real, -1d),
                Arguments.of(-1d, 0d, LuaValue.Type.real, -1d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, -5d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, 0d),
                Arguments.of(Double.MAX_VALUE, Double.MIN_VALUE, LuaValue.Type.real, round((Double.MAX_VALUE + Double.MIN_VALUE) * 1000000) / 1000000d),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.real, 3.51d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 3.26d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 3.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 2),
                Arguments.of(9, "2", LuaValue.Type.integer, 11),
                Arguments.of("-1", 1, LuaValue.Type.integer, 0),
                Arguments.of(9, "-2", LuaValue.Type.integer, 7),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 2d),
                Arguments.of(9d, "2", LuaValue.Type.real, 11d),
                Arguments.of("-1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "-2", LuaValue.Type.real, 7d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 2.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 11.1d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -0.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, 6.8d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 2.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 11.1d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -0.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, 6.8d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 11),
                Arguments.of("9", "-2", LuaValue.Type.integer, 7),
                Arguments.of("-9", "2", LuaValue.Type.integer, -7),
                Arguments.of("9.1", "2", LuaValue.Type.real, 11.1),
                Arguments.of("9", "2.1", LuaValue.Type.real, 11.1),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 11.2),
                // Table with metatable operation + Number
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 6),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 6d),
                Arguments.of(1, createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 6),
                Arguments.of(1d, createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.real, 6d),
                Arguments.of(1, createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6d),
                Arguments.of(1d, createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 6),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 6.1d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 6.1d),
                Arguments.of("1", createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 6),
                Arguments.of("1.1", createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.real, 6.1d),
                Arguments.of(1, createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6d),
                Arguments.of("1.1", createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6.1d)
        );
    }

    private static Stream<Arguments> subArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.integer, 0),
                Arguments.of(0, 1, LuaValue.Type.integer, -1),
                Arguments.of(1, 0, LuaValue.Type.integer, 1),
                Arguments.of(1, 2, LuaValue.Type.integer, -1),
                Arguments.of(Long.MAX_VALUE / 2, Long.MAX_VALUE / 2, LuaValue.Type.integer, 0d),
                Arguments.of(0, -1, LuaValue.Type.integer, 1),
                Arguments.of(-1, 0, LuaValue.Type.integer, -1),
                Arguments.of(-1, -4, LuaValue.Type.integer, 3),
                Arguments.of(-10, 10, LuaValue.Type.integer, -20),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.real, 0.0d),
                Arguments.of(0d, 1d, LuaValue.Type.real, -1d),
                Arguments.of(1d, 0d, LuaValue.Type.real, 1d),
                Arguments.of(1d, 2d, LuaValue.Type.real, -1d),
                Arguments.of(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2, LuaValue.Type.real, 0d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 1d),
                Arguments.of(-1d, 0d, LuaValue.Type.real, -1d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, 3d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, -20d),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.real, -1.01d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, -1.26d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 1.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 0),
                Arguments.of(9, "2", LuaValue.Type.integer, 7),
                Arguments.of("-1", 1, LuaValue.Type.integer, -2),
                Arguments.of(9, "-2", LuaValue.Type.integer, 11),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "2", LuaValue.Type.real, 7d),
                Arguments.of("-1", 1d, LuaValue.Type.real, -2d),
                Arguments.of(9d, "-2", LuaValue.Type.real, 11d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 0.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 6.9d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -2.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, 11.2d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 0.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 6.9d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -2.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, 11.2d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 7),
                Arguments.of("9", "-2", LuaValue.Type.integer, 11),
                Arguments.of("-9", "2", LuaValue.Type.integer, -11),
                Arguments.of("9.1", "2", LuaValue.Type.real, 7.1),
                Arguments.of("9", "2.1", LuaValue.Type.real, 6.9),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 7.0),
                // Table with metatable operation + Number
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 4),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 4d),
                Arguments.of(1, createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.integer, -4),
                Arguments.of(1d, createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.real, -4d),
                Arguments.of(1, createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -4d),
                Arguments.of(1d, createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 4),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 3.9d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 3.9d),
                Arguments.of("1", createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.integer, -4),
                Arguments.of("1.1", createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.real, -3.9d),
                Arguments.of(1, createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -4d),
                Arguments.of("1.1", createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -3.9d)
        );
    }

    private static Stream<Arguments> mulArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.integer, 0),
                Arguments.of(0, 1, LuaValue.Type.integer, 0),
                Arguments.of(1, 0, LuaValue.Type.integer, 0),
                Arguments.of(1, 2, LuaValue.Type.integer, 2),
                Arguments.of(0, -1, LuaValue.Type.integer, 0),
                Arguments.of(-1, 0, LuaValue.Type.integer, 0),
                Arguments.of(-1, -4, LuaValue.Type.integer, 4),
                Arguments.of(-10, 10, LuaValue.Type.integer, -100),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.real, 0.0d),
                Arguments.of(0d, 1d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 0d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 2d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, 0d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, 4d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, -100d),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.real, 2.825d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 2.26d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 2.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 1),
                Arguments.of(9, "2", LuaValue.Type.integer, 18),
                Arguments.of("-1", 1, LuaValue.Type.integer, -1),
                Arguments.of(9, "-2", LuaValue.Type.integer, -18),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 1d),
                Arguments.of(9d, "2", LuaValue.Type.real, 18d),
                Arguments.of("-1", 1d, LuaValue.Type.real, -1d),
                Arguments.of(9d, "-2", LuaValue.Type.real, -18d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 1.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 18.9d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -1.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, -19.8d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 1.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 18.9d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -1.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, -19.8d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 18),
                Arguments.of("9", "-2", LuaValue.Type.integer, -18),
                Arguments.of("-9", "2", LuaValue.Type.integer, -18),
                Arguments.of("9.1", "2", LuaValue.Type.real, 18.2),
                Arguments.of("9", "2.1", LuaValue.Type.real, 18.9),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 19.11),
                // Table with metatable operation + Number
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 5),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(1, createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 5),
                Arguments.of(1d, createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.real, 5d),
                Arguments.of(1, createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5d),
                Arguments.of(1d, createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 5),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 5.5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 5.5d),
                Arguments.of("1", createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 5),
                Arguments.of("1.1", createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.real, 5.5d),
                Arguments.of(1, createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5d),
                Arguments.of("1.1", createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5.5d)
        );
    }

    private static Stream<Arguments> divArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 1, LuaValue.Type.real, 0d),
                Arguments.of(1, 2, LuaValue.Type.real, 0.5d),
                Arguments.of(0, -1, LuaValue.Type.real, 0d),
                Arguments.of(-1, -4, LuaValue.Type.real, 0.25d),
                Arguments.of(-10, 10, LuaValue.Type.real, -1d),
                // Real + Real
                Arguments.of(0d, 1d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 0.5d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, 0.25d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, -1d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 0.442478d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 2.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.real, 1d),
                Arguments.of(9, "2", LuaValue.Type.real, 4.5d),
                Arguments.of("-1", 1, LuaValue.Type.real, -1d),
                Arguments.of(9, "-2", LuaValue.Type.real, -4.5d),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 1d),
                Arguments.of(9d, "2", LuaValue.Type.real, 4.5d),
                Arguments.of("-1", 1d, LuaValue.Type.real, -1d),
                Arguments.of(9d, "-2", LuaValue.Type.real, -4.5d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 1.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 4.285714d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -1.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, -4.090909d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 1.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 4.285714d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -1.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, -4.090909d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.real, 4.5d),
                Arguments.of("9", "-2", LuaValue.Type.real, -4.5d),
                Arguments.of("-9", "2", LuaValue.Type.real, -4.5d),
                Arguments.of("9.1", "2", LuaValue.Type.real, 4.55d),
                Arguments.of("9", "2.1", LuaValue.Type.real, 4.285714d),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 4.333333d),
                // Table with metatable operation + Number
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(1, createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.2d),
                Arguments.of(1d, createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.2d),
                Arguments.of(1, createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.2d),
                Arguments.of(1d, createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.2d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 4.545455d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 4.545455d),
                Arguments.of("1", createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.2d),
                Arguments.of("1.1", createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.22d),
                Arguments.of(1, createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.2d),
                Arguments.of("1.1", createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.22d)
        );
    }

    private static Stream<Arguments> modArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 1, LuaValue.Type.integer, 0),
                Arguments.of(1, 2, LuaValue.Type.integer, 1),
                Arguments.of(0, -1, LuaValue.Type.integer, 0),
                Arguments.of(-1, -4, LuaValue.Type.integer, -1),
                Arguments.of(-10, 10, LuaValue.Type.integer, 0),
                // Real + Real
                Arguments.of(0d, 1d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 1d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, -1d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, 0d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 1d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 0.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 0d),
                Arguments.of(9, "2", LuaValue.Type.integer, 1d),
                Arguments.of("-1", 1, LuaValue.Type.integer, 0d),
                Arguments.of(9, "-2", LuaValue.Type.integer, 1),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "2", LuaValue.Type.real, 1d),
                Arguments.of("-1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "-2", LuaValue.Type.real, 1d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 0.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 0.6d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -0.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, 0.2d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 0.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 0.6d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -0.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, 0.2d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 1),
                Arguments.of("9", "-2", LuaValue.Type.integer, 1),
                Arguments.of("-9", "2", LuaValue.Type.integer, -1d),
                Arguments.of("9.1", "2", LuaValue.Type.real, 1.1d),
                Arguments.of("9", "2.1", LuaValue.Type.real, 0.6d),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 0.7d),
                // Table with metatable operation + Number
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 0),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 0d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 0d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 0d),
                Arguments.of(1, createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 1),
                Arguments.of(1d, createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1d),
                Arguments.of(1, createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of(1d, createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 0),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 0.6d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 0d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 0.6d),
                Arguments.of("1", createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 1),
                Arguments.of("1.1", createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1.1d),
                Arguments.of(1, createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of("1.1", createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1.1d)
        );
    }

    private static Stream<Arguments> powArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.real, 1d),
                Arguments.of(0, 1, LuaValue.Type.real, 0d),
                Arguments.of(1, 0, LuaValue.Type.real, 1d),
                Arguments.of(1, 2, LuaValue.Type.real, 1d),
                Arguments.of(-1, 0, LuaValue.Type.real, 1d),
                Arguments.of(-1, -4, LuaValue.Type.real, 1d),
                Arguments.of(-10, 2, LuaValue.Type.real, 100d),
                Arguments.of(-10, 3, LuaValue.Type.real, -1000d),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.real, 1d),
                Arguments.of(0d, 1d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 0d, LuaValue.Type.real, 1d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 1d),
                Arguments.of(-1d, 0d, LuaValue.Type.real, 1d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, 1d),
                Arguments.of(-10d, 2, LuaValue.Type.real, 100d),
                Arguments.of(-10d, 3, LuaValue.Type.real, -1000d),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.real, 1.655833d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 1d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 2.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.real, 1d),
                Arguments.of(9, "2", LuaValue.Type.real, 81d),
                Arguments.of("-1", 1, LuaValue.Type.real, -1d),
                Arguments.of(9, "-2", LuaValue.Type.real, 0.012346d),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 1d),
                Arguments.of(9d, "2", LuaValue.Type.real, 81d),
                Arguments.of("-1", 1d, LuaValue.Type.real, -1d),
                Arguments.of(9d, "-2", LuaValue.Type.real, 0.012346d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 1.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 100.904206d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -1.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, 0.007955d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 1.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 100.904206d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -1.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, 0.007955d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.real, 81d),
                Arguments.of("9", "-2", LuaValue.Type.real, 0.012346d),
                Arguments.of("-9", "2", LuaValue.Type.real, 81d),
                Arguments.of("9.1", "2", LuaValue.Type.real, 82.81d),
                Arguments.of("9", "2.1", LuaValue.Type.real, 100.904206d),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 103.273031d),
                // Table with metatable operation + Number
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(1, createTableWithPowMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1d),
                Arguments.of(1d, createTableWithPowMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1d),
                Arguments.of(1, createTableWithPowMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of(1d, createTableWithPowMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 5.873095d),
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithPowMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 5.873095d),
                Arguments.of("1", createTableWithPowMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1d),
                Arguments.of("1.1", createTableWithPowMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1.61051d),
                Arguments.of(1, createTableWithPowMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of("1.1", createTableWithPowMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1.61051d)
        );
    }

    private static Stream<Arguments> unmArguments() {
        return Stream.of(
                // Integer
                Arguments.of(1, null, LuaValue.Type.integer, -1),
                Arguments.of(0, null, LuaValue.Type.integer, 0),
                Arguments.of(-1, null, LuaValue.Type.integer, 1),
                // Real
                Arguments.of(1d, null, LuaValue.Type.real, -1d),
                Arguments.of(0d, null, LuaValue.Type.real, 0d),
                Arguments.of(-1d, null, LuaValue.Type.real, 1d),
                // String
                Arguments.of("1", null, LuaValue.Type.real, -1d),
                Arguments.of("0", null, LuaValue.Type.real, 0d),
                Arguments.of("-1", null, LuaValue.Type.real, 1d),
                Arguments.of("1.0", null, LuaValue.Type.real, -1d),
                Arguments.of("0.0", null, LuaValue.Type.real, 0d),
                Arguments.of("-1.0", null, LuaValue.Type.real, 1d),
                // Table with metatable operation
                Arguments.of(createTableWithUnmMetatableAction(new LuaValue(5)), null, LuaValue.Type.integer, -5)
        );
    }

    private static Stream<Arguments> idivArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 1, LuaValue.Type.integer, 0),
                Arguments.of(1, 2, LuaValue.Type.integer, 0),
                Arguments.of(0, -1, LuaValue.Type.integer, 0),
                Arguments.of(-1, -4, LuaValue.Type.integer, 0),
                Arguments.of(-10, 10, LuaValue.Type.integer, -1),
                // Real + Real
                Arguments.of(0d, 1d, LuaValue.Type.integer, 0d),
                Arguments.of(1d, 2d, LuaValue.Type.integer, 0),
                Arguments.of(0d, -1d, LuaValue.Type.integer, 0),
                Arguments.of(-1d, -4d, LuaValue.Type.integer, 0),
                Arguments.of(-10d, 10d, LuaValue.Type.integer, -1),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.integer, 0),
                Arguments.of(2.26d, 1, LuaValue.Type.integer, 2),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 1),
                Arguments.of(9, "2", LuaValue.Type.integer, 4),
                Arguments.of("-1", 1, LuaValue.Type.integer, -1),
                Arguments.of(9, "-2", LuaValue.Type.integer, -4),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.integer, 1),
                Arguments.of(9d, "2", LuaValue.Type.integer, 4),
                Arguments.of("-1", 1d, LuaValue.Type.integer, -1),
                Arguments.of(9d, "-2", LuaValue.Type.integer, -4),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.integer, 1),
                Arguments.of(9, "2.1", LuaValue.Type.integer, 4),
                Arguments.of("-1.3", 1, LuaValue.Type.integer, -1),
                Arguments.of(9, "-2.2", LuaValue.Type.integer, -4),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.integer, 1),
                Arguments.of(9d, "2.1", LuaValue.Type.integer, 4),
                Arguments.of("-1.3", 1d, LuaValue.Type.integer, -1),
                Arguments.of(9d, "-2.2", LuaValue.Type.integer, -4),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 4),
                Arguments.of("9", "-2", LuaValue.Type.integer, -4),
                Arguments.of("-9", "2", LuaValue.Type.integer, -4),
                Arguments.of("9.1", "2", LuaValue.Type.integer, 4),
                Arguments.of("9", "2.1", LuaValue.Type.integer, 4),
                Arguments.of("9.1", "2.1", LuaValue.Type.integer, 4),
                // Table with metatable operation + Number
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 5),
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.integer, 5),
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.integer, 5),
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.integer, 5),
                Arguments.of(1, createTableWithIdivMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 0),
                Arguments.of(1, createTableWithIdivMetatableAction(new LuaValue(5d)), LuaValue.Type.integer, 0),
                Arguments.of(1d, createTableWithIdivMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 0),
                Arguments.of(1d, createTableWithIdivMetatableAction(new LuaValue(5d)), LuaValue.Type.integer, 0),
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 5),
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.integer, 4),
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.integer, 5),
                Arguments.of(createTableWithIdivMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.integer, 4),
                Arguments.of("1", createTableWithIdivMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 0),
                Arguments.of("1.1", createTableWithIdivMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 0),
                Arguments.of(1, createTableWithIdivMetatableAction(new LuaValue(5d)), LuaValue.Type.integer, 0),
                Arguments.of("1.1", createTableWithIdivMetatableAction(new LuaValue(5d)), LuaValue.Type.integer, 0)
        );
    }

    private static Stream<Arguments> concatArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.string, "00"),
                Arguments.of(0, 1, LuaValue.Type.string, "01"),
                Arguments.of(1, 0, LuaValue.Type.string, "10"),
                Arguments.of(1, 2, LuaValue.Type.string, "12"),
                Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, LuaValue.Type.string, "92233720368547758079223372036854775807"),
                Arguments.of(0, -1, LuaValue.Type.string, "0-1"),
                Arguments.of(-1, 0, LuaValue.Type.string, "-10"),
                Arguments.of(-1, -4, LuaValue.Type.string, "-1-4"),
                Arguments.of(-10, 10, LuaValue.Type.string, "-1010"),
                Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, LuaValue.Type.string, "9223372036854775807-9223372036854775808"),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.string, "0.00.0"),
                Arguments.of(0d, 1d, LuaValue.Type.string, "0.01.0"),
                Arguments.of(1d, 0d, LuaValue.Type.string, "1.00.0"),
                Arguments.of(1d, 2d, LuaValue.Type.string, "1.02.0"),
                Arguments.of(Double.MAX_VALUE, Double.MAX_VALUE, LuaValue.Type.string, "1.7976931348623157E3081.7976931348623157E308"),
                Arguments.of(0d, -1d, LuaValue.Type.string, "0.0-1.0"),
                Arguments.of(-1d, 0d, LuaValue.Type.string, "-1.00.0"),
                Arguments.of(-1d, -4d, LuaValue.Type.string, "-1.0-4.0"),
                Arguments.of(-10d, 10d, LuaValue.Type.string, "-10.010.0"),
                Arguments.of(Double.MAX_VALUE, Double.MIN_VALUE, LuaValue.Type.string, "1.7976931348623157E3084.9E-324"),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.string, "1.252.26"),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.string, "12.26"),
                Arguments.of(2.26d, 1, LuaValue.Type.string, "2.261"),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.string, "11"),
                Arguments.of(9, "2", LuaValue.Type.string, "92"),
                Arguments.of("-1", 1, LuaValue.Type.string, "-11"),
                Arguments.of(9, "-2", LuaValue.Type.string, "9-2"),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.string, "11.0"),
                Arguments.of(9d, "2", LuaValue.Type.string, "9.02"),
                Arguments.of("-1", 1d, LuaValue.Type.string, "-11.0"),
                Arguments.of(9d, "-2", LuaValue.Type.string, "9.0-2"),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.string, "1.21"),
                Arguments.of(9, "2.1", LuaValue.Type.string, "92.1"),
                Arguments.of("-1.3", 1, LuaValue.Type.string, "-1.31"),
                Arguments.of(9, "-2.2", LuaValue.Type.string, "9-2.2"),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.string, "1.21.0"),
                Arguments.of(9d, "2.1", LuaValue.Type.string, "9.02.1"),
                Arguments.of("-1.3", 1d, LuaValue.Type.string, "-1.31.0"),
                Arguments.of(9d, "-2.2", LuaValue.Type.string, "9.0-2.2"),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.string, "92"),
                Arguments.of("9", "-2", LuaValue.Type.string, "9-2"),
                Arguments.of("-9", "2", LuaValue.Type.string, "-92"),
                Arguments.of("9.1", "2", LuaValue.Type.string, "9.12"),
                Arguments.of("9", "2.1", LuaValue.Type.string, "92.1"),
                Arguments.of("9.1", "2.1", LuaValue.Type.string, "9.12.1"),
                // Table with metatable operation + Number
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5)), 1, LuaValue.Type.string, "51"),
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.string, "51.0"),
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.string, "5.01"),
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.string, "5.01.0"),
                Arguments.of(1, createTableWithConcatMetatableAction(new LuaValue(5)), LuaValue.Type.string, "15"),
                Arguments.of(1d, createTableWithConcatMetatableAction(new LuaValue(5)), LuaValue.Type.string, "1.05"),
                Arguments.of(1, createTableWithConcatMetatableAction(new LuaValue(5d)), LuaValue.Type.string, "15.0"),
                Arguments.of(1d, createTableWithConcatMetatableAction(new LuaValue(5d)), LuaValue.Type.string, "1.05.0"),
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5)), "1", LuaValue.Type.string, "51"),
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.string, "51.1"),
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.string, "5.01"),
                Arguments.of(createTableWithConcatMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.string, "5.01.1"),
                Arguments.of("1", createTableWithConcatMetatableAction(new LuaValue(5)), LuaValue.Type.string, "15"),
                Arguments.of("1.1", createTableWithConcatMetatableAction(new LuaValue(5)), LuaValue.Type.string, "1.15"),
                Arguments.of(1, createTableWithConcatMetatableAction(new LuaValue(5d)), LuaValue.Type.string, "15.0"),
                Arguments.of("1.1", createTableWithConcatMetatableAction(new LuaValue(5d)), LuaValue.Type.string, "1.15.0")
        );
    }

    private static Stream<Arguments> lenArguments() {
        Function<Integer, LuaValue> getLuaTable = (count) -> {
            HashMap<LuaValue, LuaValue> table = new HashMap<>();
            for (int i = 1; i < count + 1; i++) {
                table.put(new LuaValue(i), LuaValue.NIL_VALUE);
            }
            return new LuaValue(table);
        };
        LuaValue t = getLuaTable.apply(0);
        return Stream.of(
                // Table
                Arguments.of(getLuaTable.apply(0), null, LuaValue.Type.integer, 0),
                Arguments.of(getLuaTable.apply(1), null, LuaValue.Type.integer, 1),
                Arguments.of(getLuaTable.apply(1000), null, LuaValue.Type.integer, 1000),
                // String
                Arguments.of("", null, LuaValue.Type.integer, 0),
                Arguments.of("*", null, LuaValue.Type.integer, 1),
                Arguments.of("*".repeat(1000), null, LuaValue.Type.integer, 1000),
                // Table with metatable operation
                Arguments.of(createTableWithLenMetatableAction(new LuaValue(5)), null, LuaValue.Type.integer, 5)
        );
    }

    private static Stream<Arguments> eqArguments() {
        Function<List<LuaValue>, List<LuaValue>> func1 = (args) -> List.of();
        Function<List<LuaValue>, List<LuaValue>> func2 = (args) -> List.of();
        Map<LuaValue, LuaValue> table1 = Map.of();
        Map<LuaValue, LuaValue> table2 = Map.of(LuaValue.NIL_VALUE, LuaValue.NIL_VALUE);

        ArrayList<Arguments> args = new ArrayList<>(List.of(
                // Nil == Nil
                Arguments.of(null, null, LuaValue.Type.bool, true),
                // Bool == Bool
                Arguments.of(true, true, LuaValue.Type.bool, true),
                Arguments.of(false, false, LuaValue.Type.bool, true),
                Arguments.of(true, false, LuaValue.Type.bool, false),
                Arguments.of(true, false, LuaValue.Type.bool, false),
                // Integer == Integer
                Arguments.of(0, 0, LuaValue.Type.bool, true),
                Arguments.of(0, 1, LuaValue.Type.bool, false),
                // Real == Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.bool, true),
                Arguments.of(0d, 1d, LuaValue.Type.bool, false),
                // Real == Integer and Integer == Real
                Arguments.of(1, 1d, LuaValue.Type.bool, true),
                Arguments.of(1d, 1, LuaValue.Type.bool, true),
                Arguments.of(1, 2.26d, LuaValue.Type.bool, false),
                Arguments.of(2.26d, 1, LuaValue.Type.bool, false),
                // String(Integer) == Integer
                Arguments.of("1", 1, LuaValue.Type.bool, false),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.bool, false),
                // String(Real) + Integer
                Arguments.of("1.0", 1, LuaValue.Type.bool, false),
                // String(Real) + Real
                Arguments.of("1.0", 1d, LuaValue.Type.bool, false),
                // String + String
                Arguments.of("1", "1", LuaValue.Type.bool, true),
                Arguments.of("abc", "abc", LuaValue.Type.bool, true),
                Arguments.of("1", "abc", LuaValue.Type.bool, false),
                // Function == Function
                Arguments.of(func1, func1, LuaValue.Type.bool, true),
                Arguments.of(func2, func1, LuaValue.Type.bool, false),
                // Table == Table
                Arguments.of(table1, table1, LuaValue.Type.bool, true),
                Arguments.of(table2, table1, LuaValue.Type.bool, false),
                // Table with metatable operation + Number
                Arguments.of(createTableWithEqMetatableAction(new LuaValue(5)), createTableWithEqMetatableAction(new LuaValue(5)), LuaValue.Type.bool, true),
                Arguments.of(createTableWithEqMetatableAction(new LuaValue(5)), createTableWithEqMetatableAction(new LuaValue(-5)), LuaValue.Type.bool, false),
                Arguments.of(createTableWithEqMetatableAction(new LuaValue(5)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), LuaValue.Type.bool, true),
                Arguments.of(createTableWithEqMetatableAction(new LuaValue(5)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(-5))), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), createTableWithEqMetatableAction(new LuaValue(5)), LuaValue.Type.bool, true),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(-5))), createTableWithEqMetatableAction(new LuaValue(5)), LuaValue.Type.bool, false),
                Arguments.of(createTableWithEqMetatableAction(new LuaValue(5)), new LuaValue(-5), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(5), createTableWithEqMetatableAction(new LuaValue(5)), LuaValue.Type.bool, false)
        ));

        List<LuaValue> differentValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of()),
                new LuaValue(Map.of())
        );

        for (LuaValue val1 : differentValues) {
            for (LuaValue val2 : differentValues) {
                if (val1.getType() != val2.getType()) {
                    args.add(Arguments.of(val1, val2, LuaValue.Type.bool, false));
                }
            }
        }

        return args.stream();
    }

    private static Stream<Arguments> ltArguments() {
        return Stream.of(
                // Integer < Integer
                Arguments.of(1, 2, LuaValue.Type.bool, true),
                Arguments.of(1, 1, LuaValue.Type.bool, false),
                Arguments.of(2, 1, LuaValue.Type.bool, false),
                // Integer < Real
                Arguments.of(1, 2d, LuaValue.Type.bool, true),
                Arguments.of(1, 1d, LuaValue.Type.bool, false),
                Arguments.of(2, 1d, LuaValue.Type.bool, false),
                // Real < integer
                Arguments.of(1d, 2, LuaValue.Type.bool, true),
                Arguments.of(1d, 1, LuaValue.Type.bool, false),
                Arguments.of(2d, 1, LuaValue.Type.bool, false),
                // Real < Real
                Arguments.of(1d, 2d, LuaValue.Type.bool, true),
                Arguments.of(1d, 1d, LuaValue.Type.bool, false),
                Arguments.of(2d, 1d, LuaValue.Type.bool, false),
                // String < String
                Arguments.of("a", "b", LuaValue.Type.bool, true),
                Arguments.of("a", "a", LuaValue.Type.bool, false),
                Arguments.of("b", "a", LuaValue.Type.bool, false),
                // Table < Table
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(5)), createTableWithLtMetatableAction(new LuaValue(9)), LuaValue.Type.bool, true),
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(10)), createTableWithLtMetatableAction(new LuaValue(9)), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), createTableWithLtMetatableAction(new LuaValue(9)), LuaValue.Type.bool, true),
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(10)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), createTableWithLtMetatableAction(new LuaValue(2)), LuaValue.Type.bool, false),
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(10)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(15))), LuaValue.Type.bool, true)
        );
    }

    private static Stream<Arguments> leArguments() {
        return Stream.of(
                // Integer <= Integer
                Arguments.of(1, 2, LuaValue.Type.bool, true),
                Arguments.of(1, 1, LuaValue.Type.bool, true),
                Arguments.of(2, 1, LuaValue.Type.bool, false),
                // Integer <= Real
                Arguments.of(1, 2d, LuaValue.Type.bool, true),
                Arguments.of(1, 1d, LuaValue.Type.bool, true),
                Arguments.of(2, 1d, LuaValue.Type.bool, false),
                // Real <= integer
                Arguments.of(1d, 2, LuaValue.Type.bool, true),
                Arguments.of(1d, 1, LuaValue.Type.bool, true),
                Arguments.of(2d, 1, LuaValue.Type.bool, false),
                // Real <= Real
                Arguments.of(1d, 2d, LuaValue.Type.bool, true),
                Arguments.of(1d, 1d, LuaValue.Type.bool, true),
                Arguments.of(2d, 1d, LuaValue.Type.bool, false),
                // String <= String
                Arguments.of("a", "b", LuaValue.Type.bool, true),
                Arguments.of("a", "a", LuaValue.Type.bool, true),
                Arguments.of("b", "a", LuaValue.Type.bool, false),
                // Table <= Table  (use le metamethod)
                Arguments.of(createTableWithLeMetatableAction(new LuaValue(5)), createTableWithLeMetatableAction(new LuaValue(9)), LuaValue.Type.bool, true),
                Arguments.of(createTableWithLeMetatableAction(new LuaValue(10)), createTableWithLeMetatableAction(new LuaValue(9)), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), createTableWithLeMetatableAction(new LuaValue(9)), LuaValue.Type.bool, true),
                Arguments.of(createTableWithLeMetatableAction(new LuaValue(10)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), createTableWithLeMetatableAction(new LuaValue(2)), LuaValue.Type.bool, false),
                Arguments.of(createTableWithLeMetatableAction(new LuaValue(10)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(15))), LuaValue.Type.bool, true),
                // Table <= Table (use lt metamethod)
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(5)), createTableWithLtMetatableAction(new LuaValue(9)), LuaValue.Type.bool, true),
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(10)), createTableWithLtMetatableAction(new LuaValue(9)), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), createTableWithLtMetatableAction(new LuaValue(9)), LuaValue.Type.bool, true),
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(10)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), LuaValue.Type.bool, false),
                Arguments.of(new LuaValue(Map.of(new LuaValue("value"), new LuaValue(5))), createTableWithLtMetatableAction(new LuaValue(2)), LuaValue.Type.bool, false),
                Arguments.of(createTableWithLtMetatableAction(new LuaValue(10)), new LuaValue(Map.of(new LuaValue("value"), new LuaValue(15))), LuaValue.Type.bool, true)
        );
    }

    private static Stream<Arguments> indexArguments() {
        return Stream.of(
                Arguments.of(Map.of(new LuaValue("value"), new LuaValue(3)), "value", LuaValue.Type.integer, 3),
                Arguments.of(createTableWithIndexMetatableTable(), "value", LuaValue.Type.string, "value"),
                Arguments.of(createTableWithIndexMetatableFunction(), "value", LuaValue.Type.string, "value")
        );
    }

    private static Stream<Arguments> newIndexArguments() {
        return Stream.of(
                Arguments.of(new HashMap(), new LuaValue(3), "value"),
                Arguments.of(new HashMap(Map.of(new LuaValue(3), new LuaValue(0))), new LuaValue(3), "value"),
                Arguments.of(createTableWithNewIndexMetatableTable(), new LuaValue(3), "value"),
                Arguments.of(createTableWithNewIndexMetatableFunction(), new LuaValue(3), "value")
        );
    }

    private static Stream<Arguments> callArguments() {
        return Stream.of(
                Arguments.of(new LuaValue((args) -> List.of()), List.of(), List.of()),
                Arguments.of(new LuaValue((args) -> List.of(new LuaValue(1))), List.of(), List.of(new LuaValue(1))),
                Arguments.of(createTableWithCallMetatableAction(new LuaValue(1)), List.of(new LuaValue(2), new LuaValue(3)), List.of(new LuaValue(6)))
        );
    }

    private static Stream<Arguments> exceptionArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of()),
                new LuaValue(Map.of())
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val1 : incorrectValues) {
            for (LuaValue val2 : incorrectValues) {
                argList.add(Arguments.of(val1, val2, Arg.first, Arg.second));
            }
        }
        return argList.stream();
    }

    private static Stream<Arguments> concatExceptionArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of()),
                new LuaValue(Map.of())
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val1 : incorrectValues) {
            for (LuaValue val2 : incorrectValues) {
                if (val1.isStringValue() && val2.isStringValue()) {
                    continue;
                }
                if (val1.isStringValue()) {
                    argList.add(Arguments.of(val1, val2, Arg.second, Arg.none));
                } else {
                    argList.add(Arguments.of(val1, val2, Arg.first, Arg.none));
                }
            }
        }
        return argList.stream();
    }

    private static Stream<Arguments> lenExceptionArguments() {
        return Stream.of(
                Arguments.of(null, null, Arg.first),
                Arguments.of(1, null, Arg.first),
                Arguments.of(1d, null, Arg.first),
                Arguments.of(true, null, Arg.first),
                Arguments.of(false, null, Arg.first),
                Arguments.of(new LuaValue((list) -> List.of()), null, Arg.first)
        );
    }

    private static Stream<Arguments> compareExceptionArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of()),
                new LuaValue(Map.of())
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val1 : incorrectValues) {
            for (LuaValue val2 : incorrectValues) {
                if (val1.isStringValue() && val2.isStringValue()) {
                    continue;
                }
                argList.add(Arguments.of(val1, val2, Arg.first, Arg.second));
            }
        }
        return argList.stream();
    }

    private static Stream<Arguments> indexExceptionArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of())
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val : incorrectValues) {
            argList.add(Arguments.of(val, "v", Arg.first, Arg.none));
        }

        return argList.stream();
    }

    private static Stream<Arguments> newIndexExceptionArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of())
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val : incorrectValues) {
            argList.add(Arguments.of(val, 1, 1));
        }

        return argList.stream();
    }

    private static Stream<Arguments> callExceptionArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc")
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val : incorrectValues) {
            argList.add(Arguments.of(val, List.of()));
        }

        return argList.stream();
    }

    private static Stream<Arguments> arithmeticIncorrectMetamethodArguments(LuaValue metamethodKey) {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue(Map.of())
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val : incorrectValues) {
            argList.add(Arguments.of(
                    creteTableWithMetamethod(metamethodKey, val),
                    Map.of(),
                    val
            ));
        }

        return argList.stream();
    }

    private static Stream<Arguments> addIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.ADD_VAlUE);
    }

    private static Stream<Arguments> subIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.SUB_VAlUE);
    }

    private static Stream<Arguments> mulIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.MUL_VAlUE);
    }

    private static Stream<Arguments> divIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.DIV_VAlUE);
    }

    private static Stream<Arguments> modIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.MOD_VAlUE);
    }

    private static Stream<Arguments> powIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.POW_VAlUE);
    }

    private static Stream<Arguments> unmIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.UNM_VAlUE);
    }

    private static Stream<Arguments> idivIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.IDIV_VAlUE);
    }

    private static Stream<Arguments> lenIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.LEN_VAlUE);
    }

    private static Stream<Arguments> eqIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.EQ_VAlUE);
    }

    private static Stream<Arguments> ltIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.LT_VAlUE);
    }

    private static Stream<Arguments> leIncorrectMetamethodArguments() {
        return arithmeticIncorrectMetamethodArguments(LuaValue.LE_VAlUE);
    }

    private static Stream<Arguments> indexIncorrectMetamethodArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc")
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val : incorrectValues) {
            argList.add(Arguments.of(
                    creteTableWithMetamethod(LuaValue.INDEX_VALUE, val),
                    "v",
                    val
            ));
        }

        return argList.stream();
    }

    private static Stream<Arguments> newIndexIncorrectMetamethodArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc")
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val : incorrectValues) {
            argList.add(Arguments.of(
                    creteTableWithMetamethod(LuaValue.NEW_INDEX_VALUE, val), 1, 2,
                    val
            ));
        }

        return argList.stream();
    }

    private static Stream<Arguments> callIncorrectMetamethodArguments() {
        List<LuaValue> incorrectValues = List.of(
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(2d),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue(Map.of())
        );

        List<Arguments> argList = new ArrayList<>();
        for (LuaValue val : incorrectValues) {
            argList.add(Arguments.of(
                    creteTableWithMetamethod(LuaValue.CALL_VAlUE, val), List.of(),
                    val
            ));
        }

        return argList.stream();
    }

    private static LuaValue createTableWithAddMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.ADD_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.add(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.add(val, arg1));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithSubMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.SUB_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.sub(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.sub(arg1, val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithMulMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.MUL_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.mul(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.mul(arg1, val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithDivMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.DIV_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.div(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.div(arg1, val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithModMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.MOD_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.mod(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.mod(arg1, val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithPowMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.POW_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.pow(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.pow(arg1, val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithUnmMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.UNM_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.getFirst();

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.unm(val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithIdivMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.IDIV_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.idiv(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.idiv(arg1, val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithConcatMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.CONCAT_VALUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.concat(val, arg2));
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.concat(arg1, val));
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithLenMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("length"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.LEN_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.getFirst();

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue length = arg1.getTableValue().get(new LuaValue("length"));
                return List.of(length);
            } else {
                return List.of(LuaValue.NIL_VALUE);
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithEqMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.EQ_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table && arg2.getType() == LuaValue.Type.table) {
                LuaValue val1 = arg1.getTableValue().get(new LuaValue("value"));
                LuaValue val2 = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.eq(val1, val2));
            } else {
                return List.of(new LuaValue(false));
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithLtMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.LT_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table && arg2.getType() == LuaValue.Type.table) {
                LuaValue val1 = arg1.getTableValue().get(new LuaValue("value"));
                LuaValue val2 = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.lt(val1, val2));
            } else {
                return List.of(new LuaValue(false));
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithLeMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.LE_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table && arg2.getType() == LuaValue.Type.table) {
                LuaValue val1 = arg1.getTableValue().get(new LuaValue("value"));
                LuaValue val2 = arg2.getTableValue().get(new LuaValue("value"));
                return List.of(LuaValue.le(val1, val2));
            } else {
                return List.of(new LuaValue(false));
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithIndexMetatableFunction() {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.INDEX_VALUE, new LuaValue((args) -> {
            LuaValue arg = args.get(1);

            return List.of(arg);
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithIndexMetatableTable() {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.INDEX_VALUE, new LuaValue(Map.of(
                new LuaValue("value"), new LuaValue("value")
        )));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithNewIndexMetatableFunction() {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.NEW_INDEX_VALUE, new LuaValue((args) -> {
            LuaValue t = args.get(0);
            LuaValue key = args.get(1);
            LuaValue value = args.get(2);
            t.getTableValue().put(key, value);
            return null;
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithNewIndexMetatableTable() {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        LuaValue table = new LuaValue(tableContent);

        LuaValue parent = new LuaValue(new HashMap<>());

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.INDEX_VALUE, parent);
        metatableContent.put(LuaValue.NEW_INDEX_VALUE, parent);
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithCallMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaValue.CALL_VAlUE, new LuaValue((args) -> {
            LuaValue sum = new LuaValue(0);
            for (LuaValue arg : args) {
                sum = LuaValue.add(sum, arg);
            }
            return List.of(LuaValue.add(sum, value));
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue creteTableWithMetamethod(LuaValue key, LuaValue value) {
        LuaValue table = new LuaValue(Map.of());
        LuaValue metatable = new LuaValue(Map.of(key, value));
        table.setMetatable(metatable);
        return table;
    }
}
