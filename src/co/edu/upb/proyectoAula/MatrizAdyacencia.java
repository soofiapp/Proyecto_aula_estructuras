package co.edu.upb.proyectoAula;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.HashMap;

public class MatrizAdyacencia extends JScrollPane {

    private static final String[] LETRAS = {
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R"
    };

    public MatrizAdyacencia(Grafo grafo) {
        HashMap<String, Nodo> mapa = new HashMap<>();
        for (Nodo nd : grafo.getNodos()) mapa.put(nd.getId(), nd);

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("  ");
        for (String col : LETRAS) modelo.addColumn(col);

        for (String fila : LETRAS) {
            Object[] row = new Object[LETRAS.length + 1];
            row[0] = fila;
            for (int j = 0; j < LETRAS.length; j++)
                row[j + 1] = resolverCelda(fila + LETRAS[j], mapa);
            modelo.addRow(row);
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(50, 100, 160));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(35);
        for (int i = 1; i <= LETRAS.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(30);
        tabla.setDefaultRenderer(Object.class, new Renderer());
        setViewportView(tabla);
    }

    private String resolverCelda(String id, HashMap<String, Nodo> mapa) {
        if (!mapa.containsKey(id))            return "0";
        if (id.equals("RA"))                  return "I";
        if (!mapa.get(id).getId().equals(id)) return "F";
        return "1";
    }

    private static class Renderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            setHorizontalAlignment(CENTER);
            String v = val == null ? "" : val.toString();
            if (col == 0) {
                setBackground(new Color(50, 100, 160));
                setForeground(Color.WHITE);
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                switch (v) {
                    case "1": setBackground(new Color(200,230,200)); setForeground(new Color(20,100,20));   break;
                    case "0": setBackground(new Color(245,245,245)); setForeground(new Color(180,180,180)); break;
                    case "I": setBackground(new Color(255,220,80));  setForeground(new Color(120,80,0));    break;
                    case "F": setBackground(new Color(210,80,80));   setForeground(Color.WHITE);            break;
                    default:  setBackground(Color.WHITE);            setForeground(Color.BLACK);            break;
                }
            }
            return this;
        }
    }
}