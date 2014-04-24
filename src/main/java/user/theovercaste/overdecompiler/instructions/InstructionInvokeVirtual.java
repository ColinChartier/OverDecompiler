package user.theovercaste.overdecompiler.instructions;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Stack;

import user.theovercaste.overdecompiler.codeinternals.ClassPath;
import user.theovercaste.overdecompiler.constantpool.ConstantPoolEntry;
import user.theovercaste.overdecompiler.constantpool.ConstantPoolEntryMethodReference;
import user.theovercaste.overdecompiler.datahandlers.ClassData;
import user.theovercaste.overdecompiler.exceptions.EndOfStackException;
import user.theovercaste.overdecompiler.exceptions.InstructionParsingException;
import user.theovercaste.overdecompiler.exceptions.InvalidConstantPoolPointerException;
import user.theovercaste.overdecompiler.parserdata.method.MethodAction;
import user.theovercaste.overdecompiler.parserdata.method.MethodActionGetter;
import user.theovercaste.overdecompiler.parserdata.method.MethodActionInvokeMethod;

public class InstructionInvokeVirtual extends Instruction {
	private final int methodIndex;

	public InstructionInvokeVirtual(int opcode, int methodIndex) {
		super(opcode);
		this.methodIndex = methodIndex;
	}

	public ConstantPoolEntryMethodReference getMethod(ConstantPoolEntry[] constantPool) throws InvalidConstantPoolPointerException {
		ConstantPoolEntry e = constantPool[methodIndex];
		if (e instanceof ConstantPoolEntryMethodReference) {
			return (ConstantPoolEntryMethodReference) e;
		}
		throw new InvalidConstantPoolPointerException("Invoke virtual has an invalid reference: " + methodIndex + ".");
	}

	@Override
	public boolean isAction( ) {
		return true;
	}

	@Override
	public MethodAction getAction(int lineNumber, ClassData originClass, Stack<MethodAction> stack) throws InstructionParsingException {
		try {
			String descriptor = getMethod(originClass.getConstantPool()).getDescription(originClass.getConstantPool());
			Collection<ClassPath> arguments = ClassPath.getMethodArguments(descriptor);
			MethodAction[] actions = new MethodAction[arguments.size()];
			for (int i = 0; i < arguments.size(); i++) { // TODO stack integrity checks
				if (stack.isEmpty()) {
					throw new EndOfStackException();
				}
				MethodAction a = stack.pop();
				actions[i] = a;
			}
			if (stack.isEmpty()) {
				throw new EndOfStackException();
			}
			MethodAction a = stack.pop();
			if (a instanceof MethodActionGetter) {
				return new MethodActionInvokeMethod(lineNumber, (MethodActionGetter) a, getMethod(originClass.getConstantPool()).getName(originClass.getConstantPool()), actions);
			} else {
				throw new InstructionParsingException("The instruction which owns this invoker isn't a proper type! (" + a.getClass().getName() + ")");
			}
		} catch (InvalidConstantPoolPointerException e) {
			throw new InstructionParsingException(e);
		}
	}

	@Override
	public int getByteSize( ) {
		return 2;
	}

	public static int[] getOpcodes( ) {
		return new int[] {0xb6};
	}

	public static Factory factory( ) {
		return new Factory();
	}

	public static class Factory extends Instruction.Factory {
		@Override
		public InstructionInvokeVirtual load(int opcode, DataInputStream din) throws IOException {
			int methodIndex = din.readUnsignedShort();
			return new InstructionInvokeVirtual(opcode, methodIndex);
		}
	}
}
