package user.theovercaste.overdecompiler.parserdata.method;

import user.theovercaste.overdecompiler.parserdata.ParsedClass;
import user.theovercaste.overdecompiler.parserdata.ParsedMethod;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class MethodActionInvokeMethod extends MethodActionGetter {
    private final MethodActionGetter invokee;
    private final String method;
    private final ImmutableList<MethodAction> arguments;

    public MethodActionInvokeMethod(MethodActionGetter invokee, String method, ImmutableList<MethodAction> arguments) {
        this.invokee = invokee;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public String getStringValue(final ParsedClass c, final ParsedMethod parent) {
        return invokee.getStringValue(c, parent) + "." +
                method + "(" +
                Joiner.on(", ").join(Iterables.transform(arguments, new Function<MethodAction, String>() {
                    @Override
                    public String apply(MethodAction input) {
                        return input.getStringValue(c, parent);
                    }
                })) +
                ")";
    }

    public MethodActionGetter getInvokee( ) {
        return invokee;
    }

    public String getMethod( ) {
        return method;
    }

    public ImmutableList<MethodAction> getArguments( ) {
        return arguments;
    }
}
