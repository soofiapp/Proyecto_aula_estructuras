package co.edu.upb.proyectoAula.views;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import co.edu.upb.proyectoAula.data_structures.Grafo;
import co.edu.upb.proyectoAula.data_structures.Nodo;

import java.awt.*;
import java.util.HashMap;

public class PanelMatrizAdyacencia extends JPanel {

    private static final Color BG_PANEL  = new Color(10,  12,  20);
    private static final Color BG_CARD   = new Color(18,  22,  38);
    private static final Color BG_ROW_A  = new Color(22,  28,  48);
    private static final Color BG_ROW_B  = new Color(16,  20,  36);
    private static final Color CIAN      = new Color(0,  200, 180);
    private static final Color AZUL_H    = new Color(50, 120, 255);
    private static final Color VERDE     = new Color(34, 180,  90);
    private static final Color AMARILLO  = new Color(255,200,  60);
    private static final Color ROJO      = new Color(220,  60,  60);
    private static final Color TEXTO     = new Color(220, 230, 255);
    private static final Color TEXTO_DIM = new Color(130, 145, 175);
    private static final Color BORDE     = new Color( 50,  65, 100);

    private static final String[] LETRAS = {
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R"
    };

    public PanelMatrizAdyacencia(Grafo grafo) {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PANEL);

        // ── Encabezado ───────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-1, CIAN,
                                               getWidth(), getHeight()-1, AZUL_H));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 22, 14, 22));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblT = new JLabel("⊞  Matriz de Adyacencia");
        lblT.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblT.setForeground(CIAN);

        JLabel lblS = new JLabel("1 = conexión  ·  0 = sin conexión  ·  I = inicio  ·  F = fin");
        lblS.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblS.setForeground(TEXTO_DIM);

        textos.add(lblT);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblS);
        header.add(textos, BorderLayout.WEST);

        // ── Leyenda de colores ───────────────────────────────────────
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        leyenda.setOpaque(false);
        leyenda.add(chip("1", VERDE));
        leyenda.add(chip("0", new Color(50, 60, 90)));
        leyenda.add(chip("I", AMARILLO));
        leyenda.add(chip("F", ROJO));
        header.add(leyenda, BorderLayout.EAST);

        // ── Tabla ────────────────────────────────────────────────────
        HashMap<String, Nodo> mapa = new HashMap<>();
        for (Nodo nd : grafo.getNodos()) mapa.put(nd.getId(), nd);

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("  ");
        for (String col : LETRAS) modelo.addColumn(col);

        for (String fila : LETRAS) {
            Object[] row = new Object[LETRAS.length + 1];
            row[0] = fila;
            for (int j = 0; j < LETRAS.length; j++)
                row[j+1] = resolverCelda(fila + LETRAS[j], mapa);
            modelo.addRow(row);
        }

        JTable tabla = new JTable(modelo) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla.setBackground(BG_ROW_B);
        tabla.setForeground(TEXTO);
        tabla.setGridColor(new Color(35, 45, 70));
        tabla.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.setShowGrid(true);
        tabla.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(8, 10, 20));
        th.setForeground(CIAN);
        th.setFont(new Font("Monospaced", Font.BOLD, 12));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDE));
        th.setReorderingAllowed(false);
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(new Color(8, 10, 20));
                setForeground(CIAN);
                setFont(new Font("Monospaced", Font.BOLD, 12));
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, BORDE),
                    new EmptyBorder(4, 4, 4, 4)));
                return this;
            }
        });

        // Anchos de columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(36);
        for (int i = 1; i <= LETRAS.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(28);

        tabla.setDefaultRenderer(Object.class, new DarkMatrizRenderer());

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

    private String resolverCelda(String id, HashMap<String, Nodo> mapa) {
        if (!mapa.containsKey(id))            return "0";
        if (id.equals("RA"))                  return "I";
        if (!mapa.get(id).getId().equals(id)) return "F";
        return "1";
    }

    private JLabel chip(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (getWidth()-fm.stringWidth(text))/2,
                              (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        l.setFont(new Font("Monospaced", Font.BOLD, 11));
        l.setPreferredSize(new Dimension(28, 22));
        l.setOpaque(false);
        return l;
    }

    private static class DarkMatrizRenderer extends DefaultTableCellRenderer {
        private static final Color BG_HEADER = new Color(14, 18, 32);
        private static final Color BG_1      = new Color(20, 60, 40);
        private static final Color BG_0      = new Color(16, 20, 36);
        private static final Color BG_I      = new Color(60, 50,  0);
        private static final Color BG_F      = new Color(60, 15, 15);
        private static final Color FG_1      = new Color(34, 200, 90);
        private static final Color FG_0      = new Color(50,  60, 90);
        private static final Color FG_I      = new Color(255,200, 60);
        private static final Color FG_F      = new Color(220, 60, 60);

        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            setHorizontalAlignment(CENTER);
            setBorder(new EmptyBorder(0, 0, 0, 0));
            String v = val == null ? "" : val.toString();
            if (col == 0) {
                setBackground(BG_HEADER);
                setForeground(new Color(0, 200, 180));
                setFont(new Font("Monospaced", Font.BOLD, 12));
            } else {
                setFont(new Font("Monospaced", Font.PLAIN, 12));
                switch (v) {
                    case "1": setBackground(BG_1); setForeground(FG_1); break;
                    case "0": setBackground(BG_0); setForeground(FG_0); break;
                    case "I": setBackground(BG_I); setForeground(FG_I); break;
                    case "F": setBackground(BG_F); setForeground(FG_F); break;
                    default:  setBackground(BG_0); setForeground(new Color(80, 90, 120)); break;
                }
            }
            return this;
        }
    }
}