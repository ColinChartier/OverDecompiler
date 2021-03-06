package user.theovercaste.overdecompiler.parsers.javaparser.subparsers.methodparsers;

import user.theovercaste.overdecompiler.parseddata.methodmembers.MethodAction;
import user.theovercaste.overdecompiler.parseddata.methodmembers.MethodActionLoadParameter;
import user.theovercaste.overdecompiler.util.ClassPath;

/**
 * A special subset of {@link MethodActionPointer}s that reference a specific parameter.
 * These will always return a MethodActionLoadParameter when referenced from a MethodDecompileContext
 * 
 * @author <a href="http://www.reddit.com/user/TheOverCaste/">OverCaste</a>
 *
 */
public final class MethodActionParameterPointer extends MethodActionPointer {
    private final ClassPath type;

    MethodActionParameterPointer(int index, ClassPath type) {
        super(index);
        this.type = type;
    }

    public ClassPath getType( ) {
        return type;
    }

    @Override
    public String toString( ) {
        return "[Parameter pointer to index " + index + "]";
    }

    @Override
    public MethodAction get(MethodPrintingContext printingContext) {
        return new MethodActionLoadParameter(index, type);
    }

    @Override
    public int hashCode( ) {
        return 17 * index;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MethodActionParameterPointer) {
            return other.getClass().equals(getClass()) && ((MethodActionParameterPointer) other).index == index;
        }
        return false;
    }
}
