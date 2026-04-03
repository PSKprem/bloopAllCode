package parser;

import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.List;
import ast.expression.*;
import ast.instruction.*;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token advance() {
        return tokens.get(pos++);
    }

    private boolean match(TokenType type) {
        if (peek().getType() == type) {
            advance();
            return true;
        }
        return false;
    }

    private Expression parsePrimary() {
        Token token = advance();

        switch (token.getType()) {
            case NUMBER:
                return new NumberNode(Double.parseDouble(token.getValue()));

            case STRING:
                return new StringNode(token.getValue());

            case IDENTIFIER:
                return new VariableNode(token.getValue());

            default:
                throw new RuntimeException("Unexpected token: " + token.getType());
        }
    }

    private Expression parseTerm() {
        Expression expr = parsePrimary();

        while (true) {
            if (match(TokenType.STAR)) {
                expr = new BinaryOpNode(expr, "*", parsePrimary());
            } else if (match(TokenType.SLASH)) {
                expr = new BinaryOpNode(expr, "/", parsePrimary());
            } else {
                break;
            }
        }

        return expr;
    }

    public Expression parseExpression() {
        Expression expr = parseTerm();

        while (true) {
            if (match(TokenType.PLUS)) {
                expr = new BinaryOpNode(expr, "+", parseTerm());
            } else if (match(TokenType.MINUS)) {
                expr = new BinaryOpNode(expr, "-", parseTerm());
            } else {
                break;
            }
        }

        return expr;
    }

    public Expression parseComparison() {
        Expression expr = parseExpression();

        while (true) {
            if (match(TokenType.GREATER)) {
                expr = new BinaryOpNode(expr, ">", parseExpression());
            } else if (match(TokenType.LESS)) {
                expr = new BinaryOpNode(expr, "<", parseExpression());
            } else if (match(TokenType.EQUAL_EQUAL)) {
                expr = new BinaryOpNode(expr, "==", parseExpression());
            } else {
                break;
            }
        }

        return expr;
    }

    public List<Instruction> parse() {
        List<Instruction> instructions = new ArrayList<>();

        while (peek().getType() != TokenType.EOF) {
            if (match(TokenType.NEWLINE)) continue;
            instructions.add(parseInstruction());
        }

        return instructions;
    }

    private Instruction parseInstruction() {
        if (match(TokenType.PUT)) {
            return parseAssign();
        }

        if (match(TokenType.PRINT)) {
            return parsePrint();
        }

        if (match(TokenType.IF)) {
            return parseIf();
        }

        if (match(TokenType.REPEAT)) {
            return parseRepeat();
        }

        throw new RuntimeException("Unknown instruction " + peek().getType() + " at line " + peek().getLine());
    }

    private Instruction parseRepeat() {
    Token countToken = advance();
    int count = Integer.parseInt(countToken.getValue());

    if (!match(TokenType.TIMES)) {
        throw new RuntimeException("Expected 'times'");
    }

    if (!match(TokenType.COLON)) {
        throw new RuntimeException("Expected ':'");
    }

    if (!match(TokenType.NEWLINE)) {
        throw new RuntimeException("Expected newline");
    }

    int blockIndent = peekIndentLevel();

    List<Instruction> body = new ArrayList<>();

    while (peek().getType() != TokenType.EOF) {

        if (peek().getType() == TokenType.NEWLINE) {
            advance();
            continue;
        }

        int currentIndent = peekIndentLevel();

        if (currentIndent < blockIndent) break;

        consumeIndent(currentIndent);

        body.add(parseInstruction());

        match(TokenType.NEWLINE);
    }

    return new RepeatInstruction(count, body);
}


    private Instruction parseIf() {
    Expression condition = parseComparison();

    if (!match(TokenType.THEN)) {
        throw new RuntimeException("Expected 'then'");
    }

    if (!match(TokenType.COLON)) {
        throw new RuntimeException("Expected ':'");
    }

    if (!match(TokenType.NEWLINE)) {
        throw new RuntimeException("Expected newline");
    }

    int blockIndent = peekIndentLevel();

    List<Instruction> body = new ArrayList<>();

    while (peek().getType() != TokenType.EOF) {

        if (peek().getType() == TokenType.NEWLINE) {
            advance();
            continue;
        }

        int currentIndent = peekIndentLevel();

        if (currentIndent < blockIndent) break;

        consumeIndent(currentIndent);

        body.add(parseInstruction());

        match(TokenType.NEWLINE);
    }

    return new IfInstruction(condition, body);
}

    private Instruction parseAssign() {
        Expression expr = parseComparison();

        if (!match(TokenType.INTO)) {
            throw new RuntimeException("Expected 'into'");
        }

        Token nameToken = advance();

        return new AssignInstruction(nameToken.getValue(), expr);
    }

    private Instruction parsePrint() {
        Expression expr = parseComparison();
        return new PrintInstruction(expr);
    }

    private int peekIndentLevel() {
    int tempPos = pos;
    int count = 0;

    while (tempPos < tokens.size() &&
           tokens.get(tempPos).getType() == TokenType.TAB) {
        count++;
        tempPos++;
    }

    return count;
}

private void consumeIndent(int count) {
    for (int i = 0; i < count; i++) {
        match(TokenType.TAB);
    }
}


}
