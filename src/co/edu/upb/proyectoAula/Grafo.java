package co.edu.upb.proyectoAula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grafo {
	private Map <Integer, List<Integer>> adyacencia;

	public Grafo() {
		this.adyacencia = new HashMap<>();
	}
	
	public void addNode (int origen) {
		if (adyacencia.containsKey(origen) == false) {
			adyacencia.put(origen, new ArrayList<>());
		}
	}
	
	public void addEdge(int origen, int destino) {
		addNode(origen);
		addNode(destino);
		adyacencia.get(destino).add(origen);
		adyacencia.get(origen).add(destino);
	}
	
	public void showGraph() {
		for (int nodo : adyacencia.keySet()) {
			System.out.println(nodo + "-->" + adyacencia.get(nodo));
		}
	}
	
}
