package token;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int pos = 0;
    private int line = 1;

    public Tokenizer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            char current = peek();
            if (current == '\t') {
                tokens.add(new Token(TokenType.TAB, "\\t", line));
                advance();
            } else if (current == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\\n", line));
                line++;
                advance();
            } else if (current == ' ' || current == '\r') {
                advance();
            } else if (Character.isDigit(current)) {
                tokenizeNumber();
            } else if (current == '"') {
                tokenizeString();
            } else if (Character.isLetter(current)) {
                tokenizeWord();
            } else {
                tokenizeSymbol();
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    private void tokenizeNumber() {
        int start = pos;

        while (!isAtEnd() && Character.isDigit(peek())) {
            advance();
        }

        String value = source.substring(start, pos);
        tokens.add(new Token(TokenType.NUMBER, value, line));
    }

    private void tokenizeString() {
        advance();

        int start = pos;

        while (!isAtEnd() && peek() != '"') {
            advance();
        }

        String value = source.substring(start, pos);

        advance();

        tokens.add(new Token(TokenType.STRING, value, line));
    }

    private void tokenizeWord() {
        int start = pos;

        while (!isAtEnd() && Character.isLetter(peek())) {
            advance();
        }

        String word = source.substring(start, pos);

        TokenType type = switch (word) {
            case "put" -> TokenType.PUT;
            case "into" -> TokenType.INTO;
            case "print" -> TokenType.PRINT;
            case "if" -> TokenType.IF;
            case "then" -> TokenType.THEN;
            case "repeat" -> TokenType.REPEAT;
            case "times" -> TokenType.TIMES;
            default -> TokenType.IDENTIFIER;
        };

        tokens.add(new Token(type, word, line));
    }

    private void tokenizeSymbol() {
        char c = advance();

        switch (c) {
            case '+' -> tokens.add(new Token(TokenType.PLUS, "+", line));
            case '-' -> tokens.add(new Token(TokenType.MINUS, "-", line));
            case '*' -> tokens.add(new Token(TokenType.STAR, "*", line));
            case '/' -> tokens.add(new Token(TokenType.SLASH, "/", line));
            case '>' -> tokens.add(new Token(TokenType.GREATER, ">", line));
            case '<' -> tokens.add(new Token(TokenType.LESS, "<", line));
            case ':' -> tokens.add(new Token(TokenType.COLON, ":", line));

            case '=' -> {
                if (!isAtEnd() && peek() == '=') {
                    advance();
                    tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", line));
                }
            }

            default -> throw new RuntimeException("Unexpected character: " + c + " at line " + line);
        }
    }

    private boolean isAtEnd() {
        return pos >= source.length();
    }

    private char peek() {
        return source.charAt(pos);
    }

    private char advance() {
        return source.charAt(pos++);
    }
}