package software.ulpgc.chess;

public enum File {
    A('a'),B('b'),C('c'),D('d'),E('e'),F('f'),H('h');

    private final char symbol;

    File(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
