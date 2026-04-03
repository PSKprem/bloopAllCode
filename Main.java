import interpreter.Interpreter;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Provide .bloop file path");
            return;
        }

        String source = Files.readString(Path.of(args[0]));

        Interpreter interpreter = new Interpreter();
        String normalize = source.replace("    ", "\t");
        interpreter.run(normalize);
    }
}