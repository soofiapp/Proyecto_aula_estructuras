package co.edu.upb.proyectoAula;

import java.awt.*;
import java.util.Map;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
 
public class PanelTabla extends JPanel implements VistaDijkstra {
 
    private static final Color BG_PANEL   = new Color(10,  12,  20);
    private static final Color BG_CARD    = new Color(18,  22,  38);
    private static final Color BG_ROW_A   = new Color(22,  28,  48);
    private static final Color BG_ROW_B   = new Color(16,  20,  36);
    private static final Color BG_PATH    = new Color(20,  60,  35);
    private static final Color CIAN       = new Color(0,  200, 180);
    private static final Color VERDE      = new Color(34, 180,  90);
    private static final Color AZUL_H     = new Color(50, 120, 255);
    private static final Color TEXTO      = new Color(220,230,255);
    private static final Color TEXTO_DIM  = new Color(130,145,175);
    private static final Color BORDE      = new Color(50,  65, 100);
 
    private JTable            tabla;
    private DefaultTableModel modelo;
    private JLabel            lblTitulo;
    private JLabel            lblSub;
 
    public PanelTabla() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PANEL);
 
        // ── Encabezado ───────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Línea inferior degradada
                g2.setPaint(new GradientPaint(0, getHeight()-1, CIAN,
                                               getWidth(), getHeight()-1, AZUL_H));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 22, 14, 22));
 
        lblTitulo = new JLabel("⚡  Dijkstra");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblTitulo.setForeground(CIAN);
 
        lblSub = new JLabel("Ejecuta Dijkstra desde el grafo para ver resultados.");
        lblSub.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblSub.setForeground(TEXTO_DIM);
 
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblSub);
        header.add(textos, BorderLayout.WEST);
 
        // ── Tabla ────────────────────────────────────────────────────
        modelo = new DefaultTableModel();
        tabla  = buildTable();
 
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG_PANEL);
 
        // Scrollbar oscura
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new DarkScrollBarUI());
 
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(BG_PANEL);
        contenido.setBorder(new EmptyBorder(12, 14, 12, 14));
        contenido.add(scroll);
 
        add(header,    BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }
 
    private JTable buildTable() {
        JTable t = new JTable(modelo) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        t.setBackground(BG_ROW_B);
        t.setForeground(TEXTO);
        t.setSelectionBackground(new Color(0, 80, 70));
        t.setSelectionForeground(CIAN);
        t.setGridColor(new Color(40, 50, 80));
        t.setFont(new Font("Monospaced", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
 
        // Encabezado de tabla
        JTableHeader th = t.getTableHeader();
        th.setBackground(new Color(14, 18, 32));
        th.setForeground(CIAN);
        th.setFont(new Font("Monospaced", Font.BOLD, 13));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDE));
        th.setReorderingAllowed(false);
 
        return t;
    }
 
    @Override
    public void mostrarDijkstra(Map<Nodo, Integer> distancias,
                                 Map<Nodo, Nodo>    anteriores,
                                 List<Nodo>         camino) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        modelo.addColumn("Nodo");
        modelo.addColumn("Distancia mínima");
        modelo.addColumn("Nodo anterior");
        modelo.addColumn("¿En camino óptimo?");
 
        lblTitulo.setText("⚡  Dijkstra — Distancias mínimas");
        lblSub.setText(camino != null
            ? "Camino óptimo encontrado  ·  " + camino.size() + " nodos"
            : "Sin camino disponible.");
 
        for (Map.Entry<Nodo, Integer> e : distancias.entrySet()) {
            Nodo n        = e.getKey();
            int  dist     = e.getValue();
            Nodo anterior = anteriores.get(n);
            boolean enCam = camino != null && camino.contains(n);
            modelo.addRow(new Object[]{
                n.getId(),
                dist == Integer.MAX_VALUE ? "∞" : dist,
                anterior != null ? anterior.getId() : "—",
                enCam ? "✔  Sí" : "No"
            });
        }
 
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String enCam = (String) modelo.getValueAt(row, 3);
                if (sel) {
                    setBackground(new Color(0, 80, 70));
                    setForeground(CIAN);
                } else if ("✔  Sí".equals(enCam)) {
                    setBackground(BG_PATH);
                    setForeground(VERDE);
                } else {
                    setBackground(row % 2 == 0 ? BG_ROW_A : BG_ROW_B);
                    setForeground(TEXTO);
                }
                setFont(new Font("Monospaced", Font.PLAIN, 13));
                return this;
            }
        });
        tabla.repaint();
    }
 
    public void mostrarRecorrido(List<Nodo> orden, String tipo) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        modelo.addColumn("Paso");
        modelo.addColumn("Nodo visitado");
        lblTitulo.setText("⚡  " + tipo + " — Orden de visita");
        lblSub.setText(orden.size() + " nodos visitados");
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setBackground(row % 2 == 0 ? BG_ROW_A : BG_ROW_B);
                setForeground(TEXTO);
                setFont(new Font("Monospaced", Font.PLAIN, 13));
                return this;
            }
        });
        for (int i = 0; i < orden.size(); i++)
            modelo.addRow(new Object[]{i + 1, orden.get(i).getId()});
        tabla.repaint();
    }
 
    // ── ScrollBar oscura ─────────────────────────────────────────────
    static class DarkScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor    = new Color(50, 65, 100);
            trackColor    = new Color(14, 18, 32);
            thumbDarkShadowColor = new Color(14, 18, 32);
            thumbHighlightColor  = new Color(50, 65, 100);
            thumbLightShadowColor = new Color(14, 18, 32);
        }
        @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
        @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
        private JButton zeroButton() {
            JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6);
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }
}