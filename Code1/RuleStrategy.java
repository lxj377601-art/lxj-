package boardgame;
//规则的策略接口，定义了围棋和五子棋的通用规则接口
public interface RuleStrategy {

    /**
     * 在 board 上为 currentPlayer 在 (x,y) 落子。
     * 坐标为 0-based。若非法，返回带错误信息的 Result。
     */
    MoveResult placePiece(Board board, int x, int y, Piece currentPlayer);

    /**
     * 围棋使用：虚着（pass），五子棋中可以直接返回 ONGOING。
     */
    MoveResult pass(Board board, Piece currentPlayer);

    //一次落子后的返回结果
    class MoveResult {
        private final boolean success;//落子是否成功
        private final String message;//执行后的消息
        private final GameStatus status;//游戏当前的状态

        public MoveResult(boolean success, String message, GameStatus status) {
            this.success = success;
            this.message = message;
            this.status = status;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public GameStatus getStatus() {
            return status;
        }
    }
}
