package co.edu.upb.proyectoAula;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
 
public class PanelKruskal extends JPanel implements VistaKruskal {
 
    private static final Color BG_PANEL   = new Color(10,  12,  20);
    private static final Color BG_CARD    = new Color(18,  22,  38);
    private static final Color BG_ROW_A   = new Color(22,  28,  48);
    private static final Color BG_ROW_B   = new Color(16,  20,  36);
    private static final Color PURP       = new Color(160,  80, 220);
    private static final Color PURP_L     = new Color(200, 140, 255);
    private static final Color AZUL_H     = new Color( 50, 120, 255);
    private static final Color TEXTO      = new Color(220, 230, 255);
    private static final Color TEXTO_DIM  = new Color(130, 145, 175);
    private static final Color BORDE      = new Color( 50,  65, 100);
 
    private JTable            tabla;
    private DefaultTableModel modelo;
    private JLabel            lblTitulo;
    private JLabel            lblSub;
    private JLabel            lblPeso;
 
    public PanelKruskal() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PANEL);
 
        // ── Encabezado ───────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-1, PURP,
                                               getWidth(), getHeight()-1, AZUL_H));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 22, 14, 22));
 
        lblTitulo = new JLabel("🌿  Kruskal");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblTitulo.setForeground(PURP_L);
 
        lblSub = new JLabel("Ejecuta Kruskal desde el grafo para ver el árbol de expansión mínima.");
        lblSub.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblSub.setForeground(TEXTO_DIM);
 
        lblPeso = new JLabel("");
        lblPeso.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblPeso.setForeground(PURP_L);
 
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblSub);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblPeso);
        header.add(textos, BorderLayout.WEST);
 
        // ── Tabla ────────────────────────────────────────────────────
        modelo = new DefaultTableModel();
        tabla  = buildTable();
 
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new PanelTabla.DarkScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new PanelTabla.DarkScrollBarUI());
 
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
        t.setSelectionBackground(new Color(80, 30, 120));
        t.setSelectionForeground(PURP_L);
        t.setGridColor(new Color(40, 50, 80));
        t.setFont(new Font("Monospaced", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
 
        JTableHeader th = t.getTableHeader();
        th.setBackground(new Color(14, 18, 32));
        th.setForeground(PURP_L);
        th.setFont(new Font("Monospaced", Font.BOLD, 13));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDE));
        th.setReorderingAllowed(false);
        return t;
    }
 
    @Override
    public void mostrarKruskal(List<Arista> aristas, int pesoTotal) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        modelo.addColumn("Paso");
        modelo.addColumn("Arista");
        modelo.addColumn("Peso");
        modelo.addColumn("Acción");
 
        lblTitulo.setText("🌿  Kruskal — Árbol de expansión mínima");
        lblSub.setText(aristas.size() + " aristas seleccionadas");
        lblPeso.setText("Peso total del árbol:  " + pesoTotal);
 
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(new Color(80, 30, 120));
                    setForeground(PURP_L);
                } else {
                    setBackground(row % 2 == 0 ? BG_ROW_A : BG_ROW_B);
                    setForeground(col == 3 ? PURP_L : TEXTO);
                }
                setFont(new Font("Monospaced", Font.PLAIN, 13));
                return this;
            }
        });
 
        for (int i = 0; i < aristas.size(); i++) {
            Arista a = aristas.get(i);
            modelo.addRow(new Object[]{
                i + 1,
                a.getOrigen().getId() + "  —  " + a.getDestino().getId(),
                a.getPeso(),
                "✔  Agregada al árbol"
            });
        }
        tabla.repaint();
    }
}