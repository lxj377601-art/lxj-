package boardgame;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class GameEngine {
    private Board board;
    private RuleStrategy rule;
    private GameType gameType;
    private Piece currentPlayer;
    private GameStatus status;
    private boolean showHint = true;
    private final Deque<GameMemento> history = new ArrayDeque<>();
    private int undoCount = 0;  // 用来记录悔棋的次数
    private final int MAX_UNDO_COUNT = 3;  // 设置最大悔棋次数为 3 次


    //    public void startGame(GameType type, int size) {
//        this.gameType = type;
//        this.board = new Board(size);
//        this.currentPlayer = Piece.BLACK; // 默认黑先
//        this.status = GameStatus.ONGOING;
//        this.history.clear();
//
//        if (type == GameType.GOMOKU) {
//            this.rule = new GomokuRule();
//        } else {
//            this.rule = new GoRule();
//        }
//        saveToHistory(); // 初始状态
//        System.out.println("新对局开始： " + type + "，棋盘大小 " + size + "x" + size + "。黑先。");
//        board.display(showHint);
//    }
    public void startGame(GameType type, int size) {
        this.gameType = type;
        this.board = new Board(size);
        this.currentPlayer = Piece.BLACK; // 默认黑先
        this.status = GameStatus.ONGOING;
        this.history.clear();
        this.undoCount = 0;

        if (type == GameType.GOMOKU) {
            this.rule = new GomokuRule();
        } else {
            this.rule = new GoRule();
        }
        saveToHistory(); // 初始状态

        // 设置游戏开始的信息
        String gameTypeStr = (gameType == GameType.GOMOKU) ? "五子棋" : "围棋";
        lastActionMessage = "游戏开始！\n类型: " + gameTypeStr + "\n棋盘大小: " + size + "x" + size + "\n当前玩家: 黑方（先手）";
        lastActionSuccess = true;  // 游戏成功开始

        System.out.println("新对局开始： " + gameTypeStr + "，棋盘大小 " + size + "x" + size + "。黑先。");
        board.display(showHint);
    }


    private void saveToHistory() {
        if (board != null) {
            history.push(new GameMemento(board.clone(), currentPlayer, status));
        }
    }

    //    public void restart() {
//        if (board == null || gameType == null) {
//            System.out.println("当前没有进行中的对局，无法 restart。请先 start。");
//            return;
//        }
//        startGame(gameType, board.getSize());
//    }
    public void restart() {
        if (board == null || gameType == null) {
            lastActionSuccess = false;
            lastActionMessage = "当前没有进行中的对局，无法重新开始。请先开始游戏。";
            return;
        }
        // 重新开始游戏
        startGame(gameType, board.getSize());  // 调用 startGame 方法重新开始
        lastActionMessage = "游戏已重新开始！\n类型: " + gameType + "\n棋盘大小: " + board.getSize() + "x" + board.getSize() + "\n当前玩家: 黑方（先手）";
        lastActionSuccess = true;  // 重新开始成功
    }


    //    public void move(int x1Based, int y1Based) {
//        if (!ensureGameRunning()) return;
//        if (status != GameStatus.ONGOING) {
//            System.out.println("对局已结束，请先重新开始（restart / start）。");
//            return;
//        }
//        int x = x1Based - 1;
//        int y = y1Based - 1;
//
//        saveToHistory();
//        RuleStrategy.MoveResult result = rule.placePiece(board, x, y, currentPlayer);
//        if (!result.isSuccess()) {
//            // 恢复保存前的状态
//            history.pop();
//            System.out.println("落子失败：" + result.getMessage());
//            return;
//        }
//
//        this.status = result.getStatus();
//        System.out.println(result.getMessage());
//
//        if (status == GameStatus.ONGOING) {
//            currentPlayer = currentPlayer.opposite();
//        } else {
//            board.display(showHint);
//            System.out.println("对局结束，状态：" + status);
//        }
//        board.display(showHint);
//    }
    //落子操作
    public void move(int x1Based, int y1Based) {
        if (!ensureGameRunning()) return;
        if (status != GameStatus.ONGOING) {
            System.out.println("对局已结束，请先重新开始（restart / start）。");
            lastActionSuccess = false;
            lastActionMessage = "对局已结束，不能继续落子。";
            return;
        }
        int x = x1Based - 1;
        int y = y1Based - 1;
        //下棋前先进行状态的存储
        saveToHistory();
        //调用具体的落子函数
        RuleStrategy.MoveResult result = rule.placePiece(board, x, y, currentPlayer);

        // 记录最近一次操作结果
        lastActionSuccess = result.isSuccess();
        lastActionMessage = result.getMessage();

        if (!result.isSuccess()) {
            // 恢复保存前的状态
            history.pop();
            System.out.println("落子失败：" + result.getMessage());
            // 注意：status 不变
            return;
        }

        this.status = result.getStatus();
        System.out.println(result.getMessage());

        if (status == GameStatus.ONGOING) {
            currentPlayer = currentPlayer.opposite();
        } else {
            board.display(showHint);
            System.out.println("对局结束，状态：" + status);
        }
        board.display(showHint);
    }


    //    public void pass() {
//        if (!ensureGameRunning()) return;
//        if (gameType != GameType.GO) {
//            System.out.println("只有围棋支持虚着（pass）。");
//            return;
//        }
//        if (status != GameStatus.ONGOING) {
//            System.out.println("对局已结束，不能虚着。");
//            return;
//        }
//        saveToHistory();
//        RuleStrategy.MoveResult result = rule.pass(board, currentPlayer);
//        System.out.println(result.getMessage());
//        this.status = result.getStatus();
//        if (status == GameStatus.ONGOING) {
//            currentPlayer = currentPlayer.opposite();
//            board.display(showHint);
//        } else {
//            board.display(showHint);
//            System.out.println("对局结束，状态：" + status);
//        }
//    }
    //围棋的“虚着”操作
    public void pass() {
        if (!ensureGameRunning()) return;
        if (gameType != GameType.GO) {
            String msg = "只有围棋支持虚着（pass）。";
            System.out.println(msg);
            lastActionSuccess = false;
            lastActionMessage = msg;
            return;
        }
        if (status != GameStatus.ONGOING) {
            String msg = "对局已结束，不能虚着。";
            System.out.println(msg);
            lastActionSuccess = false;
            lastActionMessage = msg;
            return;
        }
        saveToHistory();
        //调用具体的虚着函数
        RuleStrategy.MoveResult result = rule.pass(board, currentPlayer);

        lastActionSuccess = result.isSuccess();
        lastActionMessage = result.getMessage();

        System.out.println(result.getMessage());
        this.status = result.getStatus();
        if (status == GameStatus.ONGOING) {
            currentPlayer = currentPlayer.opposite();
            board.display(showHint);
        } else {
            board.display(showHint);
            System.out.println("对局结束，状态：" + status);
        }
    }


    //    public void undo() {
//        if (!ensureGameRunning()) return;
//        if (history.size() <= 1) {
//            System.out.println("无棋可悔。");
//            return;
//        }
//        // 丢弃当前状态
//        history.pop();
//        // 取上一状态
//        GameMemento prev = history.peek();
//        if (prev != null) {
//            this.board = prev.getBoardSnapshot().clone();
//            this.currentPlayer = prev.getCurrentPlayer();
//            this.status = prev.getStatus();
//            System.out.println("悔棋成功，轮到玩家 " + currentPlayer + " 行棋。");
//            board.display(showHint);
//        }
//    }
    public void undo() {
        if (!ensureGameRunning()) return;
        if (history.size() <= 1) {
            // 无棋可悔
            lastActionSuccess = false;
            lastActionMessage = "无棋可悔。";
            return;
        }

        // 增加悔棋计数
        undoCount++;

        // 判断是否超过最大悔棋次数
        if (undoCount > MAX_UNDO_COUNT) {
            String msg = "您已经悔棋超过 " + MAX_UNDO_COUNT + " 次！请注意游戏的公平性。";
            lastActionMessage = msg;
            lastActionSuccess = false;
            return;
        }

        // 丢弃当前状态
        history.pop();
        // 获取上一状态
        GameMemento prev = history.peek();
        if (prev != null) {
            this.board = prev.getBoardSnapshot().clone();
            this.currentPlayer = prev.getCurrentPlayer();
            this.status = prev.getStatus();

            String msg = currentPlayer + " 悔棋成功，轮到 " + currentPlayer.opposite() + " 行棋。";
            lastActionMessage = msg;
            lastActionSuccess = true;

            // 打印消息
            System.out.println(msg);
        }
    }


    //    public void resign() {
//        if (!ensureGameRunning()) return;
//        if (status != GameStatus.ONGOING) {
//            System.out.println("对局已结束，无需认负。");
//            return;
//        }
//        if (currentPlayer == Piece.BLACK) {
//            status = GameStatus.WHITE_WIN;
//            System.out.println("黑方投子认负，白胜。");
//        } else {
//            status = GameStatus.BLACK_WIN;
//            System.out.println("白方投子认负，黑胜。");
//        }
//        board.display(showHint);
//    }
    //投子认负
    public void resign() {
        if (!ensureGameRunning()) return;
        if (status != GameStatus.ONGOING) {
            String msg = "对局已结束，无需认负。";
            System.out.println(msg);
            lastActionSuccess = false;
            lastActionMessage = msg;
            return;
        }
        //投子认输后，要进行弹窗
        if (currentPlayer == Piece.BLACK) {
            status = GameStatus.WHITE_WIN;
            lastActionMessage = "黑方投子认负，白胜。";
        } else {
            status = GameStatus.BLACK_WIN;
            lastActionMessage = "白方投子认负，黑胜。";
        }
        lastActionSuccess = true;
        System.out.println(lastActionMessage);
        board.display(showHint);
    }


    //    public void saveToFile(String filename) {
//        if (!ensureGameRunning()) return;
//        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
//            out.println(gameType);
//            out.println(board.getSize());
//            out.println(currentPlayer);
//            out.println(status);
//            int size = board.getSize();
//            for (int i = 0; i < size; i++) {
//                StringBuilder sb = new StringBuilder();
//                for (int j = 0; j < size; j++) {
//                    sb.append(board.get(i, j).getSymbol());
//                }
//                out.println(sb.toString());
//            }
//            System.out.println("已将当前局面保存到文件：" + filename);
//        } catch (IOException e) {
//            System.out.println("保存失败：" + e.getMessage());
//        }
//    }
    //将当前棋局存储到文件中
    public void saveToFile(String filename) {
        if (!ensureGameRunning()) return;
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println(gameType);
            out.println(board.getSize());
            out.println(currentPlayer);
            out.println(status);
            int size = board.getSize();
            for (int i = 0; i < size; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    sb.append(board.get(i, j).getSymbol());
                }
                out.println(sb.toString());
            }
            String msg = "已将当前局面保存到文件：" + filename;
            System.out.println(msg);
            lastActionSuccess = true;
            lastActionMessage = msg;
            //一旦存储出现问题，进行报错处理
        } catch (IOException e) {
            String msg = "保存失败：" + e.getMessage();
            System.out.println(msg);
            lastActionSuccess = false;
            lastActionMessage = msg;
        }
    }


    //    public void loadFromFile(String filename) {
