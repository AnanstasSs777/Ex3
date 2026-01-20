import exe.ex3.game.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo {
    public Ex3Algo() {
    }

    @Override
    /**
     *  Add a short description for the algorithm as a String.
     */
    public String getInfo() {
        return " ";
    }

    @Override
    /**
     * This method it is a brain of the algorithm.
     * It called by the server every turn of the pacman. Analyzes the situation and returns a move direction.
     * Logic:
     * 1. Parses current position and ghost positions.
     * 2. If there is a threat -> Run away
     * 3. If not so find and filter the food, choose the closest and go by BFS algorithm.
     **/
    public int move(PacmanGame game) {


        String posStr = game.getPos(0);
        if (posStr == null) {
            System.err.println("Error: the game returned a null position");
            return PacmanGame.STAY;
        }

        int[][] board = game.getGame(0);
        String pacmanPosStr = game.getPos(0);
        Point pacmanPos = parsePos(pacmanPosStr);

        // where are ghosts ( parse position of all ghost)
        GhostCL[] ghosts = game.getGhosts(0);
        ArrayList<Point> ghostPos = new ArrayList<>();
        if (ghosts != null) {
            for (GhostCL g : ghosts) {
                ghostPos.add(parsePos(g.getPos(0)));
            }
        }

        //check of dangerous
        if (isInDanger(board, pacmanPos, ghostPos)) {
            return runAway(board, pacmanPos, ghostPos);
        }

        //getting of all food and filter out unsafe that near to the ghost
        ArrayList<Point> allFood = getPinkPoints(board, game);
        ArrayList<Point> safeFood = filterOfFood(board, allFood, ghostPos);

        if (safeFood.isEmpty()) safeFood = allFood;
        if (safeFood.isEmpty()) return PacmanGame.STAY;

        // search safe nearest food and search a path to it
        Point bestPPoint = findClosestPPoint(board, pacmanPos, safeFood);
        if (bestPPoint != null) {
            return getNextMoveBFS(board, pacmanPos, bestPPoint, ghostPos);
        }

        return PacmanGame.STAY;
    }
    //------private helper methods------


    /**
     * This method don't give to our pacman go through the ghost's spawn zone area ( center)
     **/
    private boolean isSpawnZone(Point p, int w, int h) {
        Point c = new Point(w / 2, h / 2);
        return Math.abs(p.x - c.x) <= 0 && Math.abs(p.y - c.y) <= 0;
    }

    // this method checks if there is even one ghost near to the pink point.
    // here important to use bfs logic, cuz we take into account the calculation with the transition to the other side
    /**
     * This method like danger detector
     * Role: Checks if any ghost is within a 2-move radius( by BFS)
     * It's for the switch the mode from "eating" to "fleeding".
     **/
    private boolean isInDanger(int[][] board, Point pacman, ArrayList<Point> ghosts) {
        for (Point ghost : ghosts) {
            int dist = bfsDistance(board, pacman, ghost);
            if (dist != -1 && dist <= 2) {
                return true;
            }
        }
        return false;
    }

    // this method filters all food if it nearby to the ghost.
    /**
     * This method is a simple filter of pink points.
     * Logic: filter points that are close to the ghost
     * It is for preventing Pacman from going towards food that is a trap.
     **/
    private ArrayList<Point> filterOfFood(int[][] board, ArrayList<Point> foodList, ArrayList<Point> dangGhosts) {
        ArrayList<Point> safe = new ArrayList<>();
        for (Point food : foodList) {
            boolean isSafe = true;
            for (Point ghost : dangGhosts) {
                // if the pink point is near to ghost is a trap.
                // so there is a code with a simple math without bfs algorithm
                int dist = Math.abs(food.x - ghost.x) + Math.abs(food.y - ghost.y);
                if (dist <= 2) {
                    isSafe = false;
                    break;
                }
            }
            if (isSafe) safe.add(food);
        }
        return safe;
    }

    /**
     * This method is retreat logic.
     * Role: Choose a direction that maximizes the distance from the nearest ghost.
     * How: iterates through all neighbors and calculates the distance to the threat.
     **/
    private int runAway(int[][] board, Point pacman, ArrayList<Point> ghosts) {
        int bestDir = PacmanGame.STAY;
        int maxDistToGhost = -1;

        int[] dirs = {PacmanGame.UP, PacmanGame.DOWN, PacmanGame.LEFT, PacmanGame.RIGHT};

        for (int dir : dirs) {
            Point neighbour = getNeighbor(pacman, dir, board.length, board[0].length);

            //if it's wall - continue
            if (neighbour == null || board[neighbour.x][neighbour.y] == 1) continue;

            // search the dist to the closest ghost near to pacman
            int minGhostDist = Integer.MAX_VALUE;
            for (Point ghost : ghosts) {
                int c = bfsDistance(board, neighbour, ghost);
                if (c == -1) c = Integer.MAX_VALUE; // if ghost cannot eat us
                if (c < minGhostDist) {
                    minGhostDist = c;
                }
            }

            // pacman has to choose the path to be much longer from the nearest ghost
            if (minGhostDist > maxDistToGhost) {
                maxDistToGhost = minGhostDist;
                bestDir = dir;
            }
        }
        return bestDir;
    }

    /**
     * THis method is searching for the next step to the pink point.
     * Role: Builds the shortest path (BFS) to point p and return the first move direction.
     * Features: Considers walls, avoids the spawn zone, and avoids spots near to ghosts.
     **/
    private int getNextMoveBFS(int[][] board, Point start, Point p, ArrayList<Point> ghostsToAvoid) {
        if (p == null) return -1;

        int w = board.length;
        int h = board[0].length;

        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        Point[][] parent = new Point[w][h];
        boolean[][] visited = new boolean[w][h];
        visited[start.x][start.y] = true;

        boolean found = false;

        while (!queue.isEmpty()) {
            Point curr = queue.poll();
            if (curr.x == p.x && curr.y == p.y) {
                found = true;
                break;
            }
            int[] dirs = {PacmanGame.UP, PacmanGame.DOWN, PacmanGame.LEFT, PacmanGame.RIGHT};
            for (int dir : dirs) {
                Point n = getNeighbor(curr, dir, w, h);
                if (n == null) continue;
                // check wall, visited, spawn zone, ghost near to.
                if (board[n.x][n.y] == 1 || visited[n.x][n.y]) continue;
                if (isSpawnZone(n, w, h) && !n.equals(p)) continue;
                if (!isLocSafe(n, ghostsToAvoid)) continue;

                visited[n.x][n.y] = true;
                parent[n.x][n.y] = curr;
                queue.add(n);
            }
        }
        // if it's not safe - run
        if (!found) return -1;
        //Reconstruct th path backwards from the pink point to start to find the first step
        Point curr = p;
        if (parent[curr.x][curr.y] == null) return -1;
        while (parent[curr.x][curr.y] != null && !parent[curr.x][curr.y].equals(start)) {
            curr = parent[curr.x][curr.y];
        }
        return getDirection(start, curr, w, h);
    }

    /**
     * It is local safety check.
     * Helps to BFS, checks if a point is adjacent to a ghost.
     **/
    private boolean isLocSafe(Point p, ArrayList<Point> ghosts) {
        if (ghosts == null || ghosts.isEmpty()) return true;
        for (Point ghost : ghosts) {
            int dist = Math.abs(p.x - ghost.x) + Math.abs(p.y - ghost.y);
            if (dist <= 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method like a food scanner.
     *It scans the entire map and collects coordinates of all available pink points, excluding spawn.
     **/
    private ArrayList<Point> getPinkPoints(int[][] board, PacmanGame game) {
        ArrayList<Point> pinkPoints = new ArrayList<>();
        int w = board.length, h = board[0].length;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                int val = board[x][y];
                if (val != 1 && val != 0) {
                    Point p = new Point(x, y);
                    if (!isSpawnZone(p, w, h)) {
                        pinkPoints.add(p);
                    }
                }
            }
        }
        return pinkPoints;
    }
    /**
     * This method is best target selector.
     * Role:Selects the poinr from the list that is closest to run to ( by BFS distance)
     **/
    private Point findClosestPPoint(int[][] board, Point start, ArrayList<Point> pinkPoints) {
        Point closest = null;
        double minDist = Integer.MAX_VALUE;
        for (Point p : pinkPoints) {
            int dist = bfsDistance(board, start, p);
            if (dist != -1 && dist < minDist) {
                closest = p;
                minDist = dist;
            }
        }
        return closest;
    }

    /**
    * It is a distance calculator.
     * Calculates the exact number od steps from point A to B considering walls (BFS)
    **/
    private int bfsDistance(int[][] board, Point start, Point p) {
        int w = board.length;
        int h = board[0].length;
        Queue<Point> q = new LinkedList<>();
        q.add(start);
        int[][] dist = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                dist[i][j] = -1;
            }
        }
        dist[start.x][start.y] = 0;
        while (!q.isEmpty()) {
            Point curr = q.poll();
            if (curr.x == p.x && curr.y == p.y) return dist[curr.x][curr.y];
            int[] dirs = {PacmanGame.UP, PacmanGame.DOWN, PacmanGame.LEFT, PacmanGame.RIGHT};
            for (int dir : dirs) {
                Point n = getNeighbor(curr, dir, w, h);
                if (n != null && board[n.x][n.y] != 1 && dist[n.x][n.y] == -1) {
                    dist[n.x][n.y] = dist[curr.x][curr.y] + 1;
                    q.add(n);
                }
            }
        }

        return -1;
    }

    /**
     * this method gives to our pacman to move to the other side of the map
     * if he was on the edge and continues moving towards it.
     **/
    private Point getNeighbor(Point p, int dir, int w, int h) {
        if (p == null) return null;
        int x = p.x;
        int y = p.y;
        // normal steps
        if (dir == PacmanGame.UP) y++;
        if (dir == PacmanGame.DOWN) y--;
        if (dir == PacmanGame.LEFT) x--;
        if (dir == PacmanGame.RIGHT) x++;

        // cyclicity: this game map is cyclical, so this code  helps move
        x = (x + w) % w;
        y = (y + h) % h;
        if (x < 0) x += w;
        if (y < 0) y += h;
        return new Point(x, y);
    }

    /**
     * this method transforms a math plan of the path of the pacman into specific action for the game.
     * if it is none of the options, so pacman will choose the default "up".
     *
     **/
    private int getDirection(Point p1, Point p2, int w, int h) {
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;

        //steps near to wall
        if (dx > 1) return PacmanGame.LEFT;
        if (dx < -1) return PacmanGame.RIGHT;
        if (dy > 1) return PacmanGame.DOWN;
        if (dy < -1) return PacmanGame.UP;

        // simple steps
        if (dx == 1) return PacmanGame.RIGHT;
        if (dx == -1) return PacmanGame.LEFT;
        if (dy == 1) return PacmanGame.UP;
        if (dy == -1) return PacmanGame.DOWN;

        //default choose
        return PacmanGame.UP;
    }


    /**
     * a simple casting ( if we have coordinate as 3.2, 0.6 it will be 3.0,1.0).
     *
     **/

    private Point parsePos(String s) {
        if (s == null) return new Point(0, 0);
        try {
            String[] parts = s.split(",");
            int x = (int) Math.round(Double.parseDouble(parts[0]));
            int y = (int) Math.round(Double.parseDouble(parts[1]));
            return new Point(x, y);
        } catch (Exception e) {
            return new Point(0, 0);
        }
    }

    /**
     * this class is our "box" of coordinates, just for comfortable saving;
     * Point - it is point on our game map.
     *
     **/
    private class Point {
        int x, y;
        public Point(int x, int y) {this.x = x; this.y = y;}

        @Override
        public boolean equals(Object o) {
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }
    }
}