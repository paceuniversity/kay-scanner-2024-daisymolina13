package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private boolean isEof = false;
    private char nextChar = ' ';
    private BufferedReader input;

    private static final String[] keywords = {
            "bool", "else", "if", "integer", "main", "while"
    };

    public boolean isEoFile() {
        return isEof;
    }

    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
            nextChar = readChar();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            isEof = true;
        }
    }

    public Token nextToken() {
        Token t = new Token();
        t.setType("Other");
        t.setValue("");

        skipWhiteSpace();

        // --- COMMENTS ---
        while (nextChar == '/') {
            nextChar = readChar();
            if (nextChar == '/') {
                while (!isEof && !isEndOfLine(nextChar)) {
                    nextChar = readChar();
                }
                skipWhiteSpace();
                return nextToken();
            } else {
                t.setType("Operator");
                t.setValue("/");
                return t;
            }
        }

        // --- DOUBLE STAR ("**") ---
        if (nextChar == '*') {
            char first = nextChar;
            nextChar = readChar();
            if (nextChar == '*') {
                // return first "*"
                Token star1 = new Token();
                star1.setType("Operator");
                star1.setValue("*");
                return star1;
            }
            // normal "*"
            t.setType("Operator");
            t.setValue("*");
            return t;
        }

        // --- OPERATORS ---
        if (isOperator(nextChar)) {
            char c = nextChar;
            t.setValue("" + c);
            t.setType("Operator");
            nextChar = readChar();

            switch (c) {

            case '<':
            case '>':
                if (nextChar == '=') {
                    t.setValue(c + "=");
                    nextChar = readChar();
                }
                return t;

            case '=':
                if (nextChar == '=') {
                    t.setValue("==");
                    nextChar = readChar();
                    return t;
                }
                t.setType("Other"); // single '=' is Other
                return t;

            case '!':
                if (nextChar == '=') {
                    t.setValue("!=");
                    nextChar = readChar();
                }
                return t;

            case ':':
                if (nextChar == '=') {
                    t.setValue(":=");
                    nextChar = readChar();
                } else {
                    t.setType("Other");
                }
                return t;

            case '&':
                if (nextChar == '&') {
                    t.setValue("&&");
                    nextChar = readChar();
                } else {
                    t.setType("Other");
                }
                return t;

            case '|':
                if (nextChar == '|') {
                    t.setValue("||");
                    nextChar = readChar();
                } else {
                    t.setType("Other");
                }
                return t;

            default:
                return t;
            }
        }

        // --- SEPARATORS ---
        if (isSeparator(nextChar)) {
            t.setType("Separator");
            t.setValue("" + nextChar);
            nextChar = readChar();
            return t;
        }

        // --- IDENTIFIERS / KEYWORDS / TRUE/FALSE ---
        if (isLetter(nextChar)) {
            t.setType("Identifier");
            while (isLetter(nextChar) || isDigit(nextChar)) {
                t.setValue(t.getValue() + nextChar);
                nextChar = readChar();
            }
            if (isKeyword(t.getValue()))
                t.setType("Keyword");
            else if (t.getValue().equals("True") || t.getValue().equals("False"))
                t.setType("Literal");
            return t;
        }

        // --- NUMBERS + DECIMAL CASE ---
        if (isDigit(nextChar)) {
            t.setType("Literal");
            while (isDigit(nextChar)) {
                t.setValue(t.getValue() + nextChar);
                nextChar = readChar();
            }

            // decimal case: 2.5 is Other("2.") Literal("5")
            if (nextChar == '.') {
                t.setType("Other");
                t.setValue(t.getValue() + ".");
                nextChar = readChar();
                return t;
            }

            return t;
        }

        // --- UNKNOWN / OTHER ---
        if (isEof) {
            return t;
        }

        while (!isEndOfToken(nextChar) && !isEof) {
            t.setValue(t.getValue() + nextChar);
            nextChar = readChar();
        }

        skipWhiteSpace();
        return t;
    }

    private char readChar() {
        int i = 0;
        if (isEof)
            return (char) 0;
        try {
            i = input.read();
        } catch (IOException e) {
            System.exit(-1);
        }
        if (i == -1) {
            isEof = true;
            return (char) 0;
        }
        return (char) i;
    }

    private boolean isKeyword(String s) {
        for (String k : keywords)
            if (k.equals(s))
                return true;
        return false;
    }

    private boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f';
    }

    private boolean isEndOfLine(char c) {
        return c == '\r' || c == '\n' || c == '\f';
    }

    private boolean isEndOfToken(char c) {
        return isWhiteSpace(c) || isOperator(c) || isSeparator(c) || isEof;
    }

    private void skipWhiteSpace() {
        while (!isEof && isWhiteSpace(nextChar)) {
            nextChar = readChar();
        }
    }

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == ';' || c == ',';
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' ||
               c == '<' || c == '>' || c == '!' || c == '&' ||
               c == '|' || c == ':';
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public boolean isEndofFile() {
        return isEof;
    }
}
