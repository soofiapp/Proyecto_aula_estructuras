package co.edu.upb.proyectoAula;

import javax.swing.*;
import co.edu.upb.proyectoAula.data_structures.Grafo;
import co.edu.upb.proyectoAula.views.PanelGrafo;
import co.edu.upb.proyectoAula.views.PanelKruskal;
import co.edu.upb.proyectoAula.views.PanelMatrizAdyacencia;
import co.edu.upb.proyectoAula.views.PanelTabla;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Main extends JFrame {

    static final Color BG_OSCURO     = new Color(10,  12,  20);
    static final Color BG_CARD       = new Color(18,  22,  38);
    static final Color BG_TAB        = new Color(24,  30,  50);
    static final Color ACENTO_CIAN   = new Color(0,   200, 180);
    static final Color ACENTO_AZUL   = new Color(50,  120, 255);
    static final Color ACENTO_VERDE  = new Color(34,  180, 90);
    static final Color ACENTO_PURP   = new Color(160,  80, 220);
    static final Color TEXTO_CLARO   = new Color(220, 230, 255);
    static final Color TEXTO_APAGADO = new Color(130, 145, 175);
    static final Color BORDE_SUTIL   = new Color(50,  65, 100);

    public Main() {
        Grafo grafo = GrafoMapa.crear();

        PanelGrafo   panelGrafo    = new PanelGrafo(grafo);
        PanelTabla   panelDijkstra = new PanelTabla();
        PanelKruskal panelKruskal  = new PanelKruskal();
        PanelMatrizAdyacencia matrizAdj = new PanelMatrizAdyacencia(grafo);

        JTabbedPane tabs = buildTabs(panelGrafo, panelDijkstra, panelKruskal, matrizAdj);

        JButton btnDijkstra = crearBoton("▶  Dijkstra", ACENTO_VERDE,  new Color(0, 120, 60));
        JButton btnKruskal  = crearBoton("▶  Kruskal",  ACENTO_PURP,   new Color(100, 40, 160));
        JButton btnLimpiar  = crearBoton("✕  Limpiar",  new Color(60, 70, 100), new Color(30, 40, 70));

        btnDijkstra.addActionListener(e -> {
            panelGrafo.ejecutarDijkstra(panelDijkstra);
            tabs.setSelectedIndex(1);
        });
        btnKruskal.addActionListener(e -> {
            panelGrafo.ejecutarKruskal(panelKruskal);
            tabs.setSelectedIndex(2);
        });
        btnLimpiar.addActionListener(e -> panelGrafo.limpiar());

        JLabel lblEstado = new JLabel("Graf-icador Pro  ·  Explorador Dijkstra y Kruskal");
        lblEstado.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblEstado.setForeground(TEXTO_APAGADO);

        JPanel barra = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(14, 18, 32));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDE_SUTIL);
                g2.drawLine(0, 0, getWidth(), 0);
            }
        };
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnDijkstra);
        btnPanel.add(btnKruskal);
        btnPanel.add(Box.createHorizontalStrut(4));
        btnPanel.add(btnLimpiar);

        barra.add(btnPanel,  BorderLayout.WEST);
        barra.add(lblEstado, BorderLayout.EAST);

        JPanel raiz = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_OSCURO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 6));
                for (int x = 0; x < getWidth();  x += 32) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
            }
        };
        raiz.setOpaque(false);
        raiz.add(tabs,  BorderLayout.CENTER);
        raiz.add(barra, BorderLayout.SOUTH);

        setContentPane(raiz);
        setTitle("Graf-icador Pro");
        setSize(1100, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(buildIconImage());
        setVisible(true);
    }

    private JTabbedPane buildTabs(JComponent grafo, JComponent dijkstra,
                                   JComponent kruskal, JComponent matriz) {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP) {
            @Override public void updateUI() {
                setUI(new DarkTabUI());
            }
        };
        tabs.setBackground(BG_OSCURO);
        tabs.setForeground(TEXTO_CLARO);
        tabs.setFont(new Font("Monospaced", Font.BOLD, 13));
        tabs.setOpaque(true);
        tabs.addTab("  ⬡  Grafo  ", grafo);
        tabs.addTab("  ⚡  Dijkstra  ", dijkstra);
        tabs.addTab("  🌿  Kruskal  ", kruskal);
        tabs.addTab("  ⊞  Adyacencia", matriz);
        return tabs;
    }

    private JButton crearBoton(String txt, Color c1, Color c2) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color a = getModel().isPressed() ? c2 :
                          (getModel().isRollover() ? c1.brighter() : c1);
                Color b2 = getModel().isPressed() ? c1 :
                           (getModel().isRollover() ? c2.brighter() : c2);
                g2.setPaint(new GradientPaint(0, 0, a, getWidth(), 0, b2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(148, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private Image buildIconImage() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, ACENTO_CIAN, 32, 32, ACENTO_AZUL));
        int[] xp = {16,28,28,16,4,4}, yp = {2,9,23,30,23,9};
        g2.fillPolygon(xp, yp, 6);
        g2.dispose();
        return img;
    }

    static BufferedImage buildIconImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        return img;
    }

    static class DarkTabUI extends BasicTabbedPaneUI {

        @Override protected void installDefaults() {
            super.installDefaults();
            UIManager.put("TabbedPane.tabInsets",            new Insets(10, 22, 10, 22));
            UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(0, 0, 0, 0));
            UIManager.put("TabbedPane.contentBorderInsets",  new Insets(1, 0, 0, 0));
            UIManager.put("TabbedPane.tabAreaBackground",    BG_OSCURO);
            UIManager.put("TabbedPane.background",           BG_OSCURO);
            UIManager.put("TabbedPane.foreground",           TEXTO_APAGADO);
            UIManager.put("TabbedPane.selected",             BG_TAB);
            UIManager.put("TabbedPane.focus",                BG_OSCURO);
            UIManager.put("TabbedPane.selectHighlight",      BG_TAB);
            UIManager.put("TabbedPane.darkShadow",           BG_OSCURO);
            UIManager.put("TabbedPane.shadow",               BG_OSCURO);
            UIManager.put("TabbedPane.highlight",            BG_TAB);
            UIManager.put("TabbedPane.light",                BG_OSCURO);
        }

        @Override protected void paintTabArea(Graphics g, int tp, int sel) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(8, 10, 20));
            g2.fillRect(0, 0, tabPane.getWidth(),
                        calculateTabAreaHeight(tp, runCount, maxTabHeight));
            g2.setColor(new Color(0, 200, 180, 80));
            int h = calculateTabAreaHeight(tp, runCount, maxTabHeight);
            g2.drawLine(0, h - 1, tabPane.getWidth(), h - 1);
            super.paintTabArea(g, tp, sel);
        }

        @Override protected void paintTabBackground(Graphics g, int tabPlacement,
                int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected) {
                g2.setPaint(new GradientPaint(x, y, new Color(0, 200, 180, 55),
                                               x + w, y, new Color(50, 120, 255, 55)));
                g2.fillRoundRect(x + 1, y + 2, w - 2, h - 1, 8, 8);
                g2.setPaint(new GradientPaint(x, 0, ACENTO_CIAN, x + w, 0, ACENTO_AZUL));
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(x + 4, y + h - 1, x + w - 4, y + h - 1);
            } else {
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillRoundRect(x + 1, y + 4, w - 2, h - 2, 8, 8);
            }
        }

        @Override protected void paintText(Graphics g, int tabPlacement,
                Font font, FontMetrics metrics, int tabIndex,
                String title, Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(tabPane.getFont());
            FontMetrics fm = g2.getFontMetrics();
            if (isSelected) {
                g2.setColor(new Color(0, 0, 0, 100));
                g2.drawString(title, textRect.x + 1, textRect.y + fm.getAscent() + 1);
                g2.setColor(ACENTO_CIAN);
            } else {
                g2.setColor(new Color(160, 180, 220));
            }
            g2.drawString(title, textRect.x, textRect.y + fm.getAscent());
        }

        @Override protected void paintTabBorder(Graphics g, int tabPlacement,
                int tabIndex, int x, int y, int w, int h, boolean isSelected) {}

        @Override protected void paintFocusIndicator(Graphics g, int tabPlacement,
                Rectangle[] rects, int tabIndex, Rectangle iconRect,
                Rectangle textRect, boolean isSelected) {}

        @Override protected void paintContentBorder(Graphics g, int tabPlacement,
                int selectedIndex) {}
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Panel.background",             BG_OSCURO);
        UIManager.put("OptionPane.background",        BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXTO_CLARO);
        UIManager.put("TextField.background",         new Color(28, 34, 58));
        UIManager.put("TextField.foreground",         TEXTO_CLARO);
        UIManager.put("TextField.caretForeground",    ACENTO_CIAN);
        UIManager.put("ComboBox.background",          new Color(28, 34, 58));
        UIManager.put("ComboBox.foreground",          TEXTO_CLARO);
        UIManager.put("Button.background",            BG_TAB);
        UIManager.put("Button.foreground",            TEXTO_CLARO);
        UIManager.put("Label.foreground",             TEXTO_CLARO);
        SwingUtilities.invokeLater(VentanaLogin::new);
    }
}