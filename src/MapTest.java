import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {
    private int[][] _map_3_3 = {{0,1,0}, {1,0,1}, {0,1,0}};
    private int[][] _map_2_5 = {{0,1,0,1,1}, {1,0,1,0,0}};
    private int[][] _map_3_4 = {{0,0,0,1,1}, {0,0,1,1,0},{0,1,1,0,0}};
    private Map2D  _m1, _m0;
    private Map2D _m4;
    private Map2D _m3_3;
    private Map2D _m2_5;
    private Map2D _m3_4;
    @BeforeEach
    public void setup() {
        _m0 = new Map(3,3,0);
        _m1 = new Map(3,3,0);
        _m4 = new Map(2,5,0);
        _m2_5 = new Map(_map_2_5);
        _m3_3 = new Map(_map_3_3);
        _m3_4 = new Map(_map_3_4);
    }

    @Test
    void init() {
        int[][] bigarr = new int [500][500];
        _m1.init(bigarr);
        assertEquals(bigarr.length, _m1.getWidth());
        assertEquals(bigarr[0].length, _m1.getHeight());
        bigarr[0][0] = 99;
        assertEquals(0, _m1.getPixel(0,0));
    }

    @Test
    void testInit() {
        Map map = new Map(5);
        assertThrows(RuntimeException.class, () -> map.init(null));
        assertThrows(RuntimeException.class, () -> map.init(new int[0][0]));

        int[][] ragged = {{1, 2}, {1}}; // Рваный массив
        assertThrows(RuntimeException.class, () -> map.init(ragged));
    }

    @Test
    void getMap() {
        int[][] lol = _m3_3.getMap();
        assertEquals(3, lol.length);
        assertEquals(3, lol[0].length);
        assertEquals(1, lol[0][1]);
        lol[0][1] = 99;
        assertNotEquals(99, _m3_3.getPixel(0,1));
    }

    @Test
    void getWidth() {
        assertEquals(3, _m3_3.getWidth());
        assertEquals(2, _m2_5.getWidth());
        assertNotEquals(0, _m3_4.getWidth());
        assertNotEquals(10, _m2_5.getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(3, _m3_3.getHeight());
        assertEquals(5, _m2_5.getHeight());
        assertNotEquals(0, _m3_4.getHeight());
        assertNotEquals(10, _m2_5.getHeight());
    }

    @Test
    void getPixel() {
        assertEquals(1, _m3_3.getPixel(0,1));
        assertEquals(0, _m3_3.getPixel(1,1));
        assertEquals(1, _m2_5.getPixel(0,3));
        assertNotEquals(-1, _m3_4.getPixel(0,3));
        assertNotEquals(-1, _m2_5.getPixel(1,3));
    }


    @Test
    void testGetPixel() {
        Pixel2D p = new Index2D(1,1);
        assertEquals(0, _m3_3.getPixel(p));

        Pixel2D lal = new Index2D(10,10);
        assertEquals(-1, _m3_3.getPixel(lal));
    }

    @Test
    void setPixel() {
        _m1.setPixel(0,0,5);
        assertEquals(5, _m1.getPixel(0,0));
        _m1.setPixel(1,1,10);
        assertEquals(10, _m1.getPixel(1,1));
        _m1.setPixel(-5,0,10);
    }

    @Test
    void testSetPixel() {
        Pixel2D p = new Index2D(1,2);
        _m1.setPixel(p,5);
        assertEquals(5, _m1.getPixel(1,2));
    }

    @Test
    void fill() {
        int[][] trap = {
                {1,1,1},
                {1,0,1},
                {1,1,1},
        };
        Map2D m = new Map(trap);
        int col = m.fill(new Index2D(1,1), 5);
        assertEquals(1, col);
        assertEquals(5, m.getPixel(1,1));
        assertEquals(1, m.getPixel(0,1));

        Map2D m2 = new Map(3,3,0);
        int col2 = m2.fill(new Index2D(0,0), 5);
        assertEquals(9, col2);
        assertEquals(5, m2.getPixel(2,2));
    }

    @Test
    void shortestPath() {
        Map2D map = new Map(5,5,0);
        Pixel2D p1 = new Index2D(0,0);
        Pixel2D p2 = new Index2D(0,2);

        Pixel2D[] path = map.shortestPath(p1,p2,1);
        assertEquals(3, path.length);
        assertEquals(p1, path[0]);
        assertEquals(p2, path[2]);

        map.setPixel(0,1,1);
        Pixel2D[] pathAvoid = map.shortestPath(p1,p2,1);
        assertEquals(4, pathAvoid.length);
        assertTrue(pathAvoid.length >3);

        map.setPixel(1,0,1);
        map.setPixel(1,1,1);
        map.setCyclic(false);
        assertNull(map.shortestPath(p1,p2,1));
    }

    @Test
    void isInside() {
        assertTrue(_m3_3.isInside(new Index2D(0,0)));
        assertTrue(_m3_3.isInside(new Index2D(2,2)));

        assertFalse(_m3_3.isInside(new Index2D(-1,0)));
        assertFalse(_m3_3.isInside(new Index2D(3,0)));
        assertFalse(_m3_3.isInside(null));
    }

    @Test
    void isCyclic() {
        assertTrue(_m3_3.isCyclic());
        assertTrue(_m1.isCyclic());
    }

    @Test
    void setCyclic() {
        _m1.setCyclic(false);
        assertFalse(_m1.isCyclic());

        //check effect on shorted path
        int[][] line = {{0}, {1}, {0}};
        Map2D mLine = new Map(line);

        Pixel2D s = new Index2D(0,0);
        Pixel2D e = new Index2D(2,0);

        //if cyclic is false so there is no way
        mLine.setCyclic(false);
        assertNull(mLine.shortestPath(s,e,1));

        //if cyclic is true so there is a way through the edge
        mLine.setCyclic(true);
        Pixel2D[] path = mLine.shortestPath(s,e,1);
        assertNotNull(path);
        assertEquals(2, path.length);
    }

    @Test
    void allDistance() {
        Map2D map = new Map(3,3,0);
        Pixel2D s = new Index2D(1,1);
        Map2D disM = map.allDistance(s,1);
        //for itself
        assertEquals(0,disM.getPixel(1,1));
        //for neighbor
        assertEquals(1,disM.getPixel(0,1));
        assertEquals(1,disM.getPixel(2,1));
        assertEquals(1,disM.getPixel(1,0));
        assertEquals(1,disM.getPixel(1,2));
        //for the angle
        assertEquals(2,disM.getPixel(0,0));


    }
}