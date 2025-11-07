import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SnakeSwing extends JPanel implements ActionListener, KeyListener {
    // --- Spielfeld ---
    private static final int CELL = 22;          // Pixel pro Zelle
    private static final int COLS = 28;          // Spalten
    private static final int ROWS = 20;          // Zeilen
    private static final int MARGIN = 12;        // Rand fürs HUD

    // --- Game State ---
    private Deque<Point> snake = new ArrayDeque<>();
    private Point food;
    private Dir dir = Dir.RIGHT;
    private Dir nextDir = Dir.RIGHT;
    private boolean running = false;
    private boolean paused = false;
    private boolean gameOver = false;
    private int score = 0;
    private int highScore = 0;
    private int tickMs = 140; // Startgeschwindigkeit
    private final Timer timer = new Timer(tickMs, this);
    private final Random rnd = new Random();

    // --- Farben/Fonts ---
    private final Color bg = new Color(22, 22, 26);
    private final Color grid = new Color(40, 40, 48);
    private final Color snakeHead = new Color(0x48,0xC7,0x7E);
    private final Color snakeBody = new Color(0x2B,0xA8,0x65);
    private final Color foodCol = new Color(0xFF,0x5A,0x54);
    private final Color textCol = new Color(230, 230, 235);
    private final Font hudFont = new Font("Consolas", Font.PLAIN, 16);
    private final Font bigFont = new Font("Consolas", Font.BOLD, 28);

    enum Dir { UP, DOWN, LEFT, RIGHT }

    public SnakeSwing() {
        setPreferredSize(new Dimension(COLS * CELL + MARGIN * 2, ROWS * CELL + MARGIN * 2 + 30));
        setBackground(bg);
        setFocusable(true);
        addKeyListener(this);
        startNewGame();
    }

    private void startNewGame() {
        score = 0;
        dir = Dir.RIGHT;
        nextDir = Dir.RIGHT;
        paused = false;
        gameOver = false;
        tickMs = 140;
        timer.setDelay(tickMs);

        snake.clear();
        int cx = COLS / 2 - 2;
        int cy = ROWS / 2;
        // Startschlange
        snake.addFirst(new Point(cx+2, cy));
        snake.addLast(new Point(cx+1, cy));
        snake.addLast(new Point(cx, cy));

        spawnFood();
        running = true;
        timer.start();
        repaint();
    }

    private void spawnFood() {
        Set<Point> occ = new HashSet<>(snake);
        Point p;
        do {
            p = new Point(rnd.nextInt(COLS), rnd.nextInt(ROWS));
        } while (occ.contains(p));
        food = p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running || paused || gameOver) return;

        // Richtung fixen (verhindert 180°-Turn)
        if (!isOpposite(dir, nextDir)) dir = nextDir;

        Point head = snake.peekFirst();
        Point next = new Point(head);
        switch (dir) {
            case UP -> next.y--;
            case DOWN -> next.y++;
            case LEFT -> next.x--;
            case RIGHT -> next.x++;
        }

        // Kollision mit Wand?
        if (next.x < 0 || next.x >= COLS || next.y < 0 || next.y >= ROWS) {
            onGameOver(); return;
        }
        // Kollision mit sich selbst?
        if (snake.contains(next)) {
            onGameOver(); return;
        }

        // Move
        snake.addFirst(next);

        // Essen?
        if (next.equals(food)) {
            score += 10;
            // Speed leicht erhöhen, bis Minimum 70ms
            if (tickMs > 70) {
                tickMs -= 4;
                timer.setDelay(tickMs);
            }
            spawnFood();
        } else {
            snake.removeLast(); // normal weiter, kein Wachstum
        }

        repaint();
    }

    private void onGameOver() {
        gameOver = true;
        running = false;
        timer.stop();
        highScore = Math.max(highScore, score);
        repaint();
    }

    private static boolean isOpposite(Dir a, Dir b) {
        return (a == Dir.UP && b == Dir.DOWN) ||
               (a == Dir.DOWN && b == Dir.UP) ||
               (a == Dir.LEFT && b == Dir.RIGHT) ||
               (a == Dir.RIGHT && b == Dir.LEFT);
    }

    // --- Drawing ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gg = (Graphics2D) g.create();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ox = MARGIN;
        int oy = MARGIN + 24; // Platz für HUD
        // Grid
        gg.setColor(grid);
        for (int x = 0; x <= COLS; x++) {
            gg.drawLine(ox + x*CELL, oy, ox + x*CELL, oy + ROWS*CELL);
        }
        for (int y = 0; y <= ROWS; y++) {
            gg.drawLine(ox, oy + y*CELL, ox + COLS*CELL, oy + y*CELL);
        }

        // Food
        if (food != null) {
            gg.setColor(foodCol);
            int fx = ox + food.x * CELL;
            int fy = oy + food.y * CELL;
            gg.fillRoundRect(fx+2, fy+2, CELL-4, CELL-4, 8, 8);
        }

        // Snake
        Point head = snake.peekFirst();
        if (head != null) {
            // Body
            gg.setColor(snakeBody);
            for (Point p : snake) {
                if (p.equals(head)) continue;
                int sx = ox + p.x * CELL;
                int sy = oy + p.y * CELL;
                gg.fillRoundRect(sx+2, sy+2, CELL-4, CELL-4, 10, 10);
            }
            // Head
            gg.setColor(snakeHead);
            int hx = ox + head.x * CELL;
            int hy = oy + head.y * CELL;
            gg.fillRoundRect(hx+1, hy+1, CELL-2, CELL-2, 12, 12);
        }

        // HUD
        gg.setFont(hudFont);
        gg.setColor(textCol);
        String hudLeft = "Score: " + score + "   High: " + highScore + "   Speed: " + (1000 / Math.max(1,tickMs)) + " tps";
        gg.drawString(hudLeft, MARGIN, MARGIN + 16);

        // Status-Overlay
        if (paused) {
            drawCenterText(gg, "PAUSE (Space)", bigFont, textCol);
        } else if (gameOver) {
            drawCenterText(gg, "GAME OVER  -  R = Restart", bigFont, textCol);
        }

        gg.dispose();
    }

    private void drawCenterText(Graphics2D gg, String s, Font f, Color c) {
        gg.setFont(f);
        FontMetrics fm = gg.getFontMetrics();
        int w = getWidth();
        int h = getHeight();
        int tx = (w - fm.stringWidth(s)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        gg.setColor(new Color(0,0,0,120));
        gg.fillRoundRect(tx-16, ty - fm.getAscent() - 10, fm.stringWidth(s)+32, fm.getHeight()+14, 16, 16);
        gg.setColor(c);
        gg.drawString(s, tx, ty);
    }

    // --- Input ---
    @Override public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        switch (k) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> nextDir = Dir.UP;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> nextDir = Dir.DOWN;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> nextDir = Dir.LEFT;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> nextDir = Dir.RIGHT;
            case KeyEvent.VK_SPACE -> {
                if (!gameOver) {
                    paused = !paused;
                    if (paused) timer.stop(); else timer.start();
                    repaint();
                }
            }
            case KeyEvent.VK_R -> {
                startNewGame();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) { }

    private static void createAndShow() {
        JFrame f = new JFrame("Snake");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setContentPane(new SnakeSwing());
        f.pack();
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        // System Look & Feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(SnakeSwing::createAndShow);
    }
}
