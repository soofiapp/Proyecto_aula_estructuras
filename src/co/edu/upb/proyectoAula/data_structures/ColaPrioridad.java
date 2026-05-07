package co.edu.upb.proyectoAula.data_structures;

import java.util.Map;

public class ColaPrioridad {

    // Entrada del heap: nodo + su distancia en el momento de inserción
    private static class Entrada {
        Nodo   nodo;
        int    distancia;

        Entrada(Nodo nodo, int distancia) {
            this.nodo      = nodo;
            this.distancia = distancia;
        }
    }

    private Entrada[] heap;
    private int       tamaño;

    public ColaPrioridad(int capacidad) {
        heap   = new Entrada[capacidad];
        tamaño = 0;
    }

    // ── Insertar un nodo con su distancia ────────────────────────────
    public void insertar(Nodo nodo, int distancia) {
        heap[tamaño] = new Entrada(nodo, distancia);
        subirHeap(tamaño); // reubica hacia arriba para mantener el heap
        tamaño++;
    }

    // ── Extraer el nodo con menor distancia (la raíz) ────────────────
    public Nodo extraerMinimo() {
        if (tamaño == 0) return null;
        Nodo minimo = heap[0].nodo;

        // Mueve el último al inicio y baja para reordenar
        heap[0] = heap[tamaño - 1];
        tamaño--;
        bajarHeap(0);

        return minimo;
    }

    // ── Actualizar la distancia de un nodo ya insertado ──────────────
    public void actualizarDistancia(Nodo nodo, int nuevaDistancia) {
        for (int i = 0; i < tamaño; i++) {
            if (heap[i].nodo == nodo) {
                heap[i].distancia = nuevaDistancia;
                subirHeap(i);  // si bajó la distancia sube en el heap
                break;
            }
        }
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    public boolean contiene(Nodo nodo) {
        for (int i = 0; i < tamaño; i++) {
            if (heap[i].nodo == nodo) return true;
        }
        return false;
    }

    // ── Subir: compara con el padre y sube si es menor ───────────────
    private void subirHeap(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (heap[i].distancia < heap[padre].distancia) {
                intercambiar(i, padre);
                i = padre;
            } else {
                break;
            }
        }
    }

    // ── Bajar: compara con los hijos y baja si es mayor ──────────────
    private void bajarHeap(int i) {
        while (true) {
            int menor    = i;
            int hijoIzq  = 2 * i + 1;
            int hijoDer  = 2 * i + 2;

            if (hijoIzq < tamaño &&
                heap[hijoIzq].distancia < heap[menor].distancia) {
                menor = hijoIzq;
            }
            if (hijoDer < tamaño &&
                heap[hijoDer].distancia < heap[menor].distancia) {
                menor = hijoDer;
            }

            if (menor != i) {
                intercambiar(i, menor);
                i = menor;
            } else {
                break;
            }
        }
    }

    private void intercambiar(int i, int j) {
        Entrada temp = heap[i];
        heap[i]      = heap[j];
        heap[j]      = temp;
    }
}