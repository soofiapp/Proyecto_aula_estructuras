package co.edu.upb.proyectoAula;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
 
public class PanelGrafo extends JPanel {
 
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
 
    private double zoom    = 1.0;
    private double offsetY = 0;
    private double offsetX = 0; 
 
    private JPanel barraBotones;
    private JPanel canvas;
    private JLabel lblZoom;
 
    // Para zoom tactil (pinch)
    private double distanciaTactilInicial = -1;
    private double zoomAntesTactil        = 1.0;
 
    public PanelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
 
        // Barra de botones ------------
        barraBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        barraBotones.setBackground(new Color(235, 235, 235));
 
        JButton btnMenos   = new JButton("-");
        JButton btnMas     = new JButton("+");
        JButton btnCentrar = new JButton("⌖ Centrar");
        lblZoom = new JLabel("Zoom: 100%");
 
        Font fBtn = new Font("Arial", Font.BOLD, 13);
        btnMenos.setFont(fBtn);
        btnMas.setFont(fBtn);
        btnCentrar.setFont(new Font("Arial", Font.PLAIN, 13));
        lblZoom.setFont(new Font("Arial", Font.PLAIN, 12));
 
        btnMenos.setPreferredSize(new Dimension(90, 28));
        btnMas.setPreferredSize(new Dimension(90, 28));
        btnCentrar.setPreferredSize(new Dimension(90, 28));
 
        btnMas.addActionListener(e -> aplicarZoom(zoom * 1.2));
        btnMenos.addActionListener(e -> aplicarZoom(zoom / 1.2));
        btnCentrar.addActionListener(e -> {
            zoom    = 1.0;
            offsetY = 0;
            lblZoom.setText("Zoom: 100%");
            canvas.repaint();
        });
 
        barraBotones.add(btnMenos);
        barraBotones.add(btnMas);
        barraBotones.add(lblZoom);
 
