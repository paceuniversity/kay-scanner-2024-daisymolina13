package com.scanner.project;
// TokenStream.java

// Daisy Molina & Uday Brathwaite

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    // READ THE COMPLETE FILE FIRST
    // You will need to adapt it to KAY, NOT JAY

    // Instance variables
    private boolean isEof = false; // is end of file
    private char nextChar = ' '; // next character in input stream
    private BufferedReader input;

    // This function was added to make the demo file work
    public boolean isEoFile() {
        return isEof;
    }

    // Constructor
    // Pass a filename for the program text as a source for the TokenStream.
    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            // System.exit(1); // Removed to allow ScannerDemo to continue
            // running after the input file is not found.
            isEof = true;
        }
    }

    public Token nextToken() { // Main function of the scanner
                                // Return next token type and value.
        Token t = new Token();
        t.setType("Other"); // For now it is Other
        t.setValue("");

        skipWhiteSpace();
        while (nextChar == '/') {
            nextChar = readChar();
            if (nextChar == '/') {
                while (!isEndOfLine(nextChar) && !isEof) {
                    nextChar = readChar();
                }
                skipWhiteSpace();
            } else {
                t.setValue("/");
                t.setType("Operator");
                return t;
            }
        }
        if (isOperator(nextChar)) {
            t.setType("Operator");
            t.setValue(t.getValue() + nextChar);
            switch (nextChar) {
            case '<':
                nextChar = readChar();
                if (nextChar == '=') {
                    t.setValue(t.getValue() + nextChar);
                    nextChar = readChar();
                }
                return t;
            case '>':
                nextChar = readChar();
                if (nextChar == '=') {
                    t.setValue(t.getValue() + nextChar);
                    nextChar = readChar();
                }
                return t;
            case '=':
                nextChar = readChar();
                if (nextChar == '=') {
                    t.setValue(t.getValue() + nextChar);
                    nextChar = readChar();
                    return t;
                }
                t.setType("Other");
                return t;
            case '!':
                nextChar = readChar();
                if (nextChar == '=') {
                    t.setValue(t.getValue() + nextChar);
                    nextChar = readChar();
                    return t;
                }
                return t;
            case ':':
                nextChar = readChar();
                if (nextChar == '=') {
                    t.setValue(t.getValue() + nextChar);
                    nextChar = readChar();
                    return t;
                } else {
                    t.setType("Other");
                }
                return t;
            case '|':
                nextChar = readChar();
                if (nextChar == '|') {
                    t.setValue(t.getValue() + nextChar);
                    nextChar = readChar();
                    return t;
                } else {
                    t.setType("Other");
                }
                return t;
            case '&':
                nextChar = readChar();
                if (nextChar == '&') {
                    t.setValue(t.getValue() + nextChar);
                    nextChar = readChar();
                    return t;
                } else {
                    t.setType("Other");
                }
                return t;
            default:
                nextChar = readChar();
                return t;
            }
        }

        // Then check for a separator
        if (isSeparator(nextChar)) {
            t.setType("Separator");
            t.setValue(String.valueOf(nextChar));
            nextChar = readChar();
            return t;
        }

        // Then check for an identifier, keyword, or literal.
        if (isLetter(nextChar)) {
            t.setType("Identifier");
            while ((isLetter(nextChar) || isDigit(nextChar))) {
                t.setValue(t.getValue() + nextChar);
                nextChar = readChar();
            }
            // Check if it's a keyword
            if (isKeyword(t.getValue())) {
                t.setType("Keyword");
            }
            // Check if it's a boolean literal 
            else if (t.getValue().equals("True") || t.getValue().equals("False")) {
                t.setType("Literal");
            }
            if (isEndOfToken(nextChar)) {
                return t;
            }
        }

        if (isDigit(nextChar)) {
            t.setType("Literal");
            while (isDigit(nextChar)) {
                t.setValue(t.getValue() + nextChar);
                nextChar = readChar();
            }
            if (isEndOfToken(nextChar)) {
                return t;
            }
        }

        t.setType("Other");
       
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
        System.out.flush();
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
        return s.equals("bool") || s.equals("else") || s.equals("if") ||
               s.equals("integer") || s.equals("main") || s.equals("while");
    }

    private boolean isWhiteSpace(char c) {
        return (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f');
    }

    private boolean isEndOfLine(char c) {
        return (c == '\r' || c == '\n' || c == '\f');
    }

    private boolean isEndOfToken(char c) {
        return (isWhiteSpace(nextChar) || isOperator(nextChar) || isSeparator(nextChar) || isEof);
    }

    private void skipWhiteSpace() {
        while (!isEof && isWhiteSpace(nextChar)) {
            nextChar = readChar();
        }
    }

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == ',' || c == ';';
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' ||
               c == '<' || c == '>' || c == '=' || c == '!' ||
               c == '|' || c == '&' || c == ':';
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public boolean isEndofFile() {
        return isEof;
    }
}

