package co.edu.upb.proyectoAula;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PanelKruskal extends JPanel {

    private JTable             tabla;
    private DefaultTableModel  modelo;
    private JLabel             lblTitulo;
    private JLabel             lblPeso;

    public PanelKruskal() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTitulo = new JLabel("Ejecuta Kruskal para ver el árbol de expansión mínima.");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(60, 60, 60));

        lblPeso = new JLabel("");
        lblPeso.setFont(new Font("Arial", Font.BOLD, 13));
        lblPeso.setForeground(new Color(160, 80, 0));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Color.WHITE);
        encabezado.add(lblTitulo, BorderLayout.NORTH);
        encabezado.add(lblPeso,   BorderLayout.SOUTH);

        modelo = new DefaultTableModel();
        tabla  = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(160, 80, 200));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setSelectionBackground(new Color(240, 210, 255));

        add(encabezado,          BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public void mostrarKruskal(List<Arista> aristas, int pesoTotal) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        modelo.addColumn("Paso");
        modelo.addColumn("Arista");
        modelo.addColumn("Peso");
        modelo.addColumn("Acción");

        lblTitulo.setText("Kruskal — Árbol de expansión mínima");
        lblPeso.setText("Peso total del árbol: " + pesoTotal);

        // Renderer con filas alternas moradas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, val, sel, foc, row, col);
                c.setBackground(row % 2 == 0
                    ? new Color(245, 230, 255)
                    : Color.WHITE);
                c.setForeground(new Color(80, 0, 120));
                return c;
            }
        });

        for (int i = 0; i < aristas.size(); i++) {
            Arista a = aristas.get(i);
            modelo.addRow(new Object[]{
                i + 1,
                a.getOrigen().getId() + " — " + a.getDestino().getId(),
                a.getPeso(),
                "✔ Agregada al árbol"
            });
        }
        tabla.repaint();
    }
}
