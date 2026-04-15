package co.edu.upb.proyectoAula;

public class Nodo {
    private String id;
    private int x, y; // posición en pantalla

    public Nodo(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() { 
    	return id; 
    }
    public int getX() { 
    	return x;
    }
    public int getY() { 
    	return y;
    }
    public void setX(int x) { 
    	this.x = x; 
    }
    public void setY(int y) { 
    	this.y = y; 
    }
}