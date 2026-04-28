package co.edu.upb.proyectoAula;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        Grafo grafo = GrafoMapa.crear();

        PanelGrafo   panelGrafo    = new PanelGrafo(grafo);
        PanelTabla   panelDijkstra = new PanelTabla();
        PanelKruskal panelKruskal  = new PanelKruskal();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Grafo",      panelGrafo);
        tabs.addTab("Dijkstra",   panelDijkstra);
        tabs.addTab("Kruskal",    panelKruskal);
        tabs.addTab("Adyacencia", new MatrizAdyacencia(grafo));

        JButton btnDijkstra = crearBoton("▶ Dijkstra", new Color(34,  150, 80));
        JButton btnKruskal  = crearBoton("▶ Kruskal",  new Color(130,  40, 180));
        btnDijkstra.addActionListener(e -> panelGrafo.ejecutarDijkstra(panelDijkstra));
        btnKruskal .addActionListener(e -> panelGrafo.ejecutarKruskal(panelKruskal));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barra.setBackground(new Color(240, 240, 245));
        barra.add(btnDijkstra);
        barra.add(btnKruskal);

        setTitle("Grafo — Dijkstra");
        setSize(1000, 740);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(tabs,  BorderLayout.CENTER);
        add(barra, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}