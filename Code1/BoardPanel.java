package boardgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//棋盘绘制和鼠标控制落子，中央棋盘逻辑
public class BoardPanel extends JPanel {

    //游戏引擎
    private final GameEngine engine;


    public BoardPanel(GameEngine engine) {
        this.engine = engine;
        setBackground(new Color(230, 200, 150)); // 棋盘木色

        // 监听鼠标点击事件，在棋盘上落子，将跟踪鼠标点击的坐标
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    //处理鼠标点击事件
    private void handleClick(int mouseX, int mouseY) {
        Board board = engine.getBoard();
        if (board == null) {
            JOptionPane.showMessageDialog(this, "请先开始一局游戏。");
            return;
        }
        if (engine.getStatus() != GameStatus.ONGOING) {
            JOptionPane.showMessageDialog(this, "对局已结束，请重新开始。");
            return;
        }

        int size = board.getSize();
        int w = getWidth();
        int h = getHeight();
        int cellSize = Math.min(w, h) / (size + 1);
        int marginX = (w - cellSize * (size - 1)) / 2;
        int marginY = (h - cellSize * (size - 1)) / 2;

        // 把点击位置映射到最近的交叉点（行/列）
        int col = Math.round((mouseX - marginX) / (float) cellSize);
        int row = Math.round((mouseY - marginY) / (float) cellSize);

        if (row < 0 || row >= size || col < 0 || col >= size) {
            return; // 点击在棋盘外，忽略
        }

//        // 注意：GameEngine.move 使用的是 1-based 坐标
//        engine.move(row + 1, col + 1);
//
//        //重新调用paintComponent方法，对整个局面进行绘制
//        repaint();
//
//        // 更新状态栏（向上找到 GameFrame）
//        Container parent = getParent();
//        while (parent != null && !(parent instanceof GameFrame)) {
//            parent = parent.getParent();
//        }
//        if (parent instanceof GameFrame) {
//            ((GameFrame) parent).updateStatus();
//        }
        engine.move(row + 1, col + 1);

// 根据最近一次操作结果弹窗 ↓↓↓
        if (!engine.wasLastActionSuccess()) {
            // 落子失败
            JOptionPane.showMessageDialog(
                    this,
                    engine.getLastActionMessage(),
                    "落子失败",
                    JOptionPane.WARNING_MESSAGE
            );
        } else if (engine.getStatus() != GameStatus.ONGOING) {
            // 对局结束
            JOptionPane.showMessageDialog(
                    this,
                    engine.getLastActionMessage() + "\n对局结束，状态：" + engine.getStatus(),
                    "对局结束",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        repaint();

// 更新状态栏
        Container parent = getParent();
        while (parent != null && !(parent instanceof GameFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof GameFrame) {
            ((GameFrame) parent).updateStatus();
        }

    }

    //在图形界面绘制棋盘和棋子
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Board board = engine.getBoard();
        if (board == null) {
            // 还没开始对局，画一个提示
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String msg = "请在上方选择游戏类型与棋盘大小，然后点击“开始游戏”。";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            //输出提示文字
            g.drawString(msg, (getWidth() - strWidth) / 2, getHeight() / 2);
            return;
        }

        int size = board.getSize();
        int w = getWidth();
        int h = getHeight();
        int cellSize = Math.min(w, h) / (size + 1);
        int marginX = (w - cellSize * (size - 1)) / 2;
        int marginY = (h - cellSize * (size - 1)) / 2;

        // === 画网格线 ===
        g.setColor(Color.BLACK);
        for (int i = 0; i < size; i++) {
            int y = marginY + i * cellSize;
            int x1 = marginX;
            int x2 = marginX + (size - 1) * cellSize;
            //画网格线
            g.drawLine(x1, y, x2, y);

            int x = marginX + i * cellSize;
            int y1 = marginY;
            int y2 = marginY + (size - 1) * cellSize;
            g.drawLine(x, y1, x, y2);
        }

        // === 画棋子 ===
        int stoneRadius = (int) (cellSize * 0.4);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Piece p = board.get(row, col);
                if (p == Piece.EMPTY) continue;

                int cx = marginX + col * cellSize;
                int cy = marginY + row * cellSize;

                //对黑/白棋进行绘制
                if (p == Piece.BLACK) {
                    g.setColor(Color.BLACK);
                } else if (p == Piece.WHITE) {
                    g.setColor(Color.WHITE);
                }
                g.fillOval(cx - stoneRadius, cy - stoneRadius,
                        stoneRadius * 2, stoneRadius * 2);

                g.setColor(Color.BLACK);
                g.drawOval(cx - stoneRadius, cy - stoneRadius,
                        stoneRadius * 2, stoneRadius * 2);
            }
        }
    }
}
