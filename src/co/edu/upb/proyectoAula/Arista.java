package co.edu.upb.proyectoAula;

public class Arista {
    private Nodo origen, destino;
    private int peso;

    public Arista(Nodo origen, Nodo destino, int peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public Nodo getOrigen() { 
    	return origen; 
    }
    public Nodo getDestino() { 
    	return destino; 
    }
    public int getPeso() { 
    	return peso; 
    }
}
