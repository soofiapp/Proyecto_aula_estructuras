package co.edu.upb.proyectoAula;

import java.awt.*;
import java.util.Map;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class PanelTabla extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblTitulo;

    public PanelTabla() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTitulo = new JLabel("Ejecuta un algoritmo para ver los resultados.");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(60, 60, 60));

        modelo = new DefaultTableModel();
        tabla  = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(70, 130, 200));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setSelectionBackground(new Color(200, 230, 255));

        add(lblTitulo, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    // Muestra tabla de distancias Dijkstra
    public void mostrarDijkstra(Map<Nodo, Integer> distancias,
                                 Map<Nodo, Nodo> anteriores,
                                 List<Nodo> camino) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        modelo.addColumn("Nodo");
        modelo.addColumn("Distancia");
        modelo.addColumn("Nodo anterior");
        modelo.addColumn("¿En camino óptimo?");

        lblTitulo.setText("Dijkstra — Tabla de distancias mínimas");

        for (Map.Entry<Nodo, Integer> e : distancias.entrySet()) {
            Nodo n        = e.getKey();
            int  dist     = e.getValue();
            Nodo anterior = anteriores.get(n);
            boolean enCamino = camino.contains(n);

            modelo.addRow(new Object[]{
                n.getId(),
                dist == Integer.MAX_VALUE ? "∞" : dist,
                anterior != null ? anterior.getId() : "—",
                enCamino ? "✔ Sí" : "No"
            });
        }

        // Colorear filas del camino
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String enCamino = (String) modelo.getValueAt(row, 3);
                if ("✔ Sí".equals(enCamino)) {
                    c.setBackground(new Color(210, 245, 220));
                    c.setForeground(new Color(20, 120, 50));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        tabla.repaint();
    }

    // Muestra tabla de orden BFS o DFS
    public void mostrarRecorrido(List<Nodo> orden, String tipo) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        modelo.addColumn("Paso");
        modelo.addColumn("Nodo visitado");

        lblTitulo.setText(tipo + " — Orden de visita de nodos");

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                c.setBackground(row % 2 == 0 ? new Color(235, 245, 255) : Color.WHITE);
                c.setForeground(Color.BLACK);
                return c;
            }
        });

        for (int i = 0; i < orden.size(); i++) {
            modelo.addRow(new Object[]{ i + 1, orden.get(i).getId() });
        }
        tabla.repaint();
    }
}
