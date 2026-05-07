package co.edu.upb.proyectoAula.views;

import javax.swing.*;
import javax.swing.border.*;

import co.edu.upb.proyectoAula.VistaDijkstra;
import co.edu.upb.proyectoAula.VistaKruskal;
import co.edu.upb.proyectoAula.algorithms.Dijkstra;
import co.edu.upb.proyectoAula.algorithms.Kruskal;
import co.edu.upb.proyectoAula.data_structures.Arista;
import co.edu.upb.proyectoAula.data_structures.Grafo;
import co.edu.upb.proyectoAula.data_structures.Nodo;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.awt.geom.AffineTransform;

public class PanelGrafo extends JPanel {

    // ── Paleta ───────────────────────────────────────────────────────
    private static final Color BG_CANVAS  = new Color(10,  12,  20);
    private static final Color BG_TOOLBAR = new Color(14,  18,  32);
    private static final Color GRID_LINE  = new Color(255, 255, 255, 8);
    private static final Color CIAN       = new Color(0,  200, 180);
    private static final Color VERDE      = new Color(34, 180,  90);
    private static final Color PURP       = new Color(160, 80,  220);
    private static final Color TEXTO      = new Color(220, 230, 255);
    private static final Color TEXTO_DIM  = new Color(130, 145, 175);
    private static final Color BORDE      = new Color( 50,  65, 100);
    private static final Color NODO_DEF   = new Color( 40,  90, 180);
    private static final Color NODO_SEL   = new Color(255, 165,   0);
    private static final Color BLACK      = new Color(0, 0,   0);

    private static final int RADIO = 15;

    // ── Estado ───────────────────────────────────────────────────────
    private Grafo grafo;
    private Nodo  nodoSeleccionado = null;

    private List<Nodo>   caminoDijkstra = null;
    private List<Arista> aristasKruskal = null;
    private int          aristasAnimadas = 0;
    private int          distanciaTotal  = 0;
    private String       modoActual      = "";

    private Timer  timerAnimacion;
    private double zoom    = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    // ── Animación del bus ────────────────────────────────────────────
    private Timer   timerBus;
    private int     busSegmento = 0;
    private double  busT        = 0.0;
    private double  busX        = 0;
    private double  busY        = 0;
    private boolean busActivo   = false;

    private JPanel barraBotones;
    private JPanel canvas;
    private JLabel lblZoom;
    private JLabel lblStatus;

    public PanelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setLayout(new BorderLayout());
        setBackground(BG_CANVAS);

