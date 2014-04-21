package user.theovercaste.overdecompiler.instructions;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import user.theovercaste.overdecompiler.exceptions.InvalidInstructionException;

import com.google.common.collect.ImmutableMap;

public class InstructionFactory {
	private static final ImmutableMap<Integer, Instruction.Factory> factoryMap = getFactoryMap();

	private static ImmutableMap<Integer, Instruction.Factory> getFactoryMap( ) {
		ImmutableMap.Builder<Integer, Instruction.Factory> builder = ImmutableMap.<Integer, Instruction.Factory> builder();
		registerInstruction(builder, InstructionConstantNull.getOpcodes(), InstructionConstantNull.factory());
		registerInstruction(builder, InstructionConstantNumber.getOpcodes(), InstructionConstantNumber.factory());
		registerInstruction(builder, InstructionByteIntegerPush.getOpcodes(), InstructionByteIntegerPush.factory());
		registerInstruction(builder, InstructionGetStatic.getOpcodes(), InstructionGetStatic.factory()); // This may be easier with reflection, but it would make the code brittle.
		registerInstruction(builder, InstructionInvokeVirtual.getOpcodes(), InstructionInvokeVirtual.factory());
		registerInstruction(builder, InstructionInvokeStatic.getOpcodes(), InstructionInvokeStatic.factory());
		registerInstruction(builder, InstructionLoadConstant.getOpcodes(), InstructionLoadConstant.factory());
		registerInstruction(builder, InstructionLoad.getOpcodes(), InstructionLoad.factory());
		registerInstruction(builder, InstructionLoadNumbered.getOpcodes(), InstructionLoadNumbered.factory());
		registerInstruction(builder, InstructionStore.getOpcodes(), InstructionStore.factory());
		registerInstruction(builder, InstructionStoreNumbered.getOpcodes(), InstructionStoreNumbered.factory());
		registerInstruction(builder, InstructionAdd.getOpcodes(), InstructionAdd.factory());
		registerInstruction(builder, InstructionReturnVoid.getOpcodes(), InstructionReturnVoid.factory());
		registerInstruction(builder, InstructionReturnValue.getOpcodes(), InstructionReturnValue.factory());
		return builder.build();
	}

	private static void registerInstruction(ImmutableMap.Builder<Integer, Instruction.Factory> builder, int[] opcodes, Instruction.Factory b) {
		for (int i : opcodes) {
			builder.put(i, b); // Duplicate keys already fail
		}
	}

	public static Instruction loadInstruction(int id, DataInputStream din) throws IOException {
		if (factoryMap.containsKey(id)) {
			return factoryMap.get(id).load(id, din);
		}
		throw new InvalidInstructionException("Instruction " + id + " (" + Integer.toHexString(id) + ") isn't defined.");
	}

	public static Instruction loadInstruction(DataInputStream din) throws IOException {
		int id = din.readUnsignedByte();
		if (id < 0) {
			throw new EOFException();
		}
		return loadInstruction(id, din);
	}
}
