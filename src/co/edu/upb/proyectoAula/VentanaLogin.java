package co.edu.upb.proyectoAula;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
 
/*
 * Credenciales de administrador: usuario = "admin" | contraseña = "admin123"
 */
public class VentanaLogin extends JFrame {
 
    // ── Credenciales de administrador ────────────────────────────────
    private static final String USUARIO_ADMIN = "a";
    private static final String PASS_ADMIN    = "a";
 
    // ── Componentes ─────────────────────────────────────────────────
    private JTextField     txtUsuario;
    private JPasswordField txtPassword;
    private JButton        btnIngresar;
    private JLabel         lblError;
    private JLabel         lblMostrarPass;
    private boolean        mostrandoPass = false;
 
    // ── Colores del tema oscuro ──────────────────────────────────────
    private static final Color BG_OSCURO     = new Color(10,  12,  20);
    private static final Color BG_CARD       = new Color(18,  22,  38);
    private static final Color ACENTO_CIAN   = new Color(0,   200, 180);
    private static final Color ACENTO_AZUL   = new Color(50,  120, 255);
    private static final Color TEXTO_CLARO   = new Color(220, 230, 255);
    private static final Color TEXTO_APAGADO = new Color(130, 145, 175);
    private static final Color BORDE_INPUT   = new Color(50,  65,  100);
    private static final Color BORDE_FOCUS   = new Color(0,   200, 180);
    private static final Color ROJO_ERROR    = new Color(255,  80,  80);
 
    public VentanaLogin() {
        setTitle("Graf-icador Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);              // ventana sin bordes del SO
        setSize(500, 560);
        
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
 
        // Panel raíz con fondo degradado
        JPanel raiz = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo
                g2.setColor(BG_OSCURO);
                g2.fillRect(0, 0, getWidth(), getHeight());
 
                // Orbes de luz decorativos
                paintOrb(g2, getWidth() * 0.1f,  getHeight() * 0.15f, 220,
                         new Color(0, 80, 180, 55), new Color(0, 0, 0, 0));
                paintOrb(g2, getWidth() * 0.85f, getHeight() * 0.8f,  180,
                         new Color(0, 160, 140, 45), new Color(0, 0, 0, 0));
 
                // Cuadrícula punteada sutil
                g2.setColor(new Color(255, 255, 255, 8));
                for (int x = 0; x < getWidth(); x += 32)
                    g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 32)
                    g2.drawLine(0, y, getWidth(), y);
            }
            private void paintOrb(Graphics2D g2, float cx, float cy,
                                   float r, Color c1, Color c2) {
                RadialGradientPaint rg = new RadialGradientPaint(
                    cx, cy, r, new float[]{0f, 1f}, new Color[]{c1, c2});
                g2.setPaint(rg);
                g2.fillOval((int)(cx-r), (int)(cy-r), (int)(r*2), (int)(r*2));
            }
        };
        raiz.setOpaque(false);
 
        // ── Tarjeta central ─────────────────────────────────────────
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra difusa
                for (int i = 8; i > 0; i--) {
                    int alpha = (int)(6.0 * i);
                    g2.setColor(new Color(0, 0, 0, alpha));
                    g2.fillRoundRect(-i, -i,
                        getWidth() + i*2, getHeight() + i*2, 30+i, 30+i);
                }
                // Relleno tarjeta
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                // Borde sutil
                g2.setColor(new Color(255, 255, 255, 18));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                // Línea cian superior
                g2.setPaint(new GradientPaint(0, 0, ACENTO_CIAN,
                            getWidth(), 0, ACENTO_AZUL));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(36, 0, getWidth()-36, 0);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(410, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 44, 36, 44));
 
        // ── Logo / título ────────────────────────────────────────────
        JLabel lblLogo = new JLabel("⬡");
        lblLogo.setFont(new Font("Serif", Font.PLAIN, 42));
        lblLogo.setForeground(ACENTO_CIAN);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel lblTitulo = new JLabel("Graf-icador Pro");
        lblTitulo.setFont(loadFont("Monospaced", Font.BOLD, 26));
        lblTitulo.setForeground(TEXTO_CLARO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel lblSub = new JLabel("Explorador Dijkstra & Kruskal");
        lblSub.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lblSub.setForeground(ACENTO_CIAN);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // ── Separador decorativo ─────────────────────────────────────
        JPanel separador = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0,0),
                    getWidth()/2, 0, BORDE_INPUT));
                g2.fillRect(0, getHeight()/2, getWidth()/2, 1);
                g2.setPaint(new GradientPaint(getWidth()/2, 0, BORDE_INPUT,
                    getWidth(), 0, new Color(0,0,0,0)));
                g2.fillRect(getWidth()/2, getHeight()/2, getWidth()/2, 1);
            }
        };
        separador.setOpaque(false);
        separador.setPreferredSize(new Dimension(302, 16));
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
 
        // ── Campo usuario ────────────────────────────────────────────
        JLabel lUser = crearEtiqueta("USUARIO");
        txtUsuario   = crearCampoTexto("Ingrese usuario");
        lUser.setAlignmentX(Component.CENTER_ALIGNMENT); 
        txtUsuario.setMaximumSize(new Dimension(280, 44)); 
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsuario.setHorizontalAlignment(JTextField.CENTER);
        txtUsuario.setBorder(new CompoundBorder(
                new LineBorder(BORDE_INPUT, 1, true),
                new EmptyBorder(8, 12, 8, 30)));
        lblMostrarPass = new JLabel("  ");
        lblMostrarPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblMostrarPass.setBorder(new EmptyBorder(0, 5, 0, 0));
 
        // ── Campo contraseña ─────────────────────────────────────────
        JLabel lPass   = crearEtiqueta("CONTRASEÑA");
        lPass.setAlignmentX(Component.CENTER_ALIGNMENT); 
        JPanel passRow = new JPanel(new BorderLayout(0, 0));
        passRow.setOpaque(false);
        passRow.setMaximumSize(new Dimension(280, 40));
        passRow.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        txtPassword = new JPasswordField();
        estilizarInput(txtPassword);
        txtPassword.setEchoChar('●');
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
 
        lblMostrarPass = new JLabel("👁");
        lblMostrarPass.setForeground(TEXTO_APAGADO);
        lblMostrarPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblMostrarPass.setFont(new Font("Serif", Font.PLAIN, 15));
        lblMostrarPass.setBorder(new EmptyBorder(0, 6, 0, 0));
        lblMostrarPass.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mostrandoPass = !mostrandoPass;
                txtPassword.setEchoChar(mostrandoPass ? (char)0 : '●');
                lblMostrarPass.setForeground(mostrandoPass ? ACENTO_CIAN : TEXTO_APAGADO);
            }
        });
        passRow.add(txtPassword, BorderLayout.CENTER);
        passRow.add(lblMostrarPass, BorderLayout.EAST);
 
        // ── Mensaje de error ─────────────────────────────────────────
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblError.setForeground(ROJO_ERROR);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // ── Botón ingresar ───────────────────────────────────────────
        btnIngresar = new JButton("INGRESAR") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = getModel().isPressed()
                    ? new Color(0, 140, 120)
                    : (getModel().isRollover()
                        ? new Color(0, 220, 200)
                        : ACENTO_CIAN);
                Color c2 = getModel().isPressed()
                    ? new Color(30, 80, 200)
                    : (getModel().isRollover()
                        ? new Color(80, 140, 255)
                        : ACENTO_AZUL);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
            }
        };
        btnIngresar.setOpaque(false);
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
        // ── Pie de tarjeta ───────────────────────────────────────────
        JLabel lblPie = new JLabel("Acceso exclusivo para administradores");
        lblPie.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblPie.setForeground(TEXTO_APAGADO);
        lblPie.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // ── Botón cerrar (X) esquina ─────────────────────────────────
        JButton btnX = new JButton("✕") {
            @Override 
            protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 60, 60, 180));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
            }
        };
        btnX.setForeground(TEXTO_APAGADO);
        btnX.setFont(new Font("Dialog", Font.PLAIN, 13));
        btnX.setOpaque(false); btnX.setContentAreaFilled(false);
        btnX.setBorderPainted(false); btnX.setFocusPainted(false);
        btnX.setBounds(getWidth()-40, 8, 28, 28);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> System.exit(0));
 
        // ── Armar tarjeta ────────────────────────────────────────────
        card.add(lblLogo);
        card.add(Box.createVerticalStrut(8));
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(4));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(18));
        card.add(separador);
        card.add(Box.createVerticalStrut(22));
        card.add(lUser);
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsuario);
        card.add(Box.createVerticalStrut(16));
        card.add(lPass);
        card.add(Box.createVerticalStrut(6));
        card.add(passRow);
        card.add(Box.createVerticalStrut(10));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(btnIngresar);
        card.add(Box.createVerticalStrut(16));
        card.add(lblPie);
 
        // Añadir botón X sobre el card (layout absoluto del raíz)
        raiz.add(card);
        setContentPane(raiz);
 
        // Posicionar botón X (hacerlo en glass pane)
        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);
        glass.setVisible(true);
 
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                btnX.setBounds(getWidth()-48, 10, 30, 30);
                glass.add(btnX);
                glass.revalidate();
            }
        });
 
        // ── Arrastrar ventana ────────────────────────────────────────
        final int[] drag = {0, 0};
        raiz.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                drag[0] = e.getX(); drag[1] = e.getY();
            }
        });
        raiz.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - drag[0],
                            getY() + e.getY() - drag[1]);
            }
        });
 
        // ── Lógica de autenticación ──────────────────────────────────
        ActionListener loginAction = e -> intentarLogin();
        btnIngresar.addActionListener(loginAction);
        txtPassword.addActionListener(loginAction);
        txtUsuario.addActionListener(e -> txtPassword.requestFocus());
 
        // Animación de entrada
        setOpacity(0f);
        setVisible(true);
        Timer fadeIn = new Timer(16, null);
        final float[] op = {0f};
        fadeIn.addActionListener(e -> {
            op[0] += 0.06f;
            if (op[0] >= 1f) { op[0] = 1f; ((Timer)e.getSource()).stop(); }
            setOpacity(op[0]);
        });
        fadeIn.start();
 
        txtUsuario.requestFocusInWindow();
    }
 
    // ── Helpers de UI ───────────────────────────────────────────────
 
    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setForeground(TEXTO_APAGADO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
 
    private JTextField crearCampoTexto(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXTO_APAGADO);
                    g2.setFont(getFont());
                    Insets i = getInsets();
                    g2.drawString(placeholder, i.left, getHeight()/2
                        + g2.getFontMetrics().getAscent()/2 - 1);
                }
            }
        };
        estilizarInput(tf);
        return tf;
    }
 
    private void estilizarInput(JTextField tf) {
        tf.setBackground(new Color(28, 34, 58));
        tf.setForeground(TEXTO_CLARO);
        tf.setCaretColor(ACENTO_CIAN);
        tf.setFont(new Font("Monospaced", Font.PLAIN, 14));
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDE_INPUT, 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        // Resaltar borde en foco
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(new CompoundBorder(
                    new LineBorder(BORDE_FOCUS, 1, true),
                    new EmptyBorder(8, 12, 8, 12)));
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(new CompoundBorder(
                    new LineBorder(BORDE_INPUT, 1, true),
                    new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }
 
    private Font loadFont(String fallback, int style, int size) {
        return new Font(fallback, style, size);
    }
 
    // ── Autenticación ────────────────────────────────────────────────
    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String pass    = new String(txtPassword.getPassword()).trim();
 
        if (usuario.isEmpty() || pass.isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }
        if (usuario.equals(USUARIO_ADMIN) && pass.equals(PASS_ADMIN)) {
            lblError.setText(" ");
            // Fade out y abrir ventana principal
            Timer fadeOut = new Timer(16, null);
            final float[] op = {1f};
            fadeOut.addActionListener(ev -> {
                op[0] -= 0.07f;
                if (op[0] <= 0f) {
                    op[0] = 0f;
                    ((Timer) ev.getSource()).stop();
                    dispose();
                    SwingUtilities.invokeLater(() -> new Main());
                }
                setOpacity(op[0]);
            });
            fadeOut.start();
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            sacudir();
        }
    }
 
    private void mostrarError(String msg) {
        lblError.setText(msg);
        txtPassword.setBorder(new CompoundBorder(
            new LineBorder(ROJO_ERROR, 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        Timer t = new Timer(2000, e -> {
            lblError.setText(" ");
            txtPassword.setBorder(new CompoundBorder(
                new LineBorder(BORDE_INPUT, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        });
        t.setRepeats(false); t.start();
    }
 
    /** Animación de sacudida horizontal */
    private void sacudir() {
        final int[] offsets = {10, -10, 8, -8, 5, -5, 2, -2, 0};
        final int origX = getX();
        final int[] i = {0};
        Timer t = new Timer(35, null);
        t.addActionListener(e -> {
            setLocation(origX + offsets[i[0]], getY());
            i[0]++;
            if (i[0] >= offsets.length) {
                setLocation(origX, getY());
                ((Timer) e.getSource()).stop();
            }
        });
        t.start();
    }
 
    // ── Punto de entrada ─────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(VentanaLogin::new);
    }
}
