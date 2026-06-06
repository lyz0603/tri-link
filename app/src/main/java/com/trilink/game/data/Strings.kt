package com.trilink.game.data

/** 语言 */
enum class Language(val code: String, val label: String) {
    ZH("zh", "中文"),
    EN("en", "English"),
}

/** 所有界面文字。添加新语言只需实现此接口。 */
interface Strings {
    // ── Setup ──
    val appTitle: String
    val choosePiece: String
    val xFirst: String
    val xSecond: String
    val oFirst: String
    val oSecond: String
    val rulesLink: String

    // ── Rules ──
    val rulesTitle: String
    val rulesBoardTitle: String
    val rulesBoardBody: String
    val rulesThreesTitle: String
    val rulesThreesBody: String
    val rulesScoring: String
    val rulesWinTitle: String
    val rulesWinBody: String
    val rulesAiTitle: String
    val rulesAiIntro: String
    val rulesIterative: String
    val rulesIterativeBody: String
    val rulesAlphaBeta: String
    val rulesAlphaBetaBody: String
    val rulesParallel: String
    val rulesParallelBody: String
    val rulesEval: String
    val rulesEvalBody: String
    val rulesTime: String
    val rulesTimeBody: String
    val rulesTechTitle: String
    val rulesTechBody: String

    // ── Game ──
    val gameOver: String
    val aiThinking: String
    val yourTurn: String
    val you: String
    val ai: String
    val vs: String
    val firstHand: String
    val secondHand: String
    val remaining: String
    val newGame: String
    val youWin: String
    val aiWins: String
    val draw: String
    val threesLabel: String
    val invalidPos: String
    val cellOccupied: String
    val waitAi: String
    val aiCantMove: String
    val playerMoved: String
    fun aiMoved(row: Int, col: Int): String
    fun newGameStarted(piece: Char): String
    fun newGameAiFirst(piece: Char): String
    fun gameEnded(player: Int, ai: Int, winner: Int): String

    // ── Settings ──
    val settingsTitle: String
    val aiSearch: String
    val searchTime: String
    val seconds: String
    val threads: String
    val threadsAuto: String
    val threadsCores: String
    fun threadsN(n: Int): String
    val appearance: String
    val themeMode: String
    val themeLight: String
    val themeDark: String
    val themeSystem: String
    val dynamicColor: String
    val dynamicColorDesc: String
    val xColor: String
    val oColor: String
    val about: String
    val aboutApp: String
    val aboutVersion: String
    val aboutLicense: String
    val aboutPlatform: String
    val aboutTech: String
    val aboutAiDesc: String
    val languageLabel: String
}

// ═══════════════════════════════════════════════════════════════════════════════
//  中文
// ═══════════════════════════════════════════════════════════════════════════════

object ZhStrings : Strings {
    override val appTitle = "三连棋"
    override val choosePiece = "选择棋子与先后手"
    override val xFirst = "执 X · 先手"
    override val xSecond = "执 X · 后手"
    override val oFirst = "执 O · 先手"
    override val oSecond = "执 O · 后手"
    override val rulesLink = "规则与算法说明 →"

    override val rulesTitle = "规则与算法"
    override val rulesBoardTitle = "棋盘"
    override val rulesBoardBody = "6×6 = 36 格棋盘。双方轮流落子，不可覆盖已有棋子，直到下满为止。"
    override val rulesThreesTitle = "三连"
    override val rulesThreesBody =
        "棋盘上所有长度 ≥ 3 的连续线段共 32 条：\n" +
        "• 6 条横线（每行 6 格）\n" +
        "• 6 条竖线（每列 6 格）\n" +
        "• 10 条主对角线（↘ 方向）\n" +
        "• 10 条反对角线（↙ 方向）"
    override val rulesScoring =
        "每条线段上，连续同色棋子 ≥ 3 颗即构成三连。\n" +
        "长连拆分规则：\n" +
        "• 3 连 = 1 分\n" +
        "• 4 连 = 2 分\n" +
        "• 5 连 = 3 分\n" +
        "• 6 连 = 4 分"
    override val rulesWinTitle = "胜负判定"
    override val rulesWinBody = "棋盘下满后，分别统计双方在 32 条线段上的三连总数，多者获胜，相等则平局。"
    override val rulesAiTitle = "AI 算法"
    override val rulesAiIntro = "采用迭代加深 Alpha-Beta 剪枝搜索："
    override val rulesIterative = "迭代加深 (Iterative Deepening)"
    override val rulesIterativeBody =
        "从深度 1 开始，逐层增加搜索深度。每完成一层检查是否超时，" +
        "超时则返回当前最佳结果。保证在时限内总能给出一个合法走法。"
    override val rulesAlphaBeta = "Alpha-Beta 剪枝"
    override val rulesAlphaBetaBody =
        "经典的博弈树剪枝算法。AI 层最大化估值，玩家层最小化估值。" +
        "通过 α-β 窗口剔除不可能被选中的分支，大幅减少搜索节点数。"
    override val rulesParallel = "根节点并行"
    override val rulesParallelBody =
        "根节点的每个候选走法独立搜索，使用 Kotlin 协程分配到多个 " +
        "CPU 核心并行计算，充分利用现代手机的多核性能。"
    override val rulesEval = "启发式评估"
    override val rulesEvalBody =
        "终局：精确计算三连差值。\n" +
        "中局：三连差值 × 20 + 2-of-3 威胁（±3）+ 1-of-3 潜力（±1）。" +
        "走法按中心优先排序以提升剪枝效率。"
    override val rulesTime = "时限控制"
    override val rulesTimeBody =
        "单步搜索时限可调（默认 3 秒）。每次递归入口和循环内部均检查截止时间，" +
        "超时立即返回，确保 UI 不卡顿。"
    override val rulesTechTitle = "技术栈"
    override val rulesTechBody =
        "• Kotlin + Jetpack Compose (Material Design 3)\n" +
        "• MVVM 架构 (ViewModel + StateFlow)\n" +
        "• Kotlin Coroutines 异步搜索\n" +
        "• Android 12+ Dynamic Color 动态配色\n" +
        "• 最低支持 Android 8.0 (API 26)"

    override val gameOver = "游戏结束"
    override val aiThinking = "AI 思考中…"
    override val yourTurn = "轮到你了"
    override val you = "你"
    override val ai = "AI"
    override val vs = "vs"
    override val firstHand = "先手"
    override val secondHand = "后手"
    override val remaining = "剩余"
    override val newGame = "新游戏"
    override val youWin = "🎉 你赢了！"
    override val aiWins = "🤖 AI 赢了"
    override val draw = "🤝 平局"
    override val threesLabel = "三连"
    override val invalidPos = "无效位置！"
    override val cellOccupied = "该位置已有棋子！请重新选择。"
    override val waitAi = "请等待 AI 落子后再操作。"
    override val aiCantMove = "AI 无法落子。"
    override val playerMoved = "玩家落子"
    override fun aiMoved(row: Int, col: Int) = "AI 已落子 ($row,$col)，轮到你了。"
    override fun newGameStarted(piece: Char) = "新游戏！你执 $piece，先手落子。"
    override fun newGameAiFirst(piece: Char) = "新游戏！你执 $piece，AI 已先手落子，轮到你了。"
    override fun gameEnded(player: Int, ai: Int, winner: Int): String {
        val result = when { winner > 0 -> "玩家胜"; winner < 0 -> "AI胜"; else -> "平局" }
        return "游戏结束！你: $player 三连，AI: $ai 三连 → $result"
    }

    override val settingsTitle = "设置"
    override val aiSearch = "AI 搜索"
    override val searchTime = "搜索时限"
    override val seconds = "秒"
    override val threads = "线程数"
    override val threadsAuto = "自动"
    override val threadsCores = "核"
    override fun threadsN(n: Int) = "$n 线程"
    override val appearance = "外观"
    override val themeMode = "主题模式"
    override val themeLight = "浅色"
    override val themeDark = "深色"
    override val themeSystem = "跟随系统"
    override val dynamicColor = "动态配色"
    override val dynamicColorDesc = "Android 12+ 根据壁纸生成配色"
    override val xColor = "X 棋子颜色"
    override val oColor = "O 棋子颜色"
    override val about = "关于"
    override val aboutApp = "应用"
    override val aboutVersion = "版本"
    override val aboutLicense = "许可证"
    override val aboutPlatform = "平台"
    override val aboutTech = "技术"
    override val aboutAiDesc = "AI 使用迭代加深 Alpha-Beta 搜索，\n根节点协程并行，单步时限可调。"
    override val languageLabel = "语言"
}

// ═══════════════════════════════════════════════════════════════════════════════
//  English
// ═══════════════════════════════════════════════════════════════════════════════

object EnStrings : Strings {
    override val appTitle = "TriLink"
    override val choosePiece = "Choose Piece & Turn"
    override val xFirst = "X · First"
    override val xSecond = "X · Second"
    override val oFirst = "O · First"
    override val oSecond = "O · Second"
    override val rulesLink = "Rules & Algorithm →"

