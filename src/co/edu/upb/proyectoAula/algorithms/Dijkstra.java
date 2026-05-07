package co.edu.upb.proyectoAula.algorithms;

import java.util.*;

import co.edu.upb.proyectoAula.data_structures.Arista;
import co.edu.upb.proyectoAula.data_structures.Grafo;
import co.edu.upb.proyectoAula.data_structures.Nodo;
import co.edu.upb.proyectoAula.data_structures.ColaPrioridad;

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

        // ── PASO 1: Inicialización ───────────────────────────────────
        int capacidad = grafo.getNodos().size();
        ColaPrioridad cola = new ColaPrioridad(capacidad);

        for (Nodo n : grafo.getNodos()) {
            distancias.put(n, Integer.MAX_VALUE);
            anteriores.put(n, null);
        }
        distancias.put(origen, 0);
        cola.insertar(origen, 0); // solo inserta el origen con distancia 0

        // Inserta el resto con distancia infinita
        for (Nodo n : grafo.getNodos()) {
            if (n != origen) cola.insertar(n, Integer.MAX_VALUE);
        }

        // ── PASO 2: Bucle principal ──────────────────────────────────
        while (!cola.estaVacia()) {

            // 2a: Extraer el nodo con menor distancia — O(log n)
            Nodo actual = cola.extraerMinimo();

            if (actual == null || distancias.get(actual) == Integer.MAX_VALUE) break;
            if (actual == destino) break;

            // 2b: Relajar aristas
            for (Arista a : grafo.getAristas()) {
                Nodo vecino = null;
                if (a.getOrigen() == actual) vecino = a.getDestino();

                if (vecino != null && cola.contiene(vecino)) {
                    int nueva = distancias.get(actual) + a.getPeso();
                    if (nueva < distancias.get(vecino)) {
                        distancias.put(vecino, nueva);
                        anteriores.put(vecino, actual);
                        // Actualizar en el heap — O(log n)
                        cola.actualizarDistancia(vecino, nueva);
                    }
                }
            }
        }

        // ── PASO 3: Reconstrucción del camino ────────────────────────
        if (distancias.get(destino) != Integer.MAX_VALUE) {
            Nodo paso = destino;
            while (paso != null) {
                camino.add(0, paso);
                paso = anteriores.get(paso);
            }
        }
    }

}