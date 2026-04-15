package co.edu.upb.proyectoAula;

import java.util.ArrayList;

public class Grafo {
	private ArrayList<Nodo> nodos = new ArrayList<>();
    private ArrayList<Arista> aristas = new ArrayList<>();

    public void agregarNodo(Nodo n) {
        nodos.add(n);
    }

    public void agregarArista(Nodo a, Nodo b, int peso) {
        aristas.add(new Arista(a, b, peso));
    }

    public ArrayList<Nodo> getNodos() { 
    	return nodos; 
    }
    
    public ArrayList<Arista> getAristas() { 
    	return aristas; 
    }
}
