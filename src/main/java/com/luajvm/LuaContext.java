package com.luajvm;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an execution context (lexical scope) used during runtime execution.
 *
 * <p>
 * A {@code LuaContext} stores local variables declared in the current scope and
 * provides hierarchical name resolution via a parent context.
 * If a variable is not found locally, lookup continues recursively in parent contexts.
 * </p>
 */
public class LuaContext {

    /**
     * Creates a root (global) context with no parent scope.
     */
    public LuaContext() {
    }

    /**
     * Creates a new context with the specified parent scope.
     *
     * @param parent parent context used for variable resolution;
     *               may be {@code null} to indicate a root (global) context
     */
    public LuaContext(LuaContext parent) {
        this.parent = parent;
    }

    /**
     * Resolves a variable by its identifier.
     *
     * <p>
     * The lookup first checks the current context. If the variable is not found
     * locally and a parent context exists, the lookup continues recursively.
     * </p>
     *
     * @param id variable identifier
     * @return the associated {@link LuaValue} if found; otherwise {@link LuaValue#NIL_VALUE}
     */
    public LuaValue get(String id) {
        if (locals.containsKey(id)) {
            return locals.get(id);
        }

        if (isRootContext()) {
            if (baseFunctions.containsKey(id)) {
                return new LuaValue(baseFunctions.get(id));
            }
            return new LuaValue();
        } else {
            return parent.get(id);
        }
    }

    /**
     * Assigns a value to a variable.
     *
     * <p>
     * If the variable exists in the current context or the current context is the
     * root (global) scope, the value is assigned locally. Otherwise, the assignment is
     * delegated to the parent context.
     * </p>
     *
     * @param id    variable identifier
     * @param value value to assign
     */
    public void set(String id, LuaValue value) {
        if (isRootContext() || locals.containsKey(id)) {
            locals.put(id, value);
        } else {
            parent.set(id, value);
        }
    }

    /**
     * Declares a new local variable initialized with {@code nil}.
     *
     * @param id variable identifier
     */
    public LuaValue declareLocal(String id) {
        LuaValue newValue = new LuaValue();
        locals.put(id, newValue);
        return newValue;
    }

    /**
     * Declares a new local variable with an explicit initial value.
     *
     * @param id    variable identifier
     * @param value initial value
     */
    public void declareLocal(String id, LuaValue value) {
        locals.put(id, value);
    }

    /**
     * Checks whether this context is the root (global) scope.
     *
     * @return {@code true} if this context has no parent; {@code false} otherwise
     */
    private boolean isRootContext() {
        return parent == null;
    }

    /**
     * Parent context used for hierarchical variable resolution.
     */
    private LuaContext parent = null;

    /**
     * Map storing variables declared in this context.
     */
    private final Map<String, LuaValue> locals = new HashMap<>();

    private static Map<String, LuaValue> baseFunctions = Map.of(
            "print", new LuaValue(LuaFunctions::print)
    );
}
