package co.edu.upb.proyectoAula;

import java.util.*;

public class Dijkstra {

    // Almacena la distancia mínima conocida desde el origen a cada nodo
    public static Map<Nodo, Integer> distancias = new HashMap<>();

    // Almacena el nodo anterior en el camino óptimo hacia cada nodo
    public static Map<Nodo, Nodo> anteriores = new HashMap<>();

    // Lista ordenada de nodos que forman el camino óptimo origen → destino
    public static List<Nodo> camino = new ArrayList<>();


    /**
     * Ejecuta el algoritmo de Dijkstra.
     *
     * @param grafo   El grafo con todos los nodos y aristas
     * @param origen  Nodo desde donde parte la búsqueda
     * @param destino Nodo al que se quiere llegar
     *
     * ─────────────────────────────────────────────────────────────────
     * PSEUDOCÓDIGO
     * ─────────────────────────────────────────────────────────────────
     *
     * PASO 1 — Inicialización
     *   Para cada nodo N en el grafo:
     *     distancias[N]  ← INFINITO  (Integer.MAX_VALUE)
     *     anteriores[N]  ← null
     *   distancias[origen] ← 0
     *   pendientes ← copia de todos los nodos del grafo
     *
     * PASO 2 — Bucle principal  (mientras haya nodos en pendientes)
     *   2a. Seleccionar el nodo ACTUAL con la MENOR distancia
     *       dentro de la lista "pendientes".
     *       → Si ACTUAL es null  O  su distancia es INFINITO → salir del bucle
     *       → Si ACTUAL == destino                           → salir del bucle
     *
     *   2b. Eliminar ACTUAL de la lista de pendientes.
     *
     *   2c. Para cada arista A en el grafo:
     *         Determinar si la arista conecta con ACTUAL:
     *           - Si A.origen  == ACTUAL  →  vecino = A.destino
     *           - Si A.destino == ACTUAL  →  vecino = A.origen   ← (grafo no dirigido)
     *
     *         Si VECINO todavía está en pendientes:
     *           nuevaDist ← distancias[ACTUAL] + A.peso
     *           Si nuevaDist < distancias[VECINO]:
     *             distancias[VECINO] ← nuevaDist
     *             anteriores[VECINO] ← ACTUAL
     *
     * PASO 3 — Reconstrucción del camino (de destino hacia origen)
     *   paso ← destino
     *   Mientras paso != null:
     *     Insertar paso al INICIO de la lista "camino"
     *     paso ← anteriores[paso]
     *
     * RESULTADO:
     *   - distancias → mapa con el costo mínimo a cada nodo
     *   - anteriores → árbol de predecesores del camino óptimo
     *   - camino     → secuencia de nodos del camino más corto
     * ─────────────────────────────────────────────────────────────────
     */
    
    public static void ejecutar(Grafo grafo, Nodo origen, Nodo destino) {
        distancias.clear();
        anteriores.clear();
        camino.clear();

        List<Nodo> pendientes = new ArrayList<>(grafo.getNodos());

        // ── PASO 1: Inicialización
        // Asignar distancia INFINITO a todos los nodos
        // Asignar null como nodo anterior para todos los nodos
        // Asignar distancia 0 al nodo origen
        // Crear la lista de nodos pendientes con todos los nodos del grafo
        
        for (Nodo n : grafo.getNodos()) {
            distancias.put(n, Integer.MAX_VALUE);
            anteriores.put(n, null);
        }
        distancias.put(origen, 0);

        // ── PASO 2: Bucle principal
        // Repetir mientras haya nodos en pendientes:
        	//2a: Encontrar el nodo con menor distancia en pendientes
        // recorrer todos los pendientes comparando distancias

        //Condición de parada — salir si no hay nodo alcanzable o si ya llegamos al destino


            //2b: Eliminar el nodo actual de pendientes


            // 2c: Revisar cada arista del grafo Para cada arista, identificar si conecta con el nodo actual
            // y si el vecino aún está en pendientes, relajar la distancia


        while (!pendientes.isEmpty()) {
            Nodo actual = null;
            int menor = Integer.MAX_VALUE;

            for (Nodo n : pendientes) {
                int d = distancias.get(n);
                if (d < menor) {
                    menor = d;
                    actual = n;
                }
            }

            if (actual == null || menor == Integer.MAX_VALUE) break; // no alcanzable
            if (actual == destino) break; // primer destino encontrado con costo mínimo

            pendientes.remove(actual);

            for (Arista a : grafo.getAristas()) {
                Nodo vecino = null;
                if (a.getOrigen() == actual) vecino = a.getDestino();
                else if (a.getDestino() == actual) vecino = a.getOrigen();

                if (vecino != null && pendientes.contains(vecino)) {
                    int nueva = distancias.get(actual) + a.getPeso();
                    if (nueva < distancias.get(vecino)) {
                        distancias.put(vecino, nueva);
                        anteriores.put(vecino, actual);
                    }
                }
            }
        }

        // ── PASO 3: Reconstrucción del camino
        //Recorrer el mapa "anteriores" desde destino hasta origen e insertar cada nodo al inicio de la lista "camino"

        if (distancias.get(destino) != Integer.MAX_VALUE) {
            Nodo paso = destino;
            while (paso != null) {
                camino.add(0, paso);
                paso = anteriores.get(paso);
            }
        }
    }

}