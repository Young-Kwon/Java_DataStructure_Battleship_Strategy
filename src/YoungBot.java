import battleship.*;
import java.awt.Point;
import java.util.HashSet;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class YoungBot implements BattleShipBot {
    private int gameSize;
    private BattleShip2 battleShip;
    private Random random;
    private HashSet<Point> shotsFired;
    private boolean isShipHit;
    private Point lastHit;
    private List<Point> potentialTargets;

    @Override
    public void initialize(BattleShip2 b) {
        battleShip = b;
        gameSize = b.BOARD_SIZE;
        shotsFired = new HashSet<>();
        isShipHit = false;
        potentialTargets = new ArrayList<>();
        random = new Random(0xAAAAAAAA);
    }

    @Override
    public void fireShot() {
        Point shot = null;

        if (isShipHit) {
            shot = getNextTargetPoint();
        } else {
            shot = getCheckerboardPoint();
        }

        // Fallback to random if no valid shot is found
        while (shot == null || shotsFired.contains(shot)) {
            int x = random.nextInt(gameSize);
            int y = random.nextInt(gameSize);
            shot = new Point(x, y);
        }

        shotsFired.add(shot);
        boolean hit = battleShip.shoot(shot);
        if (hit) {
            isShipHit = true;
            lastHit = shot;
            updatePotentialTargets(shot);
        } else if (isShipHit && potentialTargets.isEmpty()) {
            isShipHit = false; // Reset if no more targets around the last hit
        }
    }

    private Point getNextTargetPoint() {
        while (!potentialTargets.isEmpty()) {
            Point target = potentialTargets.remove(0);
            if (!shotsFired.contains(target)) {
                return target;
            }
        }
        return null;
    }

    private void updatePotentialTargets(Point hit) {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] d : directions) {
            int x = hit.x + d[0];
            int y = hit.y + d[1];
            if (x >= 0 && x < gameSize && y >= 0 && y < gameSize) {
                potentialTargets.add(new Point(x, y));
            }
        }
    }

    private Point getCheckerboardPoint() {
        for (int x = 0; x < gameSize; x++) {
            for (int y = x % 2; y < gameSize; y += 2) { // Skip every other row
                Point p = new Point(x, y);
                if (!shotsFired.contains(p)) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public String getAuthors() {
        return "Young Sang Kwon (000847777)";
    }
}
