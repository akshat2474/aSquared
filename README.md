# A² Programming Language

**A² (A-squared)** is a lightweight, C-style interpreted language built in Java.
It’s designed to teach compiler and interpreter design with familiar curly-brace syntax and simple semantics.

## Features

* C-style syntax with `;` and `{}`
* Variables via `let`
* Arithmetic, comparison & string operations
* `print` statements for output
* `if` / `else` conditionals
* `while` loops
* `#` comments


## Quick Start

### Prerequisites

* Java 8 or higher
* Terminal or command prompt

### Build & Run

```bash
git clone https://github.com/akshat2474/aSquared.git
cd aSquared
mkdir -p bin
javac -d bin src/*.java
java -cp bin ASquared examples/example1.a2
```


## Language Basics

```a2
let x = 10;
let y = 5;

if (x > y) {
    print "x is greater!";
}

while (y <= x) {
    print y;
    let y = y + 1;
}

let name = "A²";
print "Hello, " + name;
print 3 * (5 + 2);
```


## Project Layout

```text
src/
 ├── Lexer.java        # Tokenizer
 ├── Parser.java       # AST builder
 ├── AST.java          # Node definitions
 ├── Interpreter.java  # Evaluator
 └── ASquared.java     # Entry point
examples/
 ├── example1.a2
 └── example2.a2
```


## Grammar

```text
program         → statement* EOF
statement       → letStmt | printStmt | ifStmt | whileStmt
letStmt         → "let" IDENT "=" expression ";"
printStmt       → "print" expression ";"
expression      → comparison
comparison      → additive (("==" | "!=" | "<" | ">") additive)*
additive        → multiplicative (("+" | "-") multiplicative)*
multiplicative  → unary (("*" | "/" | "%") unary)*
unary           → "-" unary | primary
primary         → NUMBER | STRING | IDENT | "(" expression ")"
```