//        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
//            String typeLine = br.readLine();
//            String sizeLine = br.readLine();
//            String currentPlayerLine = br.readLine();
//            String statusLine = br.readLine();
//            if (typeLine == null || sizeLine == null || currentPlayerLine == null || statusLine == null) {
//                System.out.println("存档文件格式不合法。");
//                return;
//            }
//            GameType type = GameType.valueOf(typeLine.trim());
//            int size = Integer.parseInt(sizeLine.trim());
//            Piece cur = Piece.valueOf(currentPlayerLine.trim());
//            GameStatus stat = GameStatus.valueOf(statusLine.trim());
//
//            Board newBoard = new Board(size);
//            for (int i = 0; i < size; i++) {
//                String row = br.readLine();
//                if (row == null || row.length() < size) {
//                    System.out.println("存档文件数据不完整。");
//                    return;
//                }
//                for (int j = 0; j < size; j++) {
//                    char c = row.charAt(j);
//                    Piece p;
//                    if (c == 'B') p = Piece.BLACK;
//                    else if (c == 'W') p = Piece.WHITE;
//                    else p = Piece.EMPTY;
//                    newBoard.set(i, j, p);
//                }
//            }
//
//            this.gameType = type;
//            if (type == GameType.GOMOKU) {
//                this.rule = new GomokuRule();
//            } else {
//                this.rule = new GoRule();
//            }
//            this.board = newBoard;
//            this.currentPlayer = cur;
//            this.status = stat;
//            this.history.clear();
//            saveToHistory();
//
//            System.out.println("已从文件读取局面：" + filename);
//            board.display(showHint);
//        } catch (IOException | IllegalArgumentException e) {
//            System.out.println("读取存档失败：" + e.getMessage());
//        }
//    }
    //从文件中读取存储的棋局状态
    public void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String typeLine = br.readLine();
            String sizeLine = br.readLine();
            String currentPlayerLine = br.readLine();
            String statusLine = br.readLine();
            //如果读取文件出先问题，设置系统动作未完成
            if (typeLine == null || sizeLine == null || currentPlayerLine == null || statusLine == null) {
                String msg = "存档文件格式不合法。";
                System.out.println(msg);
                lastActionSuccess = false;
                lastActionMessage = msg;
                return;
            }
            GameType type = GameType.valueOf(typeLine.trim());
            int size = Integer.parseInt(sizeLine.trim());
            Piece cur = Piece.valueOf(currentPlayerLine.trim());
            GameStatus stat = GameStatus.valueOf(statusLine.trim());

            Board newBoard = new Board(size);
            for (int i = 0; i < size; i++) {
                String row = br.readLine();
                if (row == null || row.length() < size) {
                    String msg = "存档文件数据不完整。";
                    System.out.println(msg);
                    lastActionSuccess = false;
                    lastActionMessage = msg;
                    return;
                }
                for (int j = 0; j < size; j++) {
                    char c = row.charAt(j);
                    Piece p;
                    if (c == 'B') p = Piece.BLACK;
                    else if (c == 'W') p = Piece.WHITE;
                    else p = Piece.EMPTY;
                    newBoard.set(i, j, p);
                }
            }

            this.gameType = type;
            if (type == GameType.GOMOKU) {
                this.rule = new GomokuRule();
            } else {
                this.rule = new GoRule();
            }
            this.board = newBoard;
            this.currentPlayer = cur;
            this.status = stat;
            this.history.clear();
            saveToHistory();

            String msg = "已从文件读取局面：" + filename;
            System.out.println(msg);
            lastActionSuccess = true;
            lastActionMessage = msg;

            board.display(showHint);
        } catch (IOException | IllegalArgumentException e) {
            String msg = "读取存档失败：" + e.getMessage();
            System.out.println(msg);
            lastActionSuccess = false;
            lastActionMessage = msg;
        }
    }


    public void setShowHint(boolean showHint) {
        this.showHint = showHint;
        System.out.println("操作提示已 " + (showHint ? "开启" : "关闭") + "。");
        if (board != null) {
            board.display(showHint);
        }
    }

    private boolean ensureGameRunning() {
        if (board == null) {
            System.out.println("当前没有进行中的对局，请先使用 start 命令开始游戏。");
            return false;
        }
        return true;
    }

    // ===== GUI 需要的一些 getter =====

    public Board getBoard() {
        return board;
    }

    public Piece getCurrentPlayer() {
        return currentPlayer;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameType getGameType() {
        return gameType;
    }

    //————————————————————————————————————————————————————————————————————————
    // 最近一次操作（落子 / pass / resign）的结果
    private String lastActionMessage = "";
    private boolean lastActionSuccess = true;

    // 提供给 GUI 查询
    public String getLastActionMessage() {
        return lastActionMessage;
    }

    public boolean wasLastActionSuccess() {
        return lastActionSuccess;
    }

}