        // ── Barra de herramientas ────────────────────────────────────
        barraBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_TOOLBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDE);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        barraBotones.setOpaque(false);

        JButton btnMenos   = mkBtn("−", new Color(40,50,80), new Color(60,75,110));
        JButton btnMas     = mkBtn("+", new Color(40,50,80), new Color(60,75,110));
        JButton btnCentrar = mkBtn("⌖ Centrar", CIAN.darker(), CIAN.darker().darker());

        lblZoom = new JLabel("100%");
        lblZoom.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblZoom.setForeground(CIAN);
        lblZoom.setBorder(new EmptyBorder(0, 6, 0, 6));

        lblStatus = new JLabel("Clic en nodo para seleccionar  ·  Clic en espacio vacío para agregar");
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblStatus.setForeground(TEXTO_DIM);

        btnMas.addActionListener(e -> aplicarZoom(zoom * 1.2));
        btnMenos.addActionListener(e -> aplicarZoom(zoom / 1.2));
        btnCentrar.addActionListener(e -> { zoom = 1.0; offsetX = 0; offsetY = 0;
            lblZoom.setText("100%"); canvas.repaint(); });

        // Separador visual
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 22));
        sep.setForeground(BORDE);

        barraBotones.add(btnMenos);
        barraBotones.add(lblZoom);
        barraBotones.add(btnMas);
        barraBotones.add(Box.createHorizontalStrut(4));
        barraBotones.add(sep);
        barraBotones.add(Box.createHorizontalStrut(4));
        barraBotones.add(btnCentrar);
        barraBotones.add(Box.createHorizontalStrut(16));
        barraBotones.add(lblStatus);

        // ── Canvas ───────────────────────────────────────────────────
        canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar(g);
            }
        };
        canvas.setBackground(BG_CANVAS);

        add(barraBotones, BorderLayout.NORTH);
        add(canvas,       BorderLayout.CENTER);

        // ── Eventos de ratón ─────────────────────────────────────────
        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (timerAnimacion != null) timerAnimacion.stop();
                int[] gc  = pantallaAGrafo(e.getX(), e.getY());
                Nodo found = buscarNodo(gc[0], gc[1]);
                if (found == null) {
                    String id = mostrarDialogoTexto(canvas, "Nombre del nodo:", "Nuevo nodo");
                    if (id != null && !id.trim().isEmpty()) {
                        grafo.agregarNodo(new Nodo(id.trim(), gc[0], gc[1]));
                        canvas.repaint();
                    }
                } else {
                    if (nodoSeleccionado == null) {
                        nodoSeleccionado = found;
                        lblStatus.setText("Seleccionado: " + found.getId() +
                            "  ·  Clic en otro nodo para conectar");
                        canvas.repaint();
                    } else if (nodoSeleccionado != found) {
                        String input = mostrarDialogoTexto(canvas,
                            "Peso de la arista  " + nodoSeleccionado.getId() +
                            "  ←→  " + found.getId() + " :", "Nueva arista");
                        if (input != null) {
                            try {
                                grafo.agregarArista(nodoSeleccionado, found,
                                    Integer.parseInt(input.trim()));
                            } catch (NumberFormatException ex) {
                                mostrarDialogoError(canvas, "Número inválido.");
                            }
                        }
                        nodoSeleccionado = null;
                        lblStatus.setText("Clic en nodo para seleccionar  ·  Clic en espacio vacío para agregar");
                        canvas.repaint();
                    } else {
                        nodoSeleccionado = null;
                        lblStatus.setText("Clic en nodo para seleccionar  ·  Clic en espacio vacío para agregar");
                        canvas.repaint();
                    }
                }
                canvas.requestFocusInWindow();
            }
        });

        canvas.addMouseWheelListener(e -> {
            if (e.isControlDown()) aplicarZoom(e.getWheelRotation() < 0 ? zoom * 1.1 : zoom / 1.1);
            else if (e.isShiftDown()) { offsetX -= e.getWheelRotation() * 30; canvas.repaint(); }
            else                      { offsetY -= e.getWheelRotation() * 30; canvas.repaint(); }
        });

        canvas.setFocusable(true);
        canvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:       offsetY += 40; canvas.repaint(); break;
                    case KeyEvent.VK_DOWN:     offsetY -= 40; canvas.repaint(); break;
                    case KeyEvent.VK_LEFT:     offsetX += 40; canvas.repaint(); break;
                    case KeyEvent.VK_RIGHT:    offsetX -= 40; canvas.repaint(); break;
                    case KeyEvent.VK_HOME:     zoom=1; offsetX=offsetY=0;
                        lblZoom.setText("100%"); canvas.repaint(); break;
                    case KeyEvent.VK_ADD:
                    case KeyEvent.VK_PLUS:     aplicarZoom(zoom * 1.2); break;
                    case KeyEvent.VK_SUBTRACT:
                    case KeyEvent.VK_MINUS:    aplicarZoom(zoom / 1.2); break;
                }
            }
        });
    }

    // ── Coordenadas ──────────────────────────────────────────────────
    private int[] pantallaAGrafo(int px, int py) {
        double[] c  = centroGrafo();
        int gx = (int)((px - canvas.getWidth()/2.0  - offsetX) / zoom + c[0]);
        int gy = (int)((py - canvas.getHeight()/2.0 - offsetY) / zoom + c[1]);
        return new int[]{gx, gy};
    }

    private double[] centroGrafo() {
        List<Nodo> ns = grafo.getNodos();
        if (ns.isEmpty()) return new double[]{canvas.getWidth()/2.0, canvas.getHeight()/2.0};
        double minX = ns.stream().mapToInt(Nodo::getX).min().getAsInt();
        double maxX = ns.stream().mapToInt(Nodo::getX).max().getAsInt();
        double minY = ns.stream().mapToInt(Nodo::getY).min().getAsInt();
        double maxY = ns.stream().mapToInt(Nodo::getY).max().getAsInt();
        return new double[]{(minX+maxX)/2.0, (minY+maxY)/2.0};
    }

    private void aplicarZoom(double z) {
        zoom = Math.max(0.15, Math.min(z, 6.0));
        lblZoom.setText((int)(zoom*100) + "%");
        canvas.repaint();
    }

    // ── Dibujo ───────────────────────────────────────────────────────
    private void dibujar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int pw = canvas.getWidth(), ph = canvas.getHeight();

        // Cuadrícula decorativa
        g2.setColor(GRID_LINE);
        for (int x = 0; x < pw; x += 32) g2.drawLine(x, 0, x, ph);
        for (int y = 0; y < ph; y += 32) g2.drawLine(0, y, pw, y);

        double[] centro = centroGrafo();
        AffineTransform original = g2.getTransform();
        g2.translate(pw/2.0 + offsetX, ph/2.0 + offsetY);
        g2.scale(zoom, zoom);
        g2.translate(-centro[0], -centro[1]);

        // ── Aristas ──────────────────────────────────────────────────
        Map<String, List<Arista>> grupos = new LinkedHashMap<>();
        for (Arista a : grafo.getAristas()) {
            String key = menorId(a.getOrigen().getId(), a.getDestino().getId()) 
                       + "_" 
                       + mayorId(a.getOrigen().getId(), a.getDestino().getId());
            grupos.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(a);
        }

        for (List<Arista> grupo : grupos.values()) {
            Arista a = grupo.get(0);
            
            if (a.getOrigen() == null || a.getDestino() == null) continue;
            
            int x1 = a.getOrigen().getX(),  y1 = a.getOrigen().getY();
            int x2 = a.getDestino().getX(), y2 = a.getDestino().getY();

            boolean enDij = grupo.stream().anyMatch(ar -> aristaEnCamino(ar, caminoDijkstra));
            boolean enKru = grupo.stream().anyMatch(ar -> aristaEnKruskal(ar));

            Color color;
            float grosor;
            if      (enDij) { color = VERDE;               grosor = 5f;   }
            else if (enKru) { color = PURP;                grosor = 5f;   }
            else            { color = new Color(40,55,90); grosor = 1.5f; }

            // Resplandor
            if (enDij || enKru) {
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                g2.setStroke(new BasicStroke(grosor+6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setColor(color);
            g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x1, y1, x2, y2);

            // Construir etiqueta con todos los pesos del grupo
            StringBuilder sb = new StringBuilder();
            long distinct = grupo.stream().mapToInt(ar -> ar.getPeso()).distinct().count();
            if (distinct == 1) {
                sb.append(grupo.get(0).getPeso());
            } else {
                for (int i = 0; i < grupo.size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append(grupo.get(i).getPeso());
                }
            }
            String pesoLabel = sb.toString();

            int mx = (x1+x2)/2, my = (y1+y2)/2;
            g2.setFont(new Font("Monospaced", Font.BOLD, 9));
            FontMetrics fmP = g2.getFontMetrics();
            int pw2 = fmP.stringWidth(pesoLabel);
            g2.setColor(new Color(10, 12, 20, 200));
            g2.fillRoundRect(mx-pw2/2-3, my-10, pw2+6, 13, 4, 4);
            g2.setColor(enDij||enKru ? color.brighter() : TEXTO_DIM);
            g2.drawString(pesoLabel, mx-pw2/2, my);
        }

        // ── Nodos ────────────────────────────────────────────────────
        g2.setStroke(new BasicStroke(2));
        for (Nodo n : grafo.getNodos()) {
            Color relleno = NODO_DEF;

            if ("DIJKSTRA".equals(modoActual) && caminoDijkstra != null) {
                if (n == caminoDijkstra.get(0))
                    relleno = VERDE;
                else if (n == caminoDijkstra.get(caminoDijkstra.size()-1))
                    relleno = new Color(220, 60, 60);
                else if (caminoDijkstra.contains(n))
                    relleno = new Color(255, 190, 30);
            } else if ("KRUSKAL".equals(modoActual) && aristasKruskal != null) {
                int lim = Math.min(aristasAnimadas, aristasKruskal.size());
                for (int i = 0; i < lim; i++) {
                    Arista k = aristasKruskal.get(i);
                    if (k.getOrigen()==n || k.getDestino()==n) { relleno = PURP; break; }
                }
            }

            if (n == nodoSeleccionado) relleno = NODO_SEL;

            // Resplandor del nodo
            Color glow = new Color(relleno.getRed(), relleno.getGreen(), relleno.getBlue(), 60);
            g2.setColor(glow);
            g2.fillOval(n.getX()-RADIO-5, n.getY()-RADIO-5, (RADIO+5)*2, (RADIO+5)*2);

            // Relleno con gradiente interno
            g2.setColor(relleno.darker());
            g2.fillOval(n.getX()-RADIO, n.getY()-RADIO, RADIO*2, RADIO*2);

            // Highlight superior sutil
            g2.setColor(new Color(255,255,255,30));
            g2.fillOval(n.getX()-RADIO+3, n.getY()-RADIO+3, RADIO-2, RADIO/2);

            // Borde del nodo
            g2.setColor(relleno.brighter());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(n.getX()-RADIO, n.getY()-RADIO, RADIO*2, RADIO*2);

            // Texto del nodo
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            String id = n.getId();
            g2.drawString(id, n.getX()-fm.stringWidth(id)/2, n.getY()+fm.getAscent()/2-1);
        }

        // ── Bus animado ──────────────────────────────────────────────
        if (busActivo) {
            int bx = (int) busX, by = (int) busY;
            int bw = 18, bh = 12;

            // Sombra
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRoundRect(bx - bw/2 + 2, by - bh/2 + 3, bw, bh, 5, 5);

            // Cuerpo del bus
            g2.setColor(new Color(255, 200, 0));
            g2.fillRoundRect(bx - bw/2, by - bh/2, bw, bh, 5, 5);

            // Borde
            g2.setColor(new Color(180, 130, 0));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(bx - bw/2, by - bh/2, bw, bh, 5, 5);

            // Ventanas
            g2.setColor(new Color(100, 180, 255, 180));
            g2.fillRect(bx - bw/2 + 2, by - bh/2 + 2, 4, 4);
            g2.fillRect(bx - bw/2 + 8, by - bh/2 + 2, 4, 4);

            // Ruedas
            g2.setColor(new Color(40, 40, 40));
            g2.fillOval(bx - bw/2 + 2, by + bh/2 - 3, 4, 4);
            g2.fillOval(bx + bw/2 - 6, by + bh/2 - 3, 4, 4);
        }

        // ── Barra de estado inferior ─────────────────────────────────
        g2.setTransform(original);
        g2.setFont(new Font("Monospaced", Font.BOLD, 15));

        String statusTxt = null;
        Color  statusCol = VERDE;
        if ("DIJKSTRA".equals(modoActual) && caminoDijkstra != null) {
            StringBuilder sb = new StringBuilder("⚡  Dijkstra  ·  ");
            for (int i = 0; i < caminoDijkstra.size(); i++) {
                sb.append(caminoDijkstra.get(i).getId());
                if (i < caminoDijkstra.size()-1) sb.append(" → ");
            }
            sb.append("   |   Distancia total: ").append(distanciaTotal);
            statusTxt = sb.toString(); statusCol = VERDE;
        } else if ("KRUSKAL".equals(modoActual) && aristasKruskal != null) {
            int lim = Math.min(aristasAnimadas, aristasKruskal.size());
            int pesoAcum = aristasKruskal.subList(0, lim).stream().mapToInt(Arista::getPeso).sum();
            statusTxt = "🌿  Kruskal  ·  Aristas: " + lim + "/" + aristasKruskal.size() +
                        "   |   Peso acumulado: " + pesoAcum;
            statusCol = PURP;
        }

        if (statusTxt != null) {
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(statusTxt);
            int barH = 34;
            int barY  = ph - barH;

            g2.setColor(new Color(10, 12, 20, 210));
            g2.fillRoundRect(10, barY + 4, tw + 28, barH - 8, 10, 10);
            g2.setColor(statusCol);
            g2.drawString(statusTxt, 24, barY + barH/2 + fm.getAscent()/2 - 2);
        }
    }
    
    // ── Algoritmos ───────────────────────────────────────────────────
    public void ejecutarDijkstra(VistaDijkstra vista) {
        limpiar();
        List<Nodo> nodos = grafo.getNodos();
        if (nodos.size() < 2) { mostrarDialogoError(this, "Necesitas al menos 2 nodos."); return; }
        Nodo origen = buscarPorId("RA");
        if (origen == null) { mostrarDialogoError(this, "No existe el nodo RA."); return; }

        String[] destinos = {"AA","AE","AH","AR","CI","FG","FP","FR","GL","IA","JQ","MM","OR","RG","RM","RR"};
        String destinoId  = mostrarDialogoSeleccion(this, "Selecciona el nodo destino:", "Dijkstra", destinos);
        if (destinoId == null) return;

        Nodo destino = buscarPorId(destinoId);
        if (origen == destino) { mostrarDialogoError(this, "Origen y destino iguales."); return; }

        Dijkstra.ejecutar(grafo, origen, destino);
        caminoDijkstra = Dijkstra.camino;
        distanciaTotal = Dijkstra.distancias.get(destino);
        modoActual     = "DIJKSTRA";

        if (caminoDijkstra.size() <= 1) {
            mostrarDialogoError(this, "No existe camino.");
            caminoDijkstra = null;
        }
        vista.mostrarDijkstra(Dijkstra.distancias, Dijkstra.anteriores, caminoDijkstra);
        canvas.repaint();
        animarBus();
    }

    public void ejecutarKruskal(VistaKruskal vista) {
        limpiar();
        if (grafo.getNodos().size() < 2) { mostrarDialogoError(this, "Necesitas al menos 2 nodos."); return; }
        Kruskal.ejecutar(grafo);
        aristasKruskal  = new ArrayList<>(Kruskal.aristasArbol);
        aristasAnimadas = 0;
        modoActual      = "KRUSKAL";
        vista.mostrarKruskal(aristasKruskal, Kruskal.pesoTotal);
        animarKruskal();
    }

    private void animarKruskal() {
        aristasAnimadas = 0;
        timerAnimacion  = new Timer(600, null);
        timerAnimacion.addActionListener(e -> {
            aristasAnimadas++; canvas.repaint();
            if (aristasAnimadas >= aristasKruskal.size()) timerAnimacion.stop();
        });
        timerAnimacion.start(); canvas.repaint();
    }

    private void animarBus() {
        if (caminoDijkstra == null || caminoDijkstra.size() < 2) return;
        busSegmento = 0;
        busT        = 0.0;
        busActivo   = true;

        Nodo inicio = caminoDijkstra.get(0);
        busX = inicio.getX();
        busY = inicio.getY();

        if (timerBus != null) timerBus.stop();

        timerBus = new Timer(16, e -> {
            busT += 0.03;
            if (busT >= 1.0) {
                busT = 0.0;
                busSegmento++;
                if (busSegmento >= caminoDijkstra.size() - 1) {
                    busActivo = false;
                    ((Timer) e.getSource()).stop();
                }
            }
            if (busActivo) {
                Nodo desde = caminoDijkstra.get(busSegmento);
                Nodo hasta = caminoDijkstra.get(busSegmento + 1);
                busX = desde.getX() + (hasta.getX() - desde.getX()) * busT;
                busY = desde.getY() + (hasta.getY() - desde.getY()) * busT;
            }
            canvas.repaint();
        });
        timerBus.start();
    }

    public void limpiar() {
        if (timerAnimacion != null) timerAnimacion.stop();
        if (timerBus != null) timerBus.stop();
        busActivo = false;
        caminoDijkstra = null; aristasKruskal = null;
        aristasAnimadas = 0; modoActual = "";
        lblStatus.setText("Clic en nodo para seleccionar  ·  Clic en espacio vacío para agregar");
        canvas.repaint();
    }

    private Nodo buscarPorId(String id) {
        return grafo.getNodos().stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null);
    }

    private Nodo buscarNodo(int x, int y) {
        for (Nodo n : grafo.getNodos()) {
            int dx = n.getX()-x, dy = n.getY()-y;
            if (Math.sqrt(dx*dx+dy*dy) <= RADIO) return n;
        }
        return null;
    }

    private boolean aristaEnCamino(Arista a, List<Nodo> cam) {
        if (cam == null || cam.size() < 2) return false;
        for (int i = 0; i < cam.size()-1; i++) {
            Nodo u = cam.get(i), v = cam.get(i+1);
            if ((a.getOrigen()==u&&a.getDestino()==v)||(a.getOrigen()==v&&a.getDestino()==u)) return true;
        }
        return false;
    }

    private boolean aristaEnKruskal(Arista a) {
        if (aristasKruskal == null) return false;
        int lim = Math.min(aristasAnimadas, aristasKruskal.size());
        for (int i = 0; i < lim; i++) {
            Arista k = aristasKruskal.get(i);
            if ((k.getOrigen()==a.getOrigen()&&k.getDestino()==a.getDestino())||
                (k.getOrigen()==a.getDestino()&&k.getDestino()==a.getOrigen())) return true;
        }
        return false;
    }

    // ── Botón compacto de la barra ────────────────────────────────────
    private JButton mkBtn(String txt, Color c1, Color c2) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color a = getModel().isRollover() ? c2 : c1;
                g2.setColor(a);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                g2.setColor(new Color(200, 220, 255));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                              (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(txt.length() > 2 ? 100 : 36, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Diálogos dark ────────────────────────────────────────────────

    private static final Color DLG_BG    = new Color(14, 18, 32);
    private static final Color DLG_CARD  = new Color(22, 28, 48);
    private static final Color DLG_CIAN  = new Color(0,  200, 180);
    private static final Color DLG_AZUL  = new Color(50, 120, 255);
    private static final Color DLG_TEXT  = new Color(220, 230, 255);
    private static final Color DLG_DIM   = new Color(130, 145, 175);
    private static final Color DLG_BORDE = new Color(50,  65, 100);

    /** Diálogo de input de texto con estilo dark */
    private String mostrarDialogoTexto(Component parent, String mensaje, String titulo) {
        JDialog dlg = crearBaseDialogo(parent, titulo, 420, 220);
        String[] resultado = {null};

        JLabel lbl = new JLabel(mensaje);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(DLG_TEXT);

        JTextField campo = new JTextField();
        campo.setBackground(new Color(28, 34, 58));
        campo.setForeground(DLG_TEXT);
        campo.setCaretColor(DLG_CIAN);
        campo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(DLG_BORDE, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        campo.setPreferredSize(new Dimension(340, 38));

        JButton btnOk  = dlgBoton("  Aceptar  ", DLG_CIAN, DLG_AZUL);
        JButton btnCan = dlgBoton("  Cancelar  ", new Color(70,80,110), new Color(50,58,85));

        btnOk.addActionListener(e  -> { resultado[0] = campo.getText(); dlg.dispose(); });
        btnCan.addActionListener(e -> dlg.dispose());
        campo.addActionListener(e  -> { resultado[0] = campo.getText(); dlg.dispose(); });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnOk);
        btnPanel.add(btnCan);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(28, 32, 24, 32));
        lbl.setAlignmentX(0.5f);
        campo.setAlignmentX(0.5f);
        btnPanel.setAlignmentX(0.5f);
        contenido.add(lbl);
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(campo);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(btnPanel);

        dlg.add(contenido);
        dlg.setVisible(true);
        return resultado[0];
    }

    /** Diálogo de selección (combo) con estilo dark */
    private String mostrarDialogoSeleccion(Component parent, String mensaje,
                                            String titulo, String[] opciones) {
        JDialog dlg = crearBaseDialogo(parent, titulo, 420, 230);
        String[] resultado = {null};

        JLabel lbl = new JLabel(mensaje);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(TEXTO);

        JComboBox<String> combo = new JComboBox<>(opciones);
        combo.setBackground(new Color(0, 34, 58));
        combo.setForeground(BLACK);
        combo.setFont(new Font("Monospaced", Font.BOLD, 14));
        combo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TEXTO, 1, true),
            new EmptyBorder(4, 8, 4, 8)));
        combo.setPreferredSize(new Dimension(340, 38));

        // Renderizador del combo oscuro
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? new Color(0, 80, 70) : new Color(22, 28, 48));
                setForeground(sel ? DLG_CIAN : DLG_TEXT);
                setFont(new Font("Monospaced", Font.BOLD, 13));
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });

        JButton btnOk  = dlgBoton("  Aceptar  ", DLG_CIAN, DLG_AZUL);
        JButton btnCan = dlgBoton("  Cancelar  ", new Color(70,80,110), new Color(50,58,85));

        btnOk.addActionListener(e  -> { resultado[0] = (String) combo.getSelectedItem(); dlg.dispose(); });
        btnCan.addActionListener(e -> dlg.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnOk);
        btnPanel.add(btnCan);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(28, 32, 24, 32));
        lbl.setAlignmentX(0.5f);
        combo.setAlignmentX(0.5f);
        btnPanel.setAlignmentX(0.5f);
        contenido.add(lbl);
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(combo);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(btnPanel);

        dlg.add(contenido);
        dlg.setVisible(true);
        return resultado[0];
    }

    /** Diálogo de error con estilo dark */
    private void mostrarDialogoError(Component parent, String mensaje) {
        JDialog dlg = crearBaseDialogo(parent, "Aviso", 380, 170);

        JLabel lbl = new JLabel("⚠  " + mensaje);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(new Color(255, 100, 80));
        lbl.setAlignmentX(0.5f);

        JButton btnOk = dlgBoton("  Aceptar  ", DLG_CIAN, DLG_AZUL);
        btnOk.addActionListener(e -> dlg.dispose());
        btnOk.setAlignmentX(0.5f);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(30, 32, 24, 32));
        contenido.add(lbl);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(btnOk);

        dlg.add(contenido);
        dlg.setVisible(true);
    }

    /** Crea la base de un diálogo oscuro modal */
    private JDialog crearBaseDialogo(Component parent, String titulo, int w, int h) {
        Window owner = parent instanceof Window ? (Window) parent
                     : SwingUtilities.getWindowAncestor(parent);
        JDialog dlg = new JDialog(owner, titulo, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(parent);

        JPanel fondo = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DLG_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.setColor(new Color(255,255,255, 18));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                // Línea acento superior
                g2.setPaint(new GradientPaint(30, 0, DLG_CIAN, getWidth()-30, 0, DLG_AZUL));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(30, 0, getWidth()-30, 0);
            }
        };
        fondo.setOpaque(false);
        fondo.setBackground(DLG_CARD);

        // Barra de título del diálogo
        JLabel lblTit = new JLabel("  " + titulo);
        lblTit.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblTit.setForeground(DLG_CIAN);
        JButton btnX = new JButton("✕");
        btnX.setForeground(DLG_DIM);
        btnX.setFont(new Font("Dialog", Font.PLAIN, 12));
        btnX.setOpaque(false); btnX.setContentAreaFilled(false);
        btnX.setBorderPainted(false); btnX.setFocusPainted(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> dlg.dispose());

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(new EmptyBorder(10, 14, 4, 10));
        titleBar.add(lblTit, BorderLayout.WEST);
        titleBar.add(btnX,   BorderLayout.EAST);

        fondo.add(titleBar, BorderLayout.NORTH);
        dlg.setContentPane(fondo);
        return dlg;
    }
    
    private String menorId(String a, String b) {
        return a.compareTo(b) <= 0 ? a : b;
    }
    private String mayorId(String a, String b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    /** Botón estilizado para diálogos dark */
    private JButton dlgBoton(String txt, Color c1, Color c2) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color from = getModel().isRollover() ? c1.brighter() : c1;
                Color to   = getModel().isRollover() ? c2.brighter() : c2;
                if (getModel().isPressed()) { from = c2; to = c1; }
                g2.setPaint(new GradientPaint(0, 0, from, getWidth(), 0, to));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(130, 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    @Override protected void paintComponent(Graphics g) { super.paintComponent(g); }
}