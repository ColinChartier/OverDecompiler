package user.theovercaste.overdecompiler.instructions;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Stack;

import user.theovercaste.overdecompiler.datahandlers.ClassData;
import user.theovercaste.overdecompiler.exceptions.*;
import user.theovercaste.overdecompiler.parserdata.method.*;

public class InstructionStoreNumbered extends AbstractInstructionDirectAction {
    public InstructionStoreNumbered(int opcode, int byteIndex, int instructionIndex, int lineNumber) {
        super(opcode, byteIndex, instructionIndex, lineNumber);
    }

    public InstructionStoreNumbered(int opcode, int byteIndex, int instructionIndex) {
        super(opcode, byteIndex, instructionIndex);
    }

    public int getNumber( ) {
        return (opcode - 0x3b) & 3; // For clarity, this is (roughly) equivalent to (getOpcodes( )[0]) % 4, which takes advantage of the fact that all of the xstore_n operations are sequential.
    }

    @Override
    public MethodAction getAction(ClassData originClass, Stack<MethodMember> stack) throws InstructionParsingException {
        if (stack.isEmpty()) {
            throw new EndOfStackException();
        }
        MethodMember a = stack.pop();
        if (a instanceof MethodActionGetter) {
            return new MethodActionSetVariable(getNumber(), (MethodActionGetter) a);
        } else {
            throw new InvalidStackTypeException(a);
        }
    }

    @Override
    public int getByteSize( ) {
        return 0;
    }

    public static int[] getOpcodes( ) {
        return new int[] {
                0x3b, 0x3c, 0x3d, 0x3e, // istore
                0x3f, 0x40, 0x41, 0x42, // lstore
                0x43, 0x44, 0x45, 0x46, // fstore
                0x47, 0x48, 0x49, 0x4a, // dstore
                0x4b, 0x4c, 0x4d, 0x4e // astore
        };
    }

    public static Factory factory( ) {
        return new Factory();
    }

    public static class Factory extends Instruction.Factory {
        @Override
        public InstructionStoreNumbered load(int opcode, DataInputStream din) throws IOException {
            return new InstructionStoreNumbered(opcode, byteIndex, instructionIndex, lineNumber);
        }
    }
}
