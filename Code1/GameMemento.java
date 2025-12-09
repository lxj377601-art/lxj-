package boardgame;
//游戏备忘录类
public class GameMemento {
    private final Board boardSnapshot;//当前棋盘的快照
    private final Piece currentPlayer;//当前该谁落子
    private final GameStatus status;//当前棋盘的状态

    public GameMemento(Board boardSnapshot, Piece currentPlayer, GameStatus status) {
        this.boardSnapshot = boardSnapshot;
        this.currentPlayer = currentPlayer;
        this.status = status;
    }

    public Board getBoardSnapshot() {
        return boardSnapshot;
    }

    public Piece getCurrentPlayer() {
        return currentPlayer;
    }

    public GameStatus getStatus() {
        return status;
    }
}
