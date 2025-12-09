package boardgame;

import java.util.*;
//具体策略类-围棋规则
public class GoRule implements RuleStrategy {

    // 连续 pass 达 2 时触发终局
    private int consecutivePasses = 0;

    @Override
    public MoveResult placePiece(Board board, int x, int y, Piece currentPlayer) {
        int size = board.getSize();
        //判断落子点是否是空位
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return new MoveResult(false, "落子超出棋盘范围。", GameStatus.ONGOING);
        }
        if (!board.isEmpty(x, y)) {
            return new MoveResult(false, "该位置已有棋子。", GameStatus.ONGOING);
        }

        // 一旦有真正棋子落下，重置 pass 计数
        consecutivePasses = 0;

        // 先落子
        board.set(x, y, currentPlayer);

        // 提取周围对方无气的棋链
        Piece opponent = currentPlayer.opposite();
        //上下左右四个相邻位置
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        //建立集合
        Set<String> removedStones = new HashSet<>();

        //沿着四条直线的方向进行判断
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];

            if (nx < 0 || nx >= size || ny < 0 || ny >= size) continue;
            //如果是对方棋子，可以尝试进行判断，“提子”
            if (board.get(nx, ny) == opponent) {
                if (!hasLiberty(board, nx, ny, opponent, new boolean[size][size])) {
                    // 没有气，该子可以被吃掉
                    removedStones.addAll(removeGroup(board, nx, ny, opponent));
                }
            }
        }

        // 如果当前位置是一个无气位置
        if (!hasLiberty(board, x, y, currentPlayer, new boolean[size][size])) {
            //如果这一步不能吃掉对方的棋子
            if (removedStones.isEmpty()) {
                // 自杀，不允许
                board.set(x, y, Piece.EMPTY); // 撤销
                return new MoveResult(false, "自杀手（无气且未提子），落子无效。", GameStatus.ONGOING);
            }
        }

        // 简单规则：对局不会因单步结束，终局通过 pass 控制
        return new MoveResult(true, "落子成功。", GameStatus.ONGOING);
    }

    //允许玩家在这一回合不落子，
    //双方如果在同一回合都不落子之后，则游戏结束
    @Override
    public MoveResult pass(Board board, Piece currentPlayer) {
        consecutivePasses++;
        //连续两次后，游戏结束
        if (consecutivePasses >= 2) {
            // 终局：简单规则——谁在棋盘上的子更多谁赢
            int blackCount = 0, whiteCount = 0;
            int size = board.getSize();
            //终局后，统计两种颜色的棋子数量
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Piece p = board.get(i, j);
                    if (p == Piece.BLACK) blackCount++;
                    else if (p == Piece.WHITE) whiteCount++;
                }
            }
            //如果黑色棋子多，黑色胜利
            if (blackCount > whiteCount) {
                return new MoveResult(true,
                        "双方连续虚着，终局。盘上黑棋多，黑胜。", GameStatus.BLACK_WIN);
            } else if (whiteCount > blackCount) {
                return new MoveResult(true,
                        "双方连续虚着，终局。盘上白棋多，白胜。", GameStatus.WHITE_WIN);
            } else {
                return new MoveResult(true, "双方连续虚着，终局。子数相同，平局。", GameStatus.DRAW);
            }
        } else {
            //如果还没有达到两次，允许玩家选择虚着
            return new MoveResult(true, "玩家 " + currentPlayer + " 选择虚着。", GameStatus.ONGOING);
        }
    }


    //判断棋子是否是color颜色，是否处于有气状态，有气则返回true
    private boolean hasLiberty(Board board, int x, int y, Piece color, boolean[][] visited) {
        int size = board.getSize();
        if (x < 0 || x >= size || y < 0 || y >= size) return false;

        if (visited[x][y]) return false;
        //首先默认有气
        visited[x][y] = true;

        Piece p = board.get(x, y);
        //首先该棋子不是我们希望判断的颜色
        if (p != color) return false;

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx < 0 || nx >= size || ny < 0 || ny >= size) continue;
            Piece np = board.get(nx, ny);
            if (np == Piece.EMPTY) {
                return true;
            }
        }
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx < 0 || nx >= size || ny < 0 || ny >= size) continue;
            if (!visited[nx][ny] && board.get(nx, ny) == color) {
                if (hasLiberty(board, nx, ny, color, visited)) return true;
            }
        }
        return false;
    }

    //提子操作
    private Set<String> removeGroup(Board board, int x, int y, Piece color) {
        int size = board.getSize();
        Set<String> removed = new HashSet<>();
        Deque<int[]> stack = new ArrayDeque<>();
        boolean[][] visited = new boolean[size][size];//初始化为true
        stack.push(new int[]{x, y});
        visited[x][y] = true;

        while (!stack.isEmpty()) {//
            int[] cur = stack.pop();
            int cx = cur[0], cy = cur[1];
            if (board.get(cx, cy) == color) {//如果确实是对方颜色
                board.set(cx, cy, Piece.EMPTY);//该子提走
                removed.add(cx + "," + cy);
                int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                for (int[] d : dirs) {
                    int nx = cx + d[0];
                    int ny = cy + d[1];
                    if (nx < 0 || nx >= size || ny < 0 || ny >= size) continue;

                    if (!visited[nx][ny] && board.get(nx, ny) == color) {
                        visited[nx][ny] = true;
                        stack.push(new int[]{nx, ny});
                    }
                }
            }
        }
        return removed;
    }
}
