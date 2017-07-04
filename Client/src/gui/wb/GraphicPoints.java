package gui.wb;

import java.awt.*;
import java.util.*;

/**
 * Created by XHWhy on 2017/7/4.
 */
public class GraphicPoints {
    ArrayList<GraphicPoint> points = new ArrayList<>();
    ArrayList<GraphicClearRect> rects = new ArrayList<>();
    WhiteBoard.ScribblePanel panel ;
    class GraphicClearRect {
        int x1;
        int y1;
        int x2;
        int y2;
        public GraphicClearRect(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.x2 = y2;
            this.y1 = y1;
            this.y2 = y2;
        }
    }
    class GraphicPoint {
        public GraphicPoint(int x1, int y1, int x2, int y2, float pensize, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.pensize = pensize;
            this.color = color;
        }

        public boolean within(int x1, int y1, int x2, int y2) {
            int xmin = x1 < x2 ? x1 : x2;
            int xmax = x1 > x2 ? x1 : x2;
            int ymin = y1 < y2 ? y1 : y2;
            int ymax = y1 > y2 ? y1 : y2;
            if(this.x1 <= xmax && this.x1 >= xmin && this.x2 <= xmax && this.x2 >= xmin
                    && this.y1 <= ymax && this.y1 >= ymin && this.y2 <= ymax && this.y2 >= ymin)
                return true;
            else return false;
        }

        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public  float pensize;
        public Color color;
    }

    public GraphicPoints(WhiteBoard.ScribblePanel panel) {
        this.panel = panel;
    }
    public GraphicPoints() {}

    public void addPoint(int x1, int y1, int x2, int y2, float pensize, Color color) {
        points.add(new GraphicPoint(x1, y1, x2, y2, pensize, color));
    }

    public void addRect(int x1, int y1, int x2, int y2) {
        rects.add(new GraphicClearRect(x1, y1, x2, y2));
    }
    public void clearAll() {
        points.clear();
        rects.clear();
    }
    public void clear(int x1, int y1, int x2, int y2) {
        GraphicPoint point;
        for(int i = points.size()-1; i >= 0; i--) {
            point = points.get(i);
            if(point.within(x1, y1, x2, y2))
                points.remove(i);
        }
        rects.add(new GraphicClearRect(x1, y1, x2, y2));
    }
    public void draw(Graphics2D g, float penfix) {
        for(GraphicPoint point : points) {
            g.setColor(point.color);
            g.setStroke(new BasicStroke(point.pensize * penfix));
            g.drawLine(point.x1, point.y1, point.x2, point.y2);
        }
        for(GraphicClearRect rect : rects) {
            int x1 = rect.x1;
            int x2 = rect.x2;
            int y1 = rect.y1;
            int y2 = rect.y2;
            int x = x1 < x2 ? x1 : x2;
            int y = y1 < y2 ? y1 : y2;
            int width = Math.abs(x1-x2);
            int height = Math.abs(y1-y2);
            g.clearRect(x,y,width,height);
        }
    }
}