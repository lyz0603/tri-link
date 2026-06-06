# 三连棋 (TriLink)

6×6 棋盘 · 双方轮流落子 · 满盘后统计三连总数决定胜负

## 游戏规则

- **棋盘**：6×6 = 36 格
- **对局**：玩家 vs AI，轮流落子直到下满
- **三连**：横、竖、对角线方向，连续 3 颗同色棋子即构成三连
  - 3 连 = 1 分 · 4 连 = 2 分 · 5 连 = 3 分 · 6 连 = 4 分
- **统计**：棋盘上所有长度 ≥ 3 的线段共 32 条（6横 + 6竖 + 10主对角线 + 10反对角线）
- **胜负**：满盘后统计全局三连总数，多者胜

## 技术方案

| 项目 | 说明 |
|------|------|
| 语言 | Kotlin |
| UI | Jetpack Compose (Material Design 3) |
| 架构 | MVVM (ViewModel + StateFlow) |
| AI | 迭代加深 Alpha-Beta 剪枝 + 根节点协程并行 |
| 配色 | Android 12+ Dynamic Color 动态配色 |
| 最低 SDK | Android 8.0 (API 26) |

## 编译 & 运行

需要 Android Studio 或 Gradle + Android SDK。

```bash
# 调试编译
gradle assembleDebug

# 安装到设备/模拟器
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 项目结构

```
app/src/main/java/com/trilink/game/
├── engine/
│   ├── Board.kt          # 棋盘常量、32 条线段定义
│   ├── GameEngine.kt     # 三连计数、启发评估、终局判断
│   └── AISearch.kt       # Alpha-Beta 迭代加深搜索（协程并行）
├── viewmodel/
│   └── GameViewModel.kt  # MVVM 状态管理
├── ui/
│   ├── theme/            # Material 3 主题 + 动态配色
│   ├── screens/
│   │   ├── SetupScreen.kt   # 选棋界面
│   │   ├── GameScreen.kt    # 对局界面
│   │   └── RulesScreen.kt   # 规则与算法说明
│   └── components/
│       ├── BoardGrid.kt     # 6×6 棋盘网格
│       └── StatusBar.kt     # 状态栏
└── MainActivity.kt
```

## 许可证

MIT License — 详见 [LICENSE](LICENSE)
