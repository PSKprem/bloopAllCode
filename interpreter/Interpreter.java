package interpreter;

import token.Tokenizer;
import parser.Parser;
import runtime.Environment;
import ast.instruction.Instruction;

import java.util.List;

public class Interpreter {

    public void run(String sourceCode) {
        Tokenizer tokenizer = new Tokenizer(sourceCode);
        var tokens = tokenizer.tokenize();

        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();

        Environment env = new Environment();

        for (Instruction instr : instructions) {
            instr.execute(env);
        }
    }
}