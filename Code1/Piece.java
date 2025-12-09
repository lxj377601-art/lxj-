package boardgame;
//棋子类
public enum Piece {
    EMPTY('.'),//空格子
    BLACK('B'),//黑棋
    WHITE('W');//白棋

    private final char symbol;

    Piece(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    //获取当前棋子的相反状态
    public Piece opposite() {
        if (this == BLACK) return WHITE;
        if (this == WHITE) return BLACK;
        return EMPTY;
    }
}
