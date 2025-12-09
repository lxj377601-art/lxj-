package boardgame;

import javax.swing.*;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameEngine engine = new GameEngine();  // 创建新的 GameEngine 实例
            GameFrame frame = new GameFrame(engine);
            frame.setVisible(true);
        });
    }
}
