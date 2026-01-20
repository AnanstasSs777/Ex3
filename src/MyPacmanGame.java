import exe.ex3.game.GhostCL;
import exe.ex3.game.PacmanGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class MyPacmanGame {
    public static Map2D createMapFromText(String[] visualMAp){
        int h = visualMAp.length;
        int w = visualMAp[0].length();

        Map2D newMap = new Map(w,h,0);

        for(int i=0;i<h;i++){
            String line = visualMAp[i];
            for(int j=0;j<w;j++){
                char c = line.charAt(j);

                int x =j;
                int y = h-1-i;

                if(c=='#'){
                    newMap.setPixel(x,y,1);// wall
                }
                else if(c==','){
                    newMap.setPixel(x,y,0);//empty
                }
                else if(c=='.'){
                    newMap.setPixel(x,y,3);//food
                }
                else if(c=='-'){
                    newMap.setPixel(x,y,5);
                }
            }
        }
        return newMap;
    }
    public static int[] pacmanStart(String[] visualMAp){
        int h = visualMAp.length;
        for(int j=0;j<h;j++){
            String line = visualMAp[j];
            int col = line.indexOf('P');
            if(col != -1){
                int x = col;
                int y = h-1-j;
                return new int[]{x,y};
            }
        }
        return new int[]{1,1};
    }
    public static List<int[]> ghostStart(String[] visualMAp){
        List<int[]> ghosts = new ArrayList<int[]>();
        int h = visualMAp.length;
        for(int j=0;j<h;j++){
            String line = visualMAp[j];
            for(int k=0; k< line.length();k++){
                if(line.charAt(k)=='G'){
                    ghosts.add(new int[]{k,h-1-j});
                }
            }
        }
        return ghosts;
    }
    public static int getAutoMove(Map2D map, int pacX, int pacY) {
        int w = map.getWidth();
        int h = map.getHeight();
        boolean[][] visited = new boolean[w][h];
        int[][][] parent = new int[w][h][2];
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{pacX, pacY});
        visited[pacX][pacY] = true;

        int targetX = -1, targetY = -1;
        boolean found = false;


        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int cx = curr[0];
            int cy = curr[1];

            if (map.getPixel(cx, cy) == 3 || map.getPixel(cx, cy) == 5) {
                targetX = cx; targetY = cy; found = true; break;
            }

            for (int i = 0; i < 4; i++) {
                int nx = cx + directions[i][0];
                int ny = cy + directions[i][1];
                if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                    if (!visited[nx][ny] && map.getPixel(nx, ny) != 1) {
                        visited[nx][ny] = true;
                        parent[nx][ny][0] = cx;
                        parent[nx][ny][1] = cy;
                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }
        if (!found) return -1;


        int currX = targetX, currY = targetY;
        while (true) {
            int parX = parent[currX][currY][0];
            int parY = parent[currX][currY][1];
            if (parX == pacX && parY == pacY) break;
            currX = parX; currY = parY;
        }

        if (currX > pacX) return 1;
        if (currX < pacX) return 3;
        if (currY > pacY) return 0;
        if (currY < pacY) return 2;
        return -1;
    }


    public static void drawMap(Map2D map, int pacX, int pacY, double pacAngle, List<int[]> ghosts){
        StdDraw.clear(Color.BLACK);

        for(int x = 0; x < map.getWidth();x++){
            for(int y = 0; y < map.getHeight();y++){
                int pixel = map.getPixel(x,y);

                if(pixel == 1){
                    StdDraw.picture(x + 0.5, y + 0.5,"wall.jpg", 1.0, 1.0 );
                }
                else if(pixel == 3){
                    StdDraw.picture(x + 0.5, y + 0.5, "Café.png", 0.5, 0.5 );
                }
                else if(pixel == 5){
                    StdDraw.picture(x + 0.5, y + 0.5, "kotik.png", 0.7, 0.7 );
                }
            }
        }
        StdDraw.picture(pacX + 0.5, pacY + 0.5, "p1.png", 0.8, 0.8, pacAngle);

        for(int[] ghost : ghosts){
            int gx = ghost[0];
            int gy = ghost[1];
            StdDraw.picture(gx + 0.5, gy + 0.5, "g3.png", 0.8, 0.8);
        }
        StdDraw.show();
    }



    public static void main(String[] args) {
        String[] myGame = {
                "#######################",
                "#..........#..........#",
                "#.#.######.#.######.#.#",
                "#.#-#.............#-#.#",
                "#.#.#.#.#######.#.#.#.#",
                "#.#.#.#....#....#.#.#.#",
                "#.....####.#.####.....#",
                "#####.#....P....#.#####",
                "#.....#.###,###.#.....#",
                "##.##.#.#,,,,,#.#.##.##",
                "........#,,G,,#........",
                "##.##.#.#######.#.##.##",
                "......#.........#......",
                "#.###.#.#######>#.###.#",
                "#..........#..........#",
                "#.###.####.#.####.###.#",
                "#..-#.............#-..#",
                "###.#.#.#######.#.#.###",
                "#.....#....#....#.....#",
                "#.########.#.########.#",
                "#.....................#",
                "#######################"
        };
        Map2D map = createMapFromText(myGame);
        int[] pacStart = pacmanStart(myGame);
        int pacX = pacStart[0];
        int pacY = pacStart[1];
        List<int[]> ghosts = ghostStart(myGame);



        double pacAngle = 0;

        StdDraw.setCanvasSize(800, 600);
        StdDraw.setXscale(0, map.getWidth());
        StdDraw.setYscale(0, map.getHeight());
        StdDraw.enableDoubleBuffering();

        while (true) {

            drawMap(map, pacX, pacY,pacAngle, ghosts);


            int dir = getAutoMove(map, pacX, pacY);


            int nextX = pacX;
            int nextY = pacY;

            if (dir == 1) { nextX++; pacAngle = 0; }
            if (dir == 3) { nextX--; pacAngle = 180; }
            if (dir == 0) { nextY++; pacAngle = 90; }
            if (dir == 2) { nextY--; pacAngle = 270; }
            int w = map.getWidth();
            int h = map.getHeight();

            nextX = (nextX + w)%w;
            nextY = (nextY + h)%h;


            if (map.getPixel(nextX, nextY) != 1) {
                pacX = nextX;
                pacY = nextY;


                if (map.getPixel(pacX, pacY) == 3) {
                    map.setPixel(pacX, pacY, 0);
                }
                else if (map.getPixel(pacX, pacY) == 5) {
                    map.setPixel(pacX, pacY, 0);
                }
            }


            StdDraw.pause(120);}
    }
}
