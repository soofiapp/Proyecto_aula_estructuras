package co.edu.upb.proyectoAula;

import java.util.*;

public class Kruskal {

    // Lista de aristas que forman el árbol de expansión mínima
    public static List<Arista> aristasArbol = new ArrayList<>();

    // Suma de los pesos de todas las aristas del árbol
    public static int pesoTotal = 0;

    // Estructura Union-Find: mapea cada nodo a su representante (padre)
    private static Map<Nodo, Nodo> padre = new HashMap<>();


    /**
     * Encuentra el representante (raíz) del conjunto al que pertenece N.
     * Debe implementarse con COMPRESIÓN DE CAMINOS para mayor eficiencia.
     *
     * ─────────────────────────────────────────────────────────────────
     * PSEUDOCÓDIGO — encontrar(N)
     * ─────────────────────────────────────────────────────────────────
     *
     * Si padre[N] != N:                     ← N no es su propia raíz
     *   padre[N] ← encontrar(padre[N])      ← comprimir el camino (recursión)
     * Retornar padre[N]
     *
     * NOTA — Compresión de caminos:
     *   Al hacer la llamada recursiva, acortamos la cadena de padres
     *   para que todos los nodos apunten directamente a la raíz.
     *   Esto mejora el rendimiento de búsquedas futuras.
     * ─────────────────────────────────────────────────────────────────
     */
    private static Nodo encontrar(Nodo n) {
        Nodo raiz = padre.get(n);
        if (raiz != n) {
            raiz = encontrar(raiz);
            padre.put(n, raiz);
        }
        return raiz;
    }


    /**
     * Une los conjuntos a los que pertenecen los nodos A y B.
     *
     * ─────────────────────────────────────────────────────────────────
     * PSEUDOCÓDIGO — unir(A, B)
     * ─────────────────────────────────────────────────────────────────
     *
     * raizA ← encontrar(A)
     * raizB ← encontrar(B)
     * padre[raizA] ← raizB      ← raizA pasa a ser hijo de raizB
     *
     * NOTA:
     *   Esto fusiona dos componentes en una sola.
     *   Después de unir(A, B), encontrar(A) == encontrar(B).
     * ─────────────────────────────────────────────────────────────────
     */
    private static void unir(Nodo a, Nodo b) {
    	 Nodo raizA = encontrar(a);
         Nodo raizB = encontrar(b);
         padre.put(raizA, raizB);
    }


    /**
     * Ejecuta el algoritmo de Kruskal para construir el MST.
     *
     * @param grafo El grafo con todos los nodos y aristas
     *
     * ─────────────────────────────────────────────────────────────────
     * PSEUDOCÓDIGO
     * ─────────────────────────────────────────────────────────────────
     *
     * PASO 1 — Inicializar Union-Find
     *   Para cada nodo N en el grafo:
     *     padre[N] ← N       ← cada nodo es su propio representante
     *
     * PASO 2 — Ordenar las aristas
     *   aristas ← copia de todas las aristas del grafo
     *   Ordenar aristas de MENOR a MAYOR según su peso
     *
     * PASO 3 — Selección de aristas (recorrer en orden ascendente)
     *   Para cada arista A en la lista ordenada:
     *     U ← A.origen
     *     V ← A.destino
     *
     *     Si encontrar(U) != encontrar(V):   ← U y V están en componentes distintas
     *       Agregar A a aristasArbol          ← incluir arista en el MST
     *       pesoTotal ← pesoTotal + A.peso   ← acumular el costo
     *       unir(U, V)                        ← fusionar ambas componentes
     *
     *     (Si encontrar(U) == encontrar(V) → agregar A crearía un ciclo → ignorar)
     *
     * RESULTADO:
     *   - aristasArbol → lista de aristas del árbol de expansión mínima
     *   - pesoTotal    → suma total del peso del árbol
     *
     * CONDICIÓN DE PARADA IMPLÍCITA:
     *   El MST de un grafo con V nodos tiene exactamente V-1 aristas.
     *   El algoritmo termina cuando se recorren todas las aristas.
     * ─────────────────────────────────────────────────────────────────
     */
    public static void ejecutar(Grafo grafo) {
        aristasArbol.clear();
        pesoTotal = 0;
        padre.clear();
        
        
        // ── PASO 1: Inicializar Union-Find ───────────────────────────
        //Asignar cada nodo como su propio padre

        for (Nodo nodo : grafo.getNodos()) {
            padre.put(nodo, nodo);
        }

        // ── PASO 2: Ordenar aristas por peso ────────────────────────
        //Crear una copia de las aristas del grafo y ordenarlas de menor a mayor usando Comparator.comparingInt(Arista::getPeso)
        
        List<Arista> aristasOrdenadas = new ArrayList<>(grafo.getAristas());
        aristasOrdenadas.sort(Comparator.comparingInt(Arista::getPeso));
        int aristasNecesarias = Math.max(0, grafo.getNodos().size() - 1);

        
        // ── PASO 3: Seleccionar aristas sin formar ciclos ────────────
        // Recorrer las aristas ordenadas
        
        for (Arista arista : aristasOrdenadas) {
            Nodo u = arista.getOrigen();
            Nodo v = arista.getDestino();

            if (encontrar(u) != encontrar(v)) {	//Para cada una, verificar si sus extremos están en componentes distintas (usar encontrar)

                aristasArbol.add(arista);					   //Si es así: agregar al árbol, sumar peso y unir componentes
                pesoTotal += arista.getPeso();
                unir(u, v);

                if (aristasArbol.size() == aristasNecesarias) {
                    break;
                }
            }
        }

    }
}