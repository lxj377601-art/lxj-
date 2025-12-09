package boardgame;

import javax.swing.*;
import java.awt.*;
import java.io.File;

//窗口管理，整个游戏窗口的布局和控制逻辑
public class GameFrame extends JFrame {

    //维护一个游戏引擎
    private final GameEngine engine;
    //维护一个画板
    private final BoardPanel boardPanel;
    //标签，控制当前执棋手
    private final JLabel statusLabel;
    //下拉框，选择游戏类型
    private final JComboBox<String> gameTypeBox;
    //数字输入框
    private final JSpinner sizeSpinner;

    public GameFrame(GameEngine engine) {
        super("棋类对战平台");
        this.engine = engine;

        // 窗口基础设置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null); // 居中

        // 布局：上面控制区 + 中间棋盘 + 底部状态栏
        setLayout(new BorderLayout());

        // === 中间棋盘面板 ===
        boardPanel = new BoardPanel(engine);
        add(boardPanel, BorderLayout.CENTER);

        // === 上方控制条 ===
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        //下拉窗口选择游戏类型
        gameTypeBox = new JComboBox<>(new String[]{"五子棋（Gomoku）", "围棋（Go）"});
        //允许选择棋盘大小
        sizeSpinner = new JSpinner(new SpinnerNumberModel(15, 8, 19, 1));
        JButton startButton = new JButton("开始游戏");
        JButton restartButton = new JButton("重新开始");
        JButton undoButton = new JButton("悔棋");
        JButton passButton = new JButton("虚着（围棋）");
        JButton resignButton = new JButton("投子认负");
        JButton saveButton = new JButton("保存局面");
        JButton loadButton = new JButton("读取局面");

        topPanel.add(new JLabel("游戏类型:"));
        topPanel.add(gameTypeBox);
        topPanel.add(new JLabel("棋盘大小:"));
        topPanel.add(sizeSpinner);
        topPanel.add(startButton);
        topPanel.add(restartButton);
        topPanel.add(undoButton);
        topPanel.add(passButton);
        topPanel.add(resignButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);

        add(topPanel, BorderLayout.NORTH);

        // === 底部状态栏 ===
        statusLabel = new JLabel("请先在上方选择游戏并点击“开始游戏”。");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(statusLabel, BorderLayout.SOUTH);

        // === 事件绑定 ===
        //为每个按钮添加点击事件监听器，监听按钮的点击事件，当用户点击按钮时相应的方法会被调用
//        startButton.addActionListener(e -> handleStart());
        startButton.addActionListener(e -> {
            String typeStr = (String) gameTypeBox.getSelectedItem();
            int size = (Integer) sizeSpinner.getValue();

            GameType type;
            if (typeStr.startsWith("五子棋")) {
                type = GameType.GOMOKU;
            } else {
                type = GameType.GO;
            }

            engine.startGame(type, size);  // 调用 GameEngine 的 startGame 方法
            updateStatus();  // 更新状态
            boardPanel.repaint();  // 刷新棋盘显示

            // 在 GameFrame 中处理弹窗显示游戏开始的信息
            if (engine.wasLastActionSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "游戏开始",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

//        restartButton.addActionListener(e -> {
//            engine.restart();
//            updateStatus();
//            boardPanel.repaint();
//        });
        //重新开始按钮，增加弹窗机制
        restartButton.addActionListener(e -> {
            engine.restart();  // 调用 GameEngine 的 restart 方法
            updateStatus();  // 更新状态
            boardPanel.repaint();  // 刷新棋盘显示

            // 在 GameFrame 中处理弹窗显示重新开始的信息
            if (engine.wasLastActionSuccess()) {
                // 游戏重新开始成功，弹出提示框
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "游戏重新开始",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // 失败情况弹窗
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "操作失败",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        //悔棋按钮
//        undoButton.addActionListener(e -> {
//            engine.undo();
//            updateStatus();
//            boardPanel.repaint();
//        });
        //悔棋按钮
        undoButton.addActionListener(e -> {
            engine.undo();

            // 根据悔棋操作结果弹窗显示
            if (!engine.wasLastActionSuccess()) {
                // 如果悔棋失败，弹出失败提示框
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "操作失败",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                // 如果悔棋成功，弹出成功提示框，显示谁进行了悔棋操作
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "悔棋成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            updateStatus();  // 更新状态
            boardPanel.repaint();  // 刷新棋盘显示
        });

//        passButton.addActionListener(e -> {
//            engine.pass();
//            updateStatus();
//            boardPanel.repaint();
//        });
//        resignButton.addActionListener(e -> {
//            engine.resign();
//            updateStatus();
//            boardPanel.repaint();
//        });
        //虚着按钮
        passButton.addActionListener(e -> {
            engine.pass();

            if (!engine.wasLastActionSuccess()) {
                // 虚着失败（例如在五子棋里pass，或者对局已结束），进行弹窗
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "操作失败",
                        JOptionPane.WARNING_MESSAGE
                );
            } else if (engine.getStatus() != GameStatus.ONGOING) {
                // 虚着后触发终局（双方连续虚着）
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage() + "\n对局结束，状态：" + engine.getStatus(),
                        "对局结束",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // 虚着成功但游戏未结束
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "虚着成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            updateStatus();
            boardPanel.repaint();
        });

        //投子认负按钮
        resignButton.addActionListener(e -> {
            engine.resign();
            if (engine.getStatus() != GameStatus.ONGOING && engine.wasLastActionSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage() + "\n对局结束，状态：" + engine.getStatus(),
                        "对局结束",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else if (!engine.wasLastActionSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "操作失败",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            updateStatus();
            boardPanel.repaint();
        });
        //保存按钮
        saveButton.addActionListener(e -> handleSave());
        //加载按钮
        loadButton.addActionListener(e -> {
            handleLoad();
            updateStatus();
            boardPanel.repaint();
        });

        // 初始状态
        updateStatus();
    }

    //开始游戏
    private void handleStart() {
        String typeStr = (String) gameTypeBox.getSelectedItem();
        int size = (Integer) sizeSpinner.getValue();

        GameType type;
        if (typeStr.startsWith("五子棋")) {
            type = GameType.GOMOKU;
        } else {
            type = GameType.GO;
        }

        engine.startGame(type, size);
        updateStatus();
        boardPanel.repaint();
    }

    //    //保存按钮触发
//    private void handleSave() {
//        if (engine.getBoard() == null) {
//            JOptionPane.showMessageDialog(this, "当前没有进行中的对局，无法保存。");
//            return;
//        }
//        JFileChooser chooser = new JFileChooser();
//        chooser.setDialogTitle("保存局面");
//        int result = chooser.showSaveDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File file = chooser.getSelectedFile();
//            engine.saveToFile(file.getAbsolutePath());
//        }
//    }
    //将棋局进行存储
    private void handleSave() {
        if (engine.getBoard() == null) {
            JOptionPane.showMessageDialog(this, "当前没有进行中的对局，无法保存。");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("保存局面");
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            engine.saveToFile(file.getAbsolutePath());

            if (engine.wasLastActionSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "保存成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "保存失败",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }


    //    //从文件中加载游戏
//    private void handleLoad() {
//        JFileChooser chooser = new JFileChooser();
//        chooser.setDialogTitle("读取局面");
//        int result = chooser.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File file = chooser.getSelectedFile();
//            engine.loadFromFile(file.getAbsolutePath());
//        }
//    }
    //从文件中加载时
    private void handleLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("读取局面");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            //调用加载函数
            engine.loadFromFile(file.getAbsolutePath());

            if (engine.wasLastActionSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "读取成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        engine.getLastActionMessage(),
                        "读取失败",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }


    //更新状态
    public void updateStatus() {
        Board board = engine.getBoard();
        GameStatus s = engine.getStatus();
        GameType t = engine.getGameType();
        Piece p = engine.getCurrentPlayer();

        if (board == null || t == null) {
            statusLabel.setText("尚未开始对局。请选择类型与大小后点击“开始游戏”。");
            return;
        }

        String typeText = (t == GameType.GOMOKU) ? "五子棋" : "围棋";
        String statusText;
        switch (s) {
            case ONGOING:
                statusText = "进行中";
                break;
            case BLACK_WIN:
                statusText = "黑方获胜";
                break;
            case WHITE_WIN:
                statusText = "白方获胜";
                break;
            case DRAW:
                statusText = "平局";
                break;
            default:
                statusText = s.toString();
        }

        String turnText = (s == GameStatus.ONGOING)
                ? ("当前轮到：" + (p == Piece.BLACK ? "黑方" : "白方"))
                : "对局已结束";

        statusLabel.setText("游戏类型：" + typeText +
                "    棋盘：" + board.getSize() + "x" + board.getSize() +
                "    状态：" + statusText +
                "    " + turnText);
    }
}
