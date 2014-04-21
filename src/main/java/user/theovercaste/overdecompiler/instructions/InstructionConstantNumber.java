package user.theovercaste.overdecompiler.instructions;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Stack;

import user.theovercaste.overdecompiler.datahandlers.ClassData;
import user.theovercaste.overdecompiler.exceptions.InstructionParsingException;
import user.theovercaste.overdecompiler.parserdata.method.MethodAction;
import user.theovercaste.overdecompiler.parserdata.method.MethodActionGetConstant;
import user.theovercaste.overdecompiler.parserdata.method.MethodActionGetConstant.ConstantType;

/**
 * Equivalent to <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.aconst_null">aconst_null</a>
 */
public class InstructionConstantNumber extends Instruction {
	public InstructionConstantNumber(int opcode) {
		super(opcode);
	}

	public static int[] getOpcodes( ) {
		return new int[] {
				0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, // integers -1, 0, 1, 2, 3, 4, 5
				0x09, 0x0A, // longs - 0, 1
				0x0B, 0x0C, 0x0D, // floats, 0.0f, 1.0f, 2.0f
				0x0E, 0x0F}; // doubles, 0.0, 1.0
	}

	@Override
	public boolean isAction( ) {
		return true;
	}

	@Override
	public MethodAction getAction(ClassData originClass, Stack<MethodAction> stack) throws InstructionParsingException {
		switch (getOpcode()) {
			case 0x02:
			case 0x03:
			case 0x04:
			case 0x05:
			case 0x06:
			case 0x07:
			case 0x08:
				return new MethodActionGetConstant(String.valueOf(getOpcode() - 0x03), ConstantType.INT); // iconst_m1's opcode is 02, and it's value is -1. (2--1 = 3) All other values are sequential.
			case 0x09:
			case 0x0A:
				return new MethodActionGetConstant(String.valueOf(getOpcode() - 0x09), ConstantType.LONG); // lconst_0's opcode is 09, and it's value is 0. (9-0 = 9) The only other value is sequential.
			case 0x0B:
			case 0x0C:
			case 0x0D:
				return new MethodActionGetConstant(String.valueOf(getOpcode() - 0x0B), ConstantType.FLOAT); // fconst_0's opcode is 0x0B and it's value is 0. (0B-0 = 0B) All other values are sequential.
			case 0x0E:
			case 0x0F:
				return new MethodActionGetConstant(String.valueOf(getOpcode() - 0x0E), ConstantType.DOUBLE); // dconst_0's opcode is 0x0E, and it's value is 0. (0E-0 = 0E) The only other value is sequential.
			default:
				throw new InstructionParsingException("ConstantNumber's opcode is invalid! (" + opcode + ")");
		}
	}

	public static Factory factory( ) {
		return new Factory();
	}

	public static class Factory extends Instruction.Factory {
		@Override
		public InstructionConstantNumber load(int opcode, DataInputStream din) throws IOException {
			return new InstructionConstantNumber(opcode);
		}
	}
}
