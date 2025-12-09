package boardgame;
//命令行版的游戏入口
public class Main {
    public static void main(String[] args) {
        // 获取 GameEngine 的唯一实例
        GameEngine engine = new GameEngine(); // 使用单例模式获取实例
        ConsoleUI ui = new ConsoleUI(engine);
        ui.run();
    }
}

