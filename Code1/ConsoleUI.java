package boardgame;

import java.util.Locale;
import java.util.Scanner;
//命令行交互处理类，是游戏与用户交互的接口
public class ConsoleUI {
    //维护一个游戏引擎
    private final GameEngine engine;

    public ConsoleUI(GameEngine engine) {
        this.engine = engine;
    }

    //开启游戏控制
    public void run() {
        System.out.println("欢迎来到棋类对战平台（五子棋 & 围棋）- 命令行版。");
        System.out.println("使用 helpon / helpoff 控制操作提示。");
        System.out.println("用 start 命令开始新对局，例如：start gomoku 15  或 start go 19");

        //对命令行的用户输入进行读取和处理
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\n> ");
            String line = scanner.nextLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String cmd = parts[0].toLowerCase(Locale.ROOT);

            try {
                switch (cmd) {
                    case "quit":
                    case "exit":
                        System.out.println("退出程序。");
                        return;
                    case "start":
                        handleStart(parts);
                        break;
                    case "restart":
                        engine.restart();
                        break;
                    case "move":
                        handleMove(parts);
                        break;
                    case "pass":
                        engine.pass();
                        break;
                    case "undo":
                        engine.undo();
                        break;
                    case "resign":
                        engine.resign();
                        break;
                    case "save":
                        handleSave(parts);
                        break;
                    case "load":
                        handleLoad(parts);
                        break;
                    case "helpon":
                        engine.setShowHint(true);
                        break;
                    case "helpoff":
                        engine.setShowHint(false);
                        break;
                    default:
                        System.out.println("未知指令：" + cmd + "。请使用 start/move/pass/undo/resign/save/load/helpon/helpoff/restart/quit。");
                }
            } catch (NumberFormatException e) {
                System.out.println("无效的数字输入，请检查你的命令格式。");
            } catch (IllegalArgumentException e) {
                System.out.println("非法指令或输入，请检查你的命令格式。");
            } catch (Exception e) {
                System.out.println("发生错误：" + e.getMessage());
            }
        }
    }

    //开始游戏
    private void handleStart(String[] parts) {
        if (parts.length < 3) {
            System.out.println("用法：start gomoku 15  或  start go 19");
            return;
        }
        //游戏类型
        String typeStr = parts[1].toLowerCase(Locale.ROOT);
        //棋盘大小（不同类型游戏，棋盘大小不同）
        String sizeStr = parts[2];
        int size;
        try {
            size = Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            System.out.println("棋盘大小必须为整数。");
            return;
        }
        if (size < 8 || size > 19) {
            System.out.println("棋盘大小必须在 8~19 之间。");
            return;
        }

        GameType type;
        if (typeStr.equals("gomoku") || typeStr.equals("wuziqi")) {
            type = GameType.GOMOKU;
        } else if (typeStr.equals("go") || typeStr.equals("weiqi")) {
            type = GameType.GO;
        } else {
            System.out.println("未知游戏类型：" + typeStr + "。支持：gomoku / go");
            return;
        }
        //开始游戏
        engine.startGame(type, size);
    }

    //处理落子
    private void handleMove(String[] parts) {
        if (parts.length < 3) {
            System.out.println("用法：move x y   （坐标从 1 开始）");
            return;
        }
        try {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            //在（x，y）处落子
            engine.move(x, y);
        } catch (NumberFormatException e) {
            System.out.println("坐标必须为整数。");
        }
    }

    //将当前游戏局面存储到文件中
    private void handleSave(String[] parts) {
        if (parts.length < 2) {
            System.out.println("用法：save filename.txt");
            return;
        }
        engine.saveToFile(parts[1]);
    }

    //从对应文件中加载游戏局面
    private void handleLoad(String[] parts) {
        if (parts.length < 2) {
            System.out.println("用法：load filename.txt");
            return;
        }
        engine.loadFromFile(parts[1]);
    }
}