    override val rulesTitle = "Rules & Algorithm"
    override val rulesBoardTitle = "Board"
    override val rulesBoardBody = "6×6 = 36 cells. Players take turns placing pieces until the board is full. Pieces cannot be overwritten."
    override val rulesThreesTitle = "Three-in-a-Row"
    override val rulesThreesBody =
        "There are 32 line segments of length ≥ 3 on the board:\n" +
        "• 6 horizontal lines (6 cells each)\n" +
        "• 6 vertical lines (6 cells each)\n" +
        "• 10 main diagonals (↘ direction)\n" +
        "• 10 anti-diagonals (↙ direction)"
    override val rulesScoring =
        "On each line, ≥ 3 consecutive same-color pieces form a three-in-a-row.\n" +
        "Long line splitting:\n" +
        "• 3-in-a-row = 1 point\n" +
        "• 4-in-a-row = 2 points\n" +
        "• 5-in-a-row = 3 points\n" +
        "• 6-in-a-row = 4 points"
    override val rulesWinTitle = "Win Condition"
    override val rulesWinBody = "When the board is full, count all three-in-a-row connections across all 32 lines. The player with more connections wins; equal counts result in a draw."
    override val rulesAiTitle = "AI Algorithm"
    override val rulesAiIntro = "Uses Iterative Deepening Alpha-Beta search:"
    override val rulesIterative = "Iterative Deepening"
    override val rulesIterativeBody =
        "Starts from depth 1 and increases search depth layer by layer. " +
        "Checks for timeout after each completed depth; returns the best result found so far if time expires."
    override val rulesAlphaBeta = "Alpha-Beta Pruning"
    override val rulesAlphaBetaBody =
        "Classic game tree pruning algorithm. AI maximizes evaluation, player minimizes. " +
        "Uses α-β windows to eliminate branches that cannot affect the final result."
    override val rulesParallel = "Root Parallelization"
    override val rulesParallelBody =
        "Each candidate move at the root is searched independently using Kotlin coroutines " +
        "distributed across multiple CPU cores, leveraging modern phone hardware."
    override val rulesEval = "Heuristic Evaluation"
    override val rulesEvalBody =
        "Endgame: exact three-in-a-row difference.\n" +
        "Midgame: three difference × 20 + 2-of-3 threats (±3) + 1-of-3 potential (±1). " +
        "Moves are sorted by centrality for better pruning."
    override val rulesTime = "Time Control"
    override val rulesTimeBody =
        "Per-move time limit is adjustable (default 3 seconds). Deadline checks are performed " +
        "at every recursion entry and loop iteration; returns immediately on timeout."
    override val rulesTechTitle = "Tech Stack"
    override val rulesTechBody =
        "• Kotlin + Jetpack Compose (Material Design 3)\n" +
        "• MVVM (ViewModel + StateFlow)\n" +
        "• Kotlin Coroutines for async search\n" +
        "• Android 12+ Dynamic Color\n" +
        "• Min SDK: Android 8.0 (API 26)"

    override val gameOver = "Game Over"
    override val aiThinking = "AI thinking…"
    override val yourTurn = "Your Turn"
    override val you = "You"
    override val ai = "AI"
    override val vs = "vs"
    override val firstHand = "First"
    override val secondHand = "Second"
    override val remaining = "Remaining"
    override val newGame = "New Game"
    override val youWin = "🎉 You Win!"
    override val aiWins = "🤖 AI Wins"
    override val draw = "🤝 Draw"
    override val threesLabel = "threes"
    override val invalidPos = "Invalid position!"
    override val cellOccupied = "Cell occupied! Choose another."
    override val waitAi = "Please wait for AI to move."
    override val aiCantMove = "AI cannot move."
    override val playerMoved = "Player moved"
    override fun aiMoved(row: Int, col: Int) = "AI moved ($row,$col). Your turn."
    override fun newGameStarted(piece: Char) = "New game! You are $piece, go first."
    override fun newGameAiFirst(piece: Char) = "New game! You are $piece, AI went first."
    override fun gameEnded(player: Int, ai: Int, winner: Int): String {
        val result = when { winner > 0 -> "Player wins"; winner < 0 -> "AI wins"; else -> "Draw" }
        return "Game Over! You: $player threes, AI: $ai threes → $result"
    }

    override val settingsTitle = "Settings"
    override val aiSearch = "AI Search"
    override val searchTime = "Time Limit"
    override val seconds = "s"
    override val threads = "Threads"
    override val threadsAuto = "Auto"
    override val threadsCores = "cores"
    override fun threadsN(n: Int) = "$n threads"
    override val appearance = "Appearance"
    override val themeMode = "Theme Mode"
    override val themeLight = "Light"
    override val themeDark = "Dark"
    override val themeSystem = "System"
    override val dynamicColor = "Dynamic Color"
    override val dynamicColorDesc = "Android 12+ wallpaper-based theming"
    override val xColor = "X Piece Color"
    override val oColor = "O Piece Color"
    override val about = "About"
    override val aboutApp = "App"
    override val aboutVersion = "Version"
    override val aboutLicense = "License"
    override val aboutPlatform = "Platform"
    override val aboutTech = "Tech"
    override val aboutAiDesc = "AI uses iterative deepening Alpha-Beta search,\nroot parallelization with coroutines, adjustable time limit."
    override val languageLabel = "Language"
}

/** 根据语言获取字符串 */
fun getStrings(lang: Language): Strings = when (lang) {
    Language.ZH -> ZhStrings
    Language.EN -> EnStrings
}
