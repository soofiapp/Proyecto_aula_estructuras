package co.edu.upb.proyectoAula;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class PanelGrafo extends JPanel{
    private Grafo grafo;
    private Nodo nodoSeleccionado = null;
    private static final int RADIO = 15;

    private List<Nodo>   caminoDijkstra  = null;
    private List<Nodo>   caminoAEstrella = null;
    private List<Nodo>   ordenRecorrido  = null;
    private List<Arista> aristasKruskal  = null;
    private int          aristasAnimadas = 0;
    private int          nodoAnimado     = -1;
    private int          distanciaTotal  = 0;
    private String       modoActual      = "";

    private Timer timerAnimacion;

    public PanelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (timerAnimacion != null) timerAnimacion.stop();
                Nodo encontrado = buscarNodo(e.getX(), e.getY());
                if (encontrado == null) {
                    String id = JOptionPane.showInputDialog("Nombre del nodo:");
                    if (id != null && !id.trim().isEmpty()) {
                        grafo.agregarNodo(new Nodo(id.trim(), e.getX(), e.getY()));
                        repaint();
                    }
                } else {
                    if (nodoSeleccionado == null) {
                        nodoSeleccionado = encontrado;
                        repaint();
                    } else if (nodoSeleccionado != encontrado) {
                        String input = JOptionPane.showInputDialog(
                            "Peso entre " + nodoSeleccionado.getId() +
                            " y " + encontrado.getId() + ":");
                        if (input != null) {
                            try {
                                grafo.agregarArista(nodoSeleccionado, encontrado,
                                    Integer.parseInt(input.trim()));
                                nodoSeleccionado = null;
                                repaint();
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Número inválido.");
                            }
                        } else {
                            nodoSeleccionado = null;
                            repaint();
                        }
                    } else {
                        nodoSeleccionado = null;
                        repaint();
                    }
                }
            }
        });
    }

    // ── Dijkstra 
    public void ejecutarDijkstra(PanelTabla panelTabla) {
        limpiar();
        List<Nodo> nodos = grafo.getNodos();
        if (nodos.size() < 2) {
            JOptionPane.showMessageDialog(this, "Necesitas al menos 2 nodos."); return;
        }
        String[] nombres = nodos.stream().map(Nodo::getId).toArray(String[]::new);
        String origenId  = (String) JOptionPane.showInputDialog(this,
            "Nodo ORIGEN:", "Dijkstra", JOptionPane.PLAIN_MESSAGE,
            null, nombres, nombres[0]);
        if (origenId == null) return;
        String destinoId = (String) JOptionPane.showInputDialog(this,
            "Nodo DESTINO:", "Dijkstra", JOptionPane.PLAIN_MESSAGE,
            null, nombres, nombres[nombres.length - 1]);
        if (destinoId == null) return;

        Nodo origen  = buscarPorId(origenId);
        Nodo destino = buscarPorId(destinoId);
        if (origen == destino) {
            JOptionPane.showMessageDialog(this, "Origen y destino iguales."); return;
        }
        Dijkstra.ejecutar(grafo, origen, destino);
        caminoDijkstra = Dijkstra.camino;
        distanciaTotal = Dijkstra.distancias.get(destino);
        modoActual     = "DIJKSTRA";
        if (caminoDijkstra.size() <= 1) {
            JOptionPane.showMessageDialog(this, "No existe camino.");
            caminoDijkstra = null;
        }
        panelTabla.mostrarDijkstra(Dijkstra.distancias, Dijkstra.anteriores, caminoDijkstra);
        repaint();
    }

    // ── Kruskal 
    public void ejecutarKruskal(PanelKruskal panelKruskal) {
        limpiar();
        if (grafo.getNodos().size() < 2) {
            JOptionPane.showMessageDialog(this, "Necesitas al menos 2 nodos."); return;
        }
        Kruskal.ejecutar(grafo);
        aristasKruskal  = new ArrayList<>(Kruskal.aristasArbol);
        aristasAnimadas = 0;
        modoActual      = "KRUSKAL";
        panelKruskal.mostrarKruskal(aristasKruskal, Kruskal.pesoTotal);
        animarKruskal();
    }
    
    // ── Animaciones ───────────────────────────────────────────
    private void animarRecorrido() {
        nodoAnimado = 0;
        timerAnimacion = new Timer(600, null);
        timerAnimacion.addActionListener(e -> {
            nodoAnimado++;
            repaint();
            if (nodoAnimado >= ordenRecorrido.size()) timerAnimacion.stop();
        });
        timerAnimacion.start();
        repaint();
    }

    private void animarKruskal() {
        aristasAnimadas = 0;
        timerAnimacion  = new Timer(700, null);
        timerAnimacion.addActionListener(e -> {
            aristasAnimadas++;
            repaint();
            if (aristasAnimadas >= aristasKruskal.size()) timerAnimacion.stop();
        });
        timerAnimacion.start();
        repaint();
    }

    public void limpiar() {
        if (timerAnimacion != null) timerAnimacion.stop();
        caminoDijkstra  = null;
        caminoAEstrella = null;
        ordenRecorrido  = null;
        aristasKruskal  = null;
        aristasAnimadas = 0;
        nodoAnimado     = -1;
        modoActual      = "";
        repaint();
    }

    private Nodo pedirOrigen(String algo) {
        List<Nodo> nodos = grafo.getNodos();
        if (nodos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay nodos."); return null;
        }
        String[] nombres = nodos.stream().map(Nodo::getId).toArray(String[]::new);
        String id = (String) JOptionPane.showInputDialog(this,
            "Nodo de inicio para " + algo + ":", algo,
            JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        return id != null ? buscarPorId(id) : null;
    }

    private Nodo buscarPorId(String id) {
        return grafo.getNodos().stream()
            .filter(n -> n.getId().equals(id)).findFirst().orElse(null);
    }

    private Nodo buscarNodo(int x, int y) {
        for (Nodo n : grafo.getNodos()) {
            int dx = n.getX() - x, dy = n.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) <= RADIO) return n;
        }
        return null;
    }

    private boolean aristaEnCamino(Arista a, List<Nodo> camino) {
        if (camino == null || camino.size() < 2) return false;
        for (int i = 0; i < camino.size() - 1; i++) {
            Nodo u = camino.get(i), v = camino.get(i + 1);
            if ((a.getOrigen() == u && a.getDestino() == v) ||
                (a.getOrigen() == v && a.getDestino() == u)) return true;
        }
        return false;
    }

    private boolean aristaEnKruskal(Arista a) {
        if (aristasKruskal == null) return false;
        int limite = Math.min(aristasAnimadas, aristasKruskal.size());
        for (int i = 0; i < limite; i++) {
            Arista k = aristasKruskal.get(i);
            if ((k.getOrigen() == a.getOrigen() && k.getDestino() == a.getDestino()) ||
                (k.getOrigen() == a.getDestino() && k.getDestino() == a.getOrigen()))
                return true;
        }
        return false;
    }

    // ── Pintar ────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ── Aristas ───────────────────────────────────────────
        for (Arista a : grafo.getAristas()) {
            int x1 = a.getOrigen().getX(),  y1 = a.getOrigen().getY();
            int x2 = a.getDestino().getX(), y2 = a.getDestino().getY();
            int mx = (x1 + x2) / 2, my = (y1 + y2) / 2;

            boolean enDijkstra  = aristaEnCamino(a, caminoDijkstra);
            boolean enAEstrella = aristaEnCamino(a, caminoAEstrella);
            boolean enKruskal   = aristaEnKruskal(a);

            Color colorArista; float grosor;
            if      (enDijkstra)  { colorArista = new Color(34, 180, 90);   grosor = 5.5f; }
            else if (enAEstrella) { colorArista = new Color(0, 160, 220);   grosor = 5.5f; }
            else if (enKruskal)   { colorArista = new Color(160, 80, 200);  grosor = 5.5f; }
            else                  { colorArista = Color.GRAY;               grosor = 5.5f; }

            g2.setColor(colorArista);
            g2.setStroke(new BasicStroke(grosor));
            g2.drawLine(x1, y1, x2, y2);

            Color colorPeso = enDijkstra  ? new Color(20, 140, 60)
                            : enAEstrella ? new Color(0, 110, 180)
                            : enKruskal   ? new Color(120, 40, 180)
                            : new Color(180, 50, 50);
            g2.setColor(Color.WHITE);
            //g2.fillOval(mx - 12, my - 12, 24, 24);
            g2.setColor(colorPeso);
            g2.setStroke(new BasicStroke(1.5f));
            // g2.drawOval(mx - 12, my - 12, 24, 24);
            // g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String ps = String.valueOf(a.getPeso());
            // g2.drawString(ps, mx - fm.stringWidth(ps) / 2, my + 5);
        }

        // ── Nodos ─────────────────────────────────────────────
        g2.setStroke(new BasicStroke(2));
        for (Nodo n : grafo.getNodos()) {
            Color relleno = new Color(100, 149, 237);

            switch (modoActual) {
                case "DIJKSTRA":
                    if (caminoDijkstra != null) {
                        if (n == caminoDijkstra.get(0))
                            relleno = new Color(34, 180, 90);
                        else if (n == caminoDijkstra.get(caminoDijkstra.size() - 1))
                            relleno = new Color(220, 60, 60);
                        else if (caminoDijkstra.contains(n))
                            relleno = new Color(255, 200, 0);
                    }
                    break;

                case "BFS": case "DFS":
                    if (ordenRecorrido != null) {
                        int idx = ordenRecorrido.indexOf(n);
                        if (idx >= 0 && idx < nodoAnimado) {
                            float t = (float) idx / ordenRecorrido.size();
                            relleno = new Color(
                                (int)(100 + 155 * t),
                                (int)(149 - 100 * t),
                                (int)(237 - 180 * t));
                        } else if (idx == nodoAnimado) {
                            relleno = new Color(255, 200, 0);
                        }
                    }
                    break;

                case "KRUSKAL":
                    if (aristasKruskal != null) {
                        int limite = Math.min(aristasAnimadas, aristasKruskal.size());
                        for (int i = 0; i < limite; i++) {
                            Arista k = aristasKruskal.get(i);
                            if (k.getOrigen() == n || k.getDestino() == n) {
                                relleno = new Color(190, 120, 230); break;
                            }
                        }
                    }
                    break;

                case "AESTRELLA":
                    if (caminoAEstrella != null) {
                        int idx = caminoAEstrella.indexOf(n);
                        if (idx == 0)
                            relleno = new Color(34, 180, 90);   // origen: verde
                        else if (idx == caminoAEstrella.size() - 1)
                            relleno = new Color(220, 60, 60);   // destino: rojo
                        else if (idx > 0 && idx < nodoAnimado)
                            relleno = new Color(0, 180, 230);   // animado: azul cyan
                        else if (idx == nodoAnimado)
                            relleno = new Color(255, 200, 0);   // actual: amarillo
                    }
                    break;
            }

            if (n == nodoSeleccionado) relleno = new Color(255, 165, 0);

            g2.setColor(relleno);
            g2.fillOval(n.getX() - RADIO, n.getY() - RADIO, RADIO * 2, RADIO * 2);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(n.getX() - RADIO, n.getY() - RADIO, RADIO * 2, RADIO * 2);

            // Número de orden BFS/DFS
            if ((modoActual.equals("BFS") || modoActual.equals("DFS"))
                    && ordenRecorrido != null) {
                int idx = ordenRecorrido.indexOf(n);
                if (idx >= 0 && idx < nodoAnimado) {
                    g2.setColor(new Color(255, 255, 200));
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    g2.drawString(String.valueOf(idx + 1),
                        n.getX() + RADIO - 10, n.getY() - RADIO + 12);
                }
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(n.getId(),
                n.getX() - fm.stringWidth(n.getId()) / 2, n.getY() + 5);
        }

        // ── Barra de estado ───────────────────────────────────
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        switch (modoActual) {
            case "DIJKSTRA":
                if (caminoDijkstra != null) {
                    StringBuilder sb = new StringBuilder("Dijkstra — Camino: ");
                    for (int i = 0; i < caminoDijkstra.size(); i++) {
                        sb.append(caminoDijkstra.get(i).getId());
                        if (i < caminoDijkstra.size() - 1) sb.append(" → ");
                    }
                    sb.append("   |   Distancia: ").append(distanciaTotal);
                    g2.setColor(new Color(30, 130, 60));
                    g2.drawString(sb.toString(), 10, getHeight() - 10);
                }
                break;

            case "KRUSKAL":
                if (aristasKruskal != null) {
                    int lim = Math.min(aristasAnimadas, aristasKruskal.size());
                    int pesoAcum = aristasKruskal.subList(0, lim)
                        .stream().mapToInt(Arista::getPeso).sum();
                    g2.setColor(new Color(120, 40, 180));
                    g2.drawString("Kruskal — Aristas: " + lim + "/" +
                        aristasKruskal.size() + "   |   Peso acumulado: " + pesoAcum,
                        10, getHeight() - 10);
                }
                break;

            default:
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString(nodoSeleccionado == null
                    ? "Clic en vacío: crear nodo  |  Clic en nodo: seleccionar"
                    : "Seleccionado: " + nodoSeleccionado.getId() +
                      "  →  Clic en otro nodo para conectar",
                    10, getHeight() - 10);
        }
    }
}
