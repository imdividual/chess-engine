package game.data;

public class RC {

    public int r, c;

    public RC(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public boolean equals(RC o) {
        return r == o.r && c == o.c;
    }

    public boolean equals(int x, int y) {
        return r == x && c == y;
    }

    public String toString() {
        return "(" + r + ", " + c + ")";
    }

}
