package co.edu.upb.proyectoAula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrizAdyacencia {

    private final ArrayList<Nodo> nodos;
    private final HashMap<String, Integer> indice; // id del nodo -> posición en la matriz
    private final int[][] matriz;

    public MatrizAdyacencia(Grafo grafo) {
        this.nodos  = grafo.getNodos();
        this.indice = new HashMap<>();

        // 1. Mapear cada id de nodo a su índice
        for (int i = 0; i < nodos.size(); i++) {
            indice.put(nodos.get(i).getId(), i);
        }

        // 2. Crear matriz n x n inicializada en 0
        int n = nodos.size();
        this.matriz = new int[n][n];

        // 3. Llenar con los pesos de cada arista
        for (Arista arista : grafo.getAristas()) {
            String origenId  = arista.getOrigen().getId();
            String destinoId = arista.getDestino().getId();

            Integer i = indice.get(origenId);
            Integer j = indice.get(destinoId);

            if (i != null && j != null) {
                matriz[i][j] = arista.getPeso();
                matriz[j][i] = arista.getPeso(); // grafo no dirigido
            }
        }
    }

    // Peso entre dos nodos por id — retorna 0 si no hay arista
    public int getPeso(String origenId, String destinoId) {
        Integer i = indice.get(origenId);
        Integer j = indice.get(destinoId);
        if (i == null || j == null) return 0;
        return matriz[i][j];
    }

    // Vecinos directos de un nodo
    public ArrayList<Nodo> getVecinos(String idNodo) {
        ArrayList<Nodo> vecinos = new ArrayList<>();
        Integer i = indice.get(idNodo);
        if (i == null) return vecinos;

        for (int j = 0; j < nodos.size(); j++) {
            if (matriz[i][j] != 0) {
                vecinos.add(nodos.get(j));
            }
        }
        return vecinos;
    }

    // Getters
    public int[][]                  getMatriz()  { return matriz; }
    public ArrayList<Nodo>          getNodos()   { return nodos;  }
    public HashMap<String, Integer> getIndice()  { return indice; }

    // Imprimir en consola para verificar
    public void imprimir() {
        // Encabezado de columnas
        System.out.printf("%6s", "");
        for (Nodo n : nodos) {
            System.out.printf("%6s", n.getId());
        }
        System.out.println();

        // Filas
        for (int i = 0; i < nodos.size(); i++) {
            System.out.printf("%6s", nodos.get(i).getId());
            for (int j = 0; j < nodos.size(); j++) {
                System.out.printf("%6d", matriz[i][j]);
            }
            System.out.println();
        }
    }
}