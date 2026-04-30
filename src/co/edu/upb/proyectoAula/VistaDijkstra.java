package co.edu.upb.proyectoAula;

import java.util.List;
import java.util.Map;

import co.edu.upb.proyectoAula.data_structures.Nodo;

public interface VistaDijkstra {
    void mostrarDijkstra(Map<Nodo, Integer> distancias,
                         Map<Nodo, Nodo>    anteriores,
                         List<Nodo>         camino);
}