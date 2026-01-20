import java.util.LinkedList;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D {
	private int[][] _map;
	private boolean _cyclicFlag = true;
	
	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {init(w,h, v);}
	/**
	 * Constructs a square map (size*size).
	 * @param size
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
	 */
	public Map(int[][] data) {
		init(data);
	}
	@Override
	public void init(int w, int h, int v) {
        this._map = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                this._map[i][j] = v;
            }
        }
	}
	@Override
	public void init(int[][] arr) {
        //exceptions: (out is trow new - those notes for me only)
        if (arr == null) {
            throw new RuntimeException("Error: The array is null!");
        }
        // if it's empty
        if (arr.length == 0 || arr[0].length == 0) {
            throw new RuntimeException("Error: The array is empty!");
        }

        int w = arr.length;
        int h = arr[0].length;
        // if it ragged ( if w isn't the same in every h) but we check too if i of array is empty
        for (int i = 0; i < w; i++) {
            if (arr[i] == null ||arr[i].length != h) {
                throw new RuntimeException("Error: The array is ragged.");
            }
        }

        this._map = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                this._map[i][j] = arr[i][j];
            }
        }
	}
	@Override
	public int[][] getMap() {
		int[][] ans = null;
        if(_map == null) {return ans;}
        int w = this._map.length;
        int h = this._map[0].length;
        ans = new int[w][h];
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                ans[i][j] = _map[i][j];
            }
        }
		return ans;
	}
	@Override
	/////// add your code below ///////
	public int getWidth() {
        int ans = -1;
        if (this._map != null && this._map.length != 0) {
            ans = this._map.length;
        }
        return ans;
    }
	@Override
	/////// add your code below ///////
	public int getHeight() {
        int ans = -1;
        if(this._map != null  && this._map[0].length != 0){
            ans = this._map[0].length;
        }
        return ans;
    }
	@Override
	/////// add your code below ///////
	public int getPixel(int x, int y) {
        int ans = -1;
        if(this._map == null || x<0 || y<0 || x>=this._map.length || y>=this._map[0].length){return ans;}
        else{ans = this._map[x][y];}
        return ans;
    }
	@Override
	/////// add your code below ///////
	public int getPixel(Pixel2D p) {
        int ans = -1;
        if(p != null){
            return getPixel(p.getX(), p.getY());
        }
        return ans;
	}
	@Override
	/////// add your code below ///////
	public void setPixel(int x, int y, int v) {
        if (_map == null) {return;}
        if(x<0 || y<0){return;}
        if(x>=this._map.length || y>=this._map[0].length){return;}
        _map[x][y] = v;
    }
	@Override
	/////// add your code below ///////
	public void setPixel(Pixel2D p, int v) {
        if (p != null) {
            setPixel(p.getX(), p.getY(), v);
        }
	}
	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v) {
		int ans=0;
		if(xy == null || !isInside(xy)) return ans;
        if(getPixel(xy) == new_v) return ans;

        int w = getWidth();
        int h = getHeight();
        int old_v = getPixel(xy);

        LinkedList<Pixel2D> queue = new LinkedList<>();
        boolean[][] visited = new boolean[w][h];
        queue.add(xy);
        visited[xy.getX()][xy.getY()] = true;
        // directions: up, down, right, left
        int[][] dirs ={{0,1},{0,-1},{1,0},{-1,0}};

        while(!queue.isEmpty()){
            Pixel2D p = queue.poll();
            int x = p.getX();
            int y = p.getY();

            //color
            setPixel(x,y,new_v);
            ans ++;

            for(int[] dir : dirs){
                int nx = x + dir[0];
                int ny = y + dir[1];

                if(_cyclicFlag){
                    nx = (nx + w) % w;
                    ny = (ny + h) % h;
                }
                if(nx >= 0 && ny >= 0 && nx < w && ny < h){
                    if(!visited[nx][ny] && getPixel(nx,ny) == old_v){
                        visited[nx][ny] = true;
                        queue.add(new Index2D(nx,ny));
                    }
                }
            }
        }
		return ans;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
		//Pixel2D[] ans = null;  // the result.
        if(p1 == null || p2 == null) return null;
        if(!isInside(p1) || !isInside(p2)) return null;
        if(getPixel(p1) == obsColor ||  getPixel(p2) == obsColor) return null;

        // if a start and a finish are the same
        if(p1.equals(p2)) return new Pixel2D[]{p1};

        int w = getWidth();
        int h = getHeight();

        // the structure of BFS
        boolean[][] visited = new boolean[w][h];
        Pixel2D[][] parent  = new Pixel2D[w][h];
        LinkedList<Pixel2D> queue = new LinkedList<>();

        // start
        queue.add(p1);
        visited[p1.getX()][p1.getY()] = true;

        boolean found = false;
        // directions : up, down, right, left
        int[][] dirs ={{0,1},{0,-1},{1,0}, {-1,0}};
        // BFS
        while(!queue.isEmpty()){
            Pixel2D p = queue.poll();
            if(p.equals(p2)){
                found = true;
                break;
            }
            int x = p.getX();
            int y = p.getY();

            for(int[] dir : dirs){
                int nx = x + dir[0];
                int ny = y + dir[1];
                if(_cyclicFlag){
                    nx = (nx + w) % w;
                    ny = (ny + h) % h;
                }
                if(nx >= 0 && ny >= 0 && nx < w && ny < h){
                    if(!visited[nx][ny] && getPixel(nx,ny) != obsColor){
                        visited[nx][ny] = true;
                        parent[nx][ny] = p;
                        queue.add(new Index2D(nx,ny));
                    }
                }
            }
        }
        if(!found) return null;

        LinkedList<Pixel2D> path = new LinkedList<>();
        Pixel2D step = p2;

        while(step != null){
            path.addFirst(step);
            if(step.equals(p1)) break;
            step = parent[step.getX()][step.getY()];
        }
        return path.toArray(new Pixel2D[0]);
	}
	@Override
	/////// add your code below ///////
	public boolean isInside(Pixel2D p) {
		if(p == null || _map == null) return false;
        int x = p.getX();
        int y = p.getY();
        return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
	}

	@Override
	/////// add your code below ///////
	public boolean isCyclic() {
		return _cyclicFlag;
	}
	@Override
	/////// add your code below ///////
	public void setCyclic(boolean cy) {
        this._cyclicFlag = cy;
    }
	@Override
	/////// add your code below ///////
	public Map2D allDistance(Pixel2D start, int obsColor) {
		Map2D ans = new Map(getWidth(),getHeight(), -1);  // the result.
		if(start == null || !isInside(start) || getPixel(start) == obsColor) {return ans;}

        int w = getWidth();
        int h = getHeight();

        LinkedList<Pixel2D> queue = new LinkedList<>();
        boolean[][] visited = new boolean[w][h];

        queue.add(start);
        visited[start.getX()][start.getY()] = true;
        ans.setPixel(start, 0);

        int[][] dirs = {{0,1},{0,-1},{1,0}, {-1,0}};

        while(!queue.isEmpty()){
            Pixel2D p = queue.poll();
            int currentDist = ans.getPixel(p);
            int x = p.getX();
            int y = p.getY();
            for(int[] dir : dirs){
                int nx = x + dir[0];
                int ny = y + dir[1];

                if(_cyclicFlag){
                    nx = (nx + w) % w;
                    ny = (ny + h) % h;
                }
                if(nx >= 0 && ny >= 0 && nx < w && ny < h){
                    if(!visited[nx][ny] && getPixel(nx,ny) != obsColor){
                        visited[nx][ny] = true;
                        ans.setPixel(nx,ny, currentDist+1);
                        queue.add(new Index2D(nx,ny));
                    }
                }
            }
        }
		return ans;
	}
}
