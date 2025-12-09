package boardgame;

import java.util.Arrays;
//棋盘类
public class Board implements Cloneable {
    private final int size;
    private final Piece[][] grid;
//初始化建立一个棋盘
    public Board(int size) {
        if (size < 8 || size > 19) {
            throw new IllegalArgumentException("Board size must be between 8 and 19.");
        }
        this.size = size;
        this.grid = new Piece[size][size];
        for (int i = 0; i < size; i++) {
            //初始化时全部为空棋子
            Arrays.fill(this.grid[i], Piece.EMPTY);
        }
    }

    public int getSize() {
        return size;
    }
 //获得某一个位置的棋子状态
    public Piece get(int x, int y) {
        checkRange(x, y);
        return grid[x][y];
    }
//落子
    public void set(int x, int y, Piece piece) {
        checkRange(x, y);
        grid[x][y] = piece;
    }

    public boolean isEmpty(int x, int y) {
        return get(x, y) == Piece.EMPTY;
    }
//检查位置是否合法
    private void checkRange(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            throw new IndexOutOfBoundsException("Position out of board");
        }
    }

    //棋盘克隆函数
    @Override
    public Board clone() {
        Board copy = new Board(size);
        for (int i = 0; i < size; i++) {
            System.arraycopy(this.grid[i], 0, copy.grid[i], 0, size);
        }
        return copy;
    }
//文本形式打印棋盘
    public void display(boolean showHint) {
        System.out.println();
        // 列坐标
        System.out.print("   ");
        for (int j = 0; j < size; j++) {
            System.out.printf("%2d", j + 1);
        }
        System.out.println();
        // 行
        for (int i = 0; i < size; i++) {
            System.out.printf("%2d ", i + 1);
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
        //是否显示辅助命令
        if (showHint) {
            System.out.println("\n提示：用命令控制游戏，例如：");
            System.out.println("  start gomoku 15   启动15路五子棋");
            System.out.println("  start go 19       启动19路围棋");
            System.out.println("  move x y          在(x,y)落子，坐标从1开始");
            System.out.println("  pass              围棋虚着");
            System.out.println("  undo              悔棋一步");
            System.out.println("  resign            投子认负");
            System.out.println("  save filename.txt 保存当前局面");
            System.out.println("  load filename.txt 读取局面");
            System.out.println("  helpon / helpoff  显示/隐藏提示");
            System.out.println("  restart           重新开始当前类型游戏");
            System.out.println("  quit              退出程序");
        }
    }
}