        // Canvas ---------
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar(g);
            }
        };
        canvas.setBackground(Color.WHITE);
 
        add(barraBotones, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
 
        // Mouse: crear nodos y conectar ---------
        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (timerAnimacion != null) timerAnimacion.stop();
 
                // Coordenadas del grafo corregidas para centrado real
                int[] gc = pantallaAGrafo(e.getX(), e.getY());
                int gx = gc[0], gy = gc[1];
 
                Nodo encontrado = buscarNodo(gx, gy);
                if (encontrado == null) {
                    String id = JOptionPane.showInputDialog("Nombre del nodo:");
                    if (id != null && !id.trim().isEmpty()) {
                        grafo.agregarNodo(new Nodo(id.trim(), gx, gy));
                        canvas.repaint();
                    }
                } else {
                    if (nodoSeleccionado == null) {
                        nodoSeleccionado = encontrado;
                        canvas.repaint();
                    } else if (nodoSeleccionado != encontrado) {
                        String input = JOptionPane.showInputDialog(
                            "Peso entre " + nodoSeleccionado.getId() +
                            " y " + encontrado.getId() + ":");
                        if (input != null) {
                            try {
                                grafo.agregarArista(nodoSeleccionado, encontrado,
                                    Integer.parseInt(input.trim()));
                                nodoSeleccionado = null;
                                canvas.repaint();
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Número inválido.");
                            }
                        } else {
                            nodoSeleccionado = null;
                            canvas.repaint();
                        }
                    } else {
                        nodoSeleccionado = null;
                        canvas.repaint();
                    }
                }
                canvas.requestFocusInWindow();
            }
        });
 
        // Rueda del mouse: scroll vertical -------------
        canvas.addMouseWheelListener(e -> {
        	if (e.isControlDown()) {
                aplicarZoom(e.getWheelRotation() < 0 ? zoom * 1.1 : zoom / 1.1);
            } else if (e.isShiftDown()) {
                offsetX -= e.getWheelRotation() * 30; // 👈 Shift+rueda = horizontal
                canvas.repaint();
            } else {
                offsetY -= e.getWheelRotation() * 30;
                canvas.repaint();
            }
        });
 
        // ── Touch / trackpad: pinch zoom ──────────────────────
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
       
        });

        canvas.addMouseWheelListener(e -> {
           
        });
 
        // Teclado ------------------
        canvas.setFocusable(true);
        canvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:       offsetY += 40; canvas.repaint(); break;
                    case KeyEvent.VK_DOWN:     offsetY -= 40; canvas.repaint(); break;
                    case KeyEvent.VK_LEFT:     offsetX += 40; canvas.repaint(); break; 
                    case KeyEvent.VK_RIGHT:    offsetX -= 40; canvas.repaint(); break;
                    case KeyEvent.VK_HOME:     zoom = 1.0; offsetY = 0; lblZoom.setText("Zoom: 100%"); canvas.repaint(); break;
                    case KeyEvent.VK_ADD:
                    case KeyEvent.VK_PLUS:     aplicarZoom(zoom * 1.2); break;
                    case KeyEvent.VK_SUBTRACT:
                    case KeyEvent.VK_MINUS:    aplicarZoom(zoom / 1.2); break;
                }
            }
        });
    }
 
    // Coordenadas-------------------
 
    /** Convierte coordenadas de pantalla a coordenadas del grafo */
    private int[] pantallaAGrafo(int px, int py) {
        double[] centro = centroGrafo();
        double gcx = centro[0], gcy = centro[1];
        double canvasCx = canvas.getWidth()  / 2.0;
        double canvasCy = canvas.getHeight() / 2.0;
 
        int gx = (int) ((px - canvasCx - offsetX) / zoom + gcx);
        int gy = (int) ((py - canvasCy - offsetY) / zoom + gcy);
        return new int[]{gx, gy};
    }
 
    /** Devuelve el centro geométrico del grafo, o el centro del canvas si está vacío */
    private double[] centroGrafo() {
        List<Nodo> nodos = grafo.getNodos();
        if (nodos.isEmpty()) {
            return new double[]{canvas.getWidth() / 2.0, canvas.getHeight() / 2.0};
        }
        double minX = nodos.stream().mapToInt(Nodo::getX).min().getAsInt();
        double maxX = nodos.stream().mapToInt(Nodo::getX).max().getAsInt();
        double minY = nodos.stream().mapToInt(Nodo::getY).min().getAsInt();
        double maxY = nodos.stream().mapToInt(Nodo::getY).max().getAsInt();
        return new double[]{(minX + maxX) / 2.0, (minY + maxY) / 2.0};
    }
 
    private void aplicarZoom(double nuevoZoom) {
        zoom = Math.max(0.2, Math.min(nuevoZoom, 5.0));
        lblZoom.setText(String.format("Zoom: %d%%", (int)(zoom * 100)));
        canvas.repaint();
    }
 
    // ── Dibujar ---------------
    private void dibujar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
        int panelW = canvas.getWidth();
        int panelH = canvas.getHeight();
 
        // Centro del canvas
        double canvasCx = panelW / 2.0;
        double canvasCy = panelH / 2.0;
 
        // Centro real del grafo
        double[] centro = centroGrafo();
        double gcx = centro[0];
        double gcy = centro[1];
 
        AffineTransform original = g2.getTransform();
        g2.translate(canvasCx + offsetX, canvasCy + offsetY);
        g2.scale(zoom, zoom);
        g2.translate(-gcx, -gcy);
 
     // Aristas ------------------
        for (Arista a : grafo.getAristas()) {
            int x1 = a.getOrigen().getX(),  y1 = a.getOrigen().getY();
            int x2 = a.getDestino().getX(), y2 = a.getDestino().getY();

            boolean enDijkstra  = aristaEnCamino(a, caminoDijkstra);
            boolean enAEstrella = aristaEnCamino(a, caminoAEstrella);
            boolean enKruskal   = aristaEnKruskal(a);

            Color color;
            float grosor;
            if      (enDijkstra)  { color = new Color(34, 180, 90);  grosor = 5.5f; }
            else if (enAEstrella) { color = new Color(0, 160, 220);  grosor = 5.5f; }
            else if (enKruskal)   { color = new Color(160, 80, 200); grosor = 5.5f; }
            else                  { color = Color.GRAY;              grosor = 2f;   }

            g2.setColor(color);
            g2.setStroke(new BasicStroke(grosor));
            g2.drawLine(x1, y1, x2, y2);

            // ── Peso de la arista ──────────────────────────────────────
            int mx = (x1 + x2) / 2;
            int my = (y1 + y2) / 2;

            String peso = String.valueOf(a.getPeso());
            g2.setFont(new Font("BOLD", Font.PLAIN, 9));
            FontMetrics fmP = g2.getFontMetrics();
            int pw = fmP.stringWidth(peso);

            // Fondo blanco pequeño para que se lea bien
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRect(mx - pw/2 - 1, my - 9, pw + 2, 11);

            // Texto del peso
            g2.setColor(enDijkstra || enAEstrella || enKruskal
                        ? color.darker()
                        : new Color(0, 0, 0));
            g2.drawString(peso, mx - pw/2, my);
            // ──────────────────────────────────────────────────────────
        }
 
        // Nodos -------------------
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
                            relleno = new Color((int)(100+155*t),(int)(149-100*t),(int)(237-180*t));
                        } else if (idx == nodoAnimado) relleno = new Color(255, 200, 0);
                    }
                    break;
                case "KRUSKAL":
                    if (aristasKruskal != null) {
                        int lim = Math.min(aristasAnimadas, aristasKruskal.size());
                        for (int i = 0; i < lim; i++) {
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
                        if (idx == 0) relleno = new Color(34, 180, 90);
                        else if (idx == caminoAEstrella.size()-1) relleno = new Color(220, 60, 60);
                        else if (idx > 0 && idx < nodoAnimado) relleno = new Color(0, 180, 230);
                        else if (idx == nodoAnimado) relleno = new Color(255, 200, 0);
                    }
                    break;
            }
 
            if (n == nodoSeleccionado) relleno = new Color(255, 165, 0);
 
            g2.setColor(relleno);
            g2.fillOval(n.getX()-RADIO, n.getY()-RADIO, RADIO*2, RADIO*2);
            g2.setColor(Color.BLUE);
            g2.drawOval(n.getX()-RADIO, n.getY()-RADIO, RADIO*2, RADIO*2);
 
            if ((modoActual.equals("BFS")||modoActual.equals("DFS")) && ordenRecorrido != null) {
                int idx = ordenRecorrido.indexOf(n);
                if (idx >= 0 && idx < nodoAnimado) {
                    g2.setColor(new Color(255, 255, 200));
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    g2.drawString(String.valueOf(idx+1), n.getX()+RADIO-10, n.getY()-RADIO+12);
                }
            }
 
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(n.getId(), n.getX()-fm.stringWidth(n.getId())/2, n.getY()+5);
        }
 
        // Transform para barra de estado  -------
        g2.setTransform(original);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
 
        switch (modoActual) {
            case "DIJKSTRA":
                if (caminoDijkstra != null) {
                    StringBuilder sb = new StringBuilder("Dijkstra — Camino: ");
                    for (int i = 0; i < caminoDijkstra.size(); i++) {
                        sb.append(caminoDijkstra.get(i).getId());
                        if (i < caminoDijkstra.size()-1) sb.append(" → ");
                    }
                    sb.append("   |   Distancia: ").append(distanciaTotal);
                    g2.setColor(new Color(30, 130, 60));
                    g2.drawString(sb.toString(), 10, panelH-10);
                }
                break;
            case "KRUSKAL":
                if (aristasKruskal != null) {
                    int lim = Math.min(aristasAnimadas, aristasKruskal.size());
                    int pesoAcum = aristasKruskal.subList(0, lim).stream().mapToInt(Arista::getPeso).sum();
                    g2.setColor(new Color(120, 40, 180));
                    g2.drawString("Kruskal — Aristas: "+lim+"/"+aristasKruskal.size()+
                        "   |   Peso acumulado: "+pesoAcum, 10, panelH-10);
                }
                break;
            default:
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString(nodoSeleccionado == null
                    ? ""
                    : "Seleccionado: "+nodoSeleccionado.getId()+"Clic en otro nodo para conectar",
                    10, panelH-10);
        }
    }
 
    // ── Dijkstra -------------------
    public void ejecutarDijkstra(PanelTabla panelTabla) {
    	limpiar();
        List<Nodo> nodos = grafo.getNodos();
        if (nodos.size() < 2) { JOptionPane.showMessageDialog(this,"Necesitas al menos 2 nodos."); return; }
        
        String origenId = "RA";
        Nodo origen = buscarPorId(origenId);
        if (origen == null) { JOptionPane.showMessageDialog(this,"No existe el nodo RA."); return; }
        
        String[] nombres = nodos.stream().map(Nodo::getId).toArray(String[]::new);
        String destinoId = (String) JOptionPane.showInputDialog(this,"Nodo DESTINO:","Dijkstra",
            JOptionPane.PLAIN_MESSAGE,null,nombres,nombres[nombres.length-1]);
        if (destinoId == null) return;
        
        Nodo destino = buscarPorId(destinoId);
        if (origen == destino) { JOptionPane.showMessageDialog(this,"Origen y destino iguales."); return; }
        Dijkstra.ejecutar(grafo, origen, destino);
        caminoDijkstra = Dijkstra.camino;
        distanciaTotal = Dijkstra.distancias.get(destino);
        modoActual     = "DIJKSTRA";
        if (caminoDijkstra.size() <= 1) { JOptionPane.showMessageDialog(this,"No existe camino."); caminoDijkstra = null; }
        panelTabla.mostrarDijkstra(Dijkstra.distancias, Dijkstra.anteriores, caminoDijkstra);
        repaint();
    }
 
    // Kruskal --------------
    public void ejecutarKruskal(PanelKruskal panelKruskal) {
        limpiar();
        if (grafo.getNodos().size() < 2) { JOptionPane.showMessageDialog(this,"Necesitas al menos 2 nodos."); return; }
        Kruskal.ejecutar(grafo);
        aristasKruskal  = new ArrayList<>(Kruskal.aristasArbol);
        aristasAnimadas = 0;
        modoActual      = "KRUSKAL";
        panelKruskal.mostrarKruskal(aristasKruskal, Kruskal.pesoTotal);
        animarKruskal();
    }
 
    //  Animaciones ------------
    private void animarRecorrido() {
        nodoAnimado = 0;
        timerAnimacion = new Timer(600, null);
        timerAnimacion.addActionListener(e -> {
            nodoAnimado++; repaint();
            if (nodoAnimado >= ordenRecorrido.size()) timerAnimacion.stop();
        });
        timerAnimacion.start(); repaint();
    }
 
    private void animarKruskal() {
        aristasAnimadas = 0;
        timerAnimacion  = new Timer(700, null);
        timerAnimacion.addActionListener(e -> {
            aristasAnimadas++; repaint();
            if (aristasAnimadas >= aristasKruskal.size()) timerAnimacion.stop();
        });
        timerAnimacion.start(); repaint();
    }
 
    public void limpiar() {
        if (timerAnimacion != null) timerAnimacion.stop();
        caminoDijkstra = null; caminoAEstrella = null;
        ordenRecorrido = null; aristasKruskal  = null;
        aristasAnimadas = 0; nodoAnimado = -1; modoActual = "";
        repaint();
    }
 
    private Nodo pedirOrigen(String algo) {
        List<Nodo> nodos = grafo.getNodos();
        if (nodos.isEmpty()) { JOptionPane.showMessageDialog(this,"No hay nodos."); return null; }
        String[] nombres = nodos.stream().map(Nodo::getId).toArray(String[]::new);
        String id = (String) JOptionPane.showInputDialog(this,"Nodo de inicio para "+algo+":",algo,
            JOptionPane.PLAIN_MESSAGE,null,nombres,nombres[0]);
        return id != null ? buscarPorId(id) : null;
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
 
    private boolean aristaEnCamino(Arista a, List<Nodo> camino) {
        if (camino == null || camino.size() < 2) return false;
        for (int i = 0; i < camino.size()-1; i++) {
            Nodo u = camino.get(i), v = camino.get(i+1);
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
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}