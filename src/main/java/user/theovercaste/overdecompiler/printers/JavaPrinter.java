package user.theovercaste.overdecompiler.printers;

import java.io.PrintStream;

import user.theovercaste.overdecompiler.codeinternals.ClassFlag;
import user.theovercaste.overdecompiler.codeinternals.ClassPath;
import user.theovercaste.overdecompiler.codeinternals.FieldFlag;
import user.theovercaste.overdecompiler.codeinternals.MethodFlag;
import user.theovercaste.overdecompiler.parsers.ParsedClass;
import user.theovercaste.overdecompiler.parsers.ParsedField;
import user.theovercaste.overdecompiler.parsers.ParsedMethod;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public abstract class JavaPrinter extends AbstractPrinter {
	protected boolean printPackage(ParsedClass clazz, PrintStream out) {
		String p = clazz.getPackage();
		if (p.length() > 0) {
			out.print("package ");
			out.print(p);
			out.println(";");
			return true;
		}
		return false;
	}

	protected boolean printImports(ParsedClass clazz, PrintStream out) {
		int count = 0;
		for (ClassPath i : clazz.getImports()) { // import is a keyword, c is already used, i it is!
			if (printImport(clazz, i, out)) {
				count++;
			}
		}
		return count > 0;
	}

	protected boolean printImport(ParsedClass clazz, ClassPath i, PrintStream out) {
		if (i.isObject()) {
			String path = i.getSimplePath();
			if (!path.startsWith("java.lang.") && !i.equals(clazz.getClassPath())) {
				out.println("import " + path + ";");
				return true;
			}
		}
		return false;
	}

	protected void printClassHeader(ParsedClass clazz, PrintStream out) {
		if (clazz.getFlags().contains(ClassFlag.PUBLIC)) {
			out.print("public ");
		}
		if (clazz.getFlags().contains(ClassFlag.FINAL)) {
			out.print("final ");
		}
		switch (clazz.getType()) {
			case ANNOTATION:
				out.print("@interface ");
				break;
			case INTERFACE:
				out.print("interface ");
				break;
			case ENUM:
				out.print("enum ");
				break;
			case ABSTRACT_CLASS:
				out.print("abstract ");
			case CLASS:
				out.print("class ");
				break;
		}
		out.print(clazz.getName());
		ClassPath parent = clazz.getParent();
		out.print(" ");
		if (!(parent.equals(ClassPath.OBJECT) || (isEnum(clazz) && parent.equals(ClassPath.OBJECT_ENUM)))) { // Doesn't extend object, or java.lang.Enum if it's an enum.
			out.print("extends ");
			out.print(parent.getClassName());
			out.print(" ");
		}
		if (clazz.getInterfaces().size() > 0) {
			out.print("implements ");
			out.print(Joiner.on(", ").join(Iterables.transform(clazz.getInterfaces(), new Function<ClassPath, String>() { // Transforms an iterable of classpaths into a CSV string
				@Override
				public String apply(ClassPath input) {
					return input.getClassName();
				}
			})));
			out.print(" ");
		}
		out.print("{");
		out.println();
	}

	protected boolean printFields(ParsedClass clazz, PrintStream out) {
		int count = 0;
		for (ParsedField f : clazz.getFields()) {
			if (printField(clazz, f, out)) {
				count++;
			}
		}
		return count > 0;
	}

	protected boolean printField(ParsedClass clazz, ParsedField f, PrintStream out) {
		if (f.getFlags().contains(FieldFlag.SYNTHETIC)) {
			// don't print
			return false;
		}
		if (f.getFlags().contains(FieldFlag.PUBLIC)) {
			out.print("public ");
		} else if (f.getFlags().contains(FieldFlag.PRIVATE)) {
			out.print("private ");
		} else if (f.getFlags().contains(FieldFlag.PROTECTED)) {
			out.print("protected ");
		}
		if (f.getFlags().contains(FieldFlag.STATIC)) {
			out.print("static ");
		}
		if (f.getFlags().contains(FieldFlag.FINAL)) {
			out.print("final ");
		}
		if (f.getFlags().contains(FieldFlag.TRANSIENT)) {
			out.print("transient ");
		}
		if (f.getFlags().contains(FieldFlag.VOLATILE)) {
			out.print("volatile ");
		}
		out.print(f.getType().getDefinition());
		out.print(" ");
		out.print(f.getName());
		out.print(";");
		out.println();
		return true;
	}

	protected boolean printMethods(ParsedClass clazz, PrintStream out) {
		int count = 0;
		for (ParsedMethod m : clazz.getMethods()) {
			if (printMethodHeader(clazz, m, out)) {
				printFooter(clazz, out);
				count++;
			}
		}
		return count > 0;
	}

	protected boolean printMethodHeader(final ParsedClass clazz, final ParsedMethod m, PrintStream out) {
		if (m.getFlags().contains(MethodFlag.SYNTHETIC)) {
			return false;
		}
		if (m.getFlags().contains(MethodFlag.PUBLIC)) {
			out.print("public ");
		} else if (m.getFlags().contains(MethodFlag.PRIVATE)) {
			out.print("private ");
		} else if (m.getFlags().contains(MethodFlag.PROTECTED)) {
			out.print("protected ");
		}
		if (m.getFlags().contains(MethodFlag.ABSTRACT)) {
			out.print("abstract ");
		}
		else if (m.getFlags().contains(MethodFlag.FINAL)) {
			out.print("final ");
		}
		if (m.getFlags().contains(MethodFlag.STATIC)) {
			out.print("static ");
		}
		if (m.getFlags().contains(MethodFlag.SYNCHRONIZED)) {
			out.print("synchronized ");
		}
		if (m.getFlags().contains(MethodFlag.NATIVE)) {
			out.print("native ");
		}
		if (m.getName().equals("<init>")) {
			out.print(clazz.getName());
		}
		else {
			if (m.getReturnType().isPresent()) {
				ClassPath returnType = m.getReturnType().get();
				out.print(returnType.getDefinition());
				out.print(" ");
			} else {
				out.print("void ");
			}
			out.print(m.getName());
		}
		out.print("(");
		if (!m.getArguments().isEmpty()) {
			out.print(Joiner.on(", ").join(Iterables.transform(m.getArguments(), new Function<ClassPath, String>() {
				@Override
				public String apply(ClassPath input) {
					return input.getClassName() + " " + getArgumentName(clazz, m, input);
				}
			})));
		}
		out.print(") {");
		out.println();
		return true;
	}

	protected void printFooter(ParsedClass clazz, PrintStream out) {
		out.println("}");
	}
}