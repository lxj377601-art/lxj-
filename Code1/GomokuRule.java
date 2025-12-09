package boardgame;
//具体策略-五子棋规则策略
public class GomokuRule implements RuleStrategy {
    //五子棋一旦5子相连，则获胜
    private final int winCount = 5;

    //落子操作
    @Override
    public MoveResult placePiece(Board board, int x, int y, Piece currentPlayer) {
        //首先检查该位置是否有对应的棋子
        if (!board.isEmpty(x, y)) {
            return new MoveResult(false, "该位置已有棋子，落子无效。", GameStatus.ONGOING);
        }
        //如果该位置空，可以落子，用set进行落子
        board.set(x, y, currentPlayer);
        //当前玩家进行落子，只可能当前玩家获胜
        //判断当前玩家是否取胜
        if (checkWin(board, x, y, currentPlayer)) {
            //如果当前玩家是黑色，则状态为黑棋胜，否则为白旗胜
            GameStatus status = currentPlayer == Piece.BLACK ? GameStatus.BLACK_WIN : GameStatus.WHITE_WIN;
            //返回落子后的消息
            return new MoveResult(true, "玩家 " + currentPlayer + " 获胜！（五子连线）", status);
        }
        //如果当前玩家还没有取胜，检查是否还可以落子
        if (isBoardFull(board)) {
            //如果棋盘已满，则返回平局
            return new MoveResult(true, "棋盘已满，平局。", GameStatus.DRAW);
        }
        //如果既没有获胜也没有落满，那么游戏继续进行。
        return new MoveResult(true, "落子成功。", GameStatus.ONGOING);
    }


    @Override
    public MoveResult pass(Board board, Piece currentPlayer) {
        // 五子棋没有虚着
        return new MoveResult(false, "五子棋不支持虚着（pass）。", GameStatus.ONGOING);
    }

    //判断棋盘是否已经落满棋子
    private boolean isBoardFull(Board board) {
        //遍历检查每个位置是否都有棋子
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.get(i, j) == Piece.EMPTY) return false;
            }
        }
        return true;
    }

    //检查当前棋局，玩家player是否获得了胜利
    private boolean checkWin(Board board, int x, int y, Piece player) {
        // 四个方向：水平、竖直、两条对角线
        int[][] dirs = {
                {1, 0}, {0, 1}, {1, 1}, {1, -1}
        };
        //四个直线分别测试
        for (int[] d : dirs) {
            //沿着该直线的两个方向计算棋子总数，如果总数大于等于5，则胜利
            if (countContinuous(board, x, y, d[0], d[1], player) +
                    countContinuous(board, x, y, -d[0], -d[1], player) - 1 >= winCount) {
                return true;
            }
        }
        return false;
    }

    //沿着dx，dy方向计算一共有多少个相同状态棋子相连
    private int countContinuous(Board board, int x, int y, int dx, int dy, Piece player) {
        int size = board.getSize();
        int count = 0;
        int i = x, j = y;
        while (i >= 0 && i < size && j >= 0 && j < size && board.get(i, j) == player) {
            count++;
            i += dx;
            j += dy;
        }
        return count;
    }
}
