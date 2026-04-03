package ast.instruction;

import runtime.Environment;

public interface Instruction {
    void execute(Environment env);
}