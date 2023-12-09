package entornografico2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class Figura {

  int tipo;
  ArrayList<Point> puntos;
  Color color;

  Figura(int tipo, Color color) {
    this.tipo = tipo;
    this.puntos = new ArrayList<>();
    this.color = color;
  }

  void agregarPunto(Point punto) {
    puntos.add(punto);
  }

  void aplicarEscala(double escalaX, double escalaY) {
    // Calcular el centro de masa
    Point centro = calcularCentroDeMasa();

    // Escalar los puntos alrededor del centro
    for (Point punto : puntos) {
        double x = punto.getX() - centro.getX();
        double y = punto.getY() - centro.getY();

        punto.setLocation(x * escalaX + centro.getX(), y * escalaY + centro.getY());
    }
}

  void aplicarTraslacion(int tx, int ty) {
    for (Point punto : puntos) {
      punto.setLocation(punto.getX() + tx, punto.getY() + ty);
    }
  }

  void aplicarRotacion(double anguloRotacion) {
    // Calcular el centro de masa
    Point centro = calcularCentroDeMasa();

    // Convertir el ángulo a radianes
    double cos = Math.cos(anguloRotacion);
    double sin = Math.sin(anguloRotacion);

    // Rotar los puntos alrededor del centro
    for (Point punto : puntos) {
      double x = punto.getX() - centro.getX();
      double y = punto.getY() - centro.getY();

      punto.setLocation(x * cos - y * sin + centro.getX(), x * sin + y * cos + centro.getY());
    }
  }

  private Point calcularCentroDeMasa() {
    if (puntos.isEmpty()) {
      return new Point(0, 0);
    }

    double sumaX = 0;
    double sumaY = 0;

    for (Point punto : puntos) {
      sumaX += punto.getX();
      sumaY += punto.getY();
    }

    double centroX = sumaX / puntos.size();
    double centroY = sumaY / puntos.size();

    return new Point((int) centroX, (int) centroY);
  }

  void dibujar(Graphics g) {
    g.setColor(color);
    switch (tipo) {
      case 0 ->
        g.drawLine(puntos.get(0).x, puntos.get(0).y, puntos.get(1).x, puntos.get(1).y);
      case 1 -> {
        int[] xPoints = {puntos.get(0).x, puntos.get(1).x, puntos.get(2).x};
        int[] yPoints = {puntos.get(0).y, puntos.get(1).y, puntos.get(2).y};
        g.fillPolygon(xPoints, yPoints, 3);
      }
      case 2 -> {
        int width = Math.abs(puntos.get(1).x - puntos.get(0).x);
        int height = Math.abs(puntos.get(1).y - puntos.get(0).y);
        g.fillRect(puntos.get(0).x, puntos.get(0).y, width, height);
      }
      case 3 -> {
        int radius = (int) Math.hypot(puntos.get(1).x - puntos.get(0).x, puntos.get(1).y - puntos.get(0).y);
        g.fillOval(puntos.get(0).x - radius, puntos.get(0).y - radius, 2 * radius, 2 * radius);
      }
    }
  }

  @Override
  public String toString() {
    return "Figura " + tipo;
  }
}

public class EntornoGrafico2D extends JFrame {

  private ArrayList<Figura> figuras = new ArrayList<>();
  private Figura figuraActual;
  private Color color = Color.BLACK;
  private DefaultListModel<Figura> listModel;
  private JList<Figura> listaFiguras;

  public EntornoGrafico2D() {
    setTitle("Dibujar Figuras");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);

    // Panel izquierdo con botón y lista
    JPanel panelIzquierdo = new JPanel(new BorderLayout());

    // Botón para seleccionar color
    JButton btnColor = new JButton("Seleccionar Color");
    btnColor.addActionListener(e -> {
      color = getColor();
    });

    // Lista de figuras
    listModel = new DefaultListModel<>();
    listaFiguras = new JList<>(listModel);
    JScrollPane scrollPane = new JScrollPane(listaFiguras);

    // Agregar botón y lista al panel izquierdo
    panelIzquierdo.add(btnColor, BorderLayout.NORTH);
    panelIzquierdo.add(scrollPane, BorderLayout.CENTER);

    // Panel superior con botones y panel de lista a la izquierda
    JPanel panelSuperior = new JPanel(new BorderLayout());
    JPanel panelBotones = new JPanel(new FlowLayout());
    JButton btnLinea = new JButton("Línea");
    JButton btnTriangulo = new JButton("Triángulo");
    JButton btnCuadrado = new JButton("Cuadrado");
    JButton btnCirculo = new JButton("Círculo");

    panelBotones.add(btnLinea);
    panelBotones.add(btnTriangulo);
    panelBotones.add(btnCuadrado);
    panelBotones.add(btnCirculo);

    // Agregar panel de la izquierda al panel superior
    panelSuperior.add(panelIzquierdo, BorderLayout.WEST);
    panelSuperior.add(panelBotones, BorderLayout.CENTER);

    // Panel inferior con botones y campos de traslación
    JPanel panelInferior = new JPanel(new FlowLayout());
    JTextField tfTraslacionX = new JTextField(5);
    JTextField tfTraslacionY = new JTextField(5);
    JTextField tfEscalaX = new JTextField(5);
    JTextField tfEscalaY = new JTextField(5);
    JTextField tfAnguloRotacion = new JTextField(5);
    JButton btnTraslacion = new JButton("Traslación");
    JButton btnEscalar = new JButton("Escalar");
    JButton btnRotar = new JButton("Rotar");

    panelInferior.add(new JLabel("Traslación en X: "));
    panelInferior.add(tfTraslacionX);
    panelInferior.add(new JLabel("Traslación en Y: "));
    panelInferior.add(tfTraslacionY);
    panelInferior.add(btnTraslacion);

    panelInferior.add(new JLabel("Escala en X: "));
    panelInferior.add(tfEscalaX);
    panelInferior.add(new JLabel("Escala en Y: "));
    panelInferior.add(tfEscalaY);
    panelInferior.add(btnEscalar);

    panelInferior.add(new JLabel("Ángulo de Rotación: "));
    panelInferior.add(tfAnguloRotacion);
    panelInferior.add(btnRotar);

    // Lienzo para dibujar
    Lienzo lienzo = new Lienzo();
    lienzo.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        lienzo.dibujarFigura(e.getPoint());
        repaint();
      }
    });

    btnLinea.addActionListener(e -> {
      figuraActual = new Figura(0, color);
      figuras.add(figuraActual);
      listModel.addElement(figuraActual);
      repaint();
    });

    btnTriangulo.addActionListener(e -> {
      figuraActual = new Figura(1, color);
      figuras.add(figuraActual);
      listModel.addElement(figuraActual);
      repaint();
    });

    btnCuadrado.addActionListener(e -> {
      figuraActual = new Figura(2, color);
      figuras.add(figuraActual);
      listModel.addElement(figuraActual);
      repaint();
    });

    btnCirculo.addActionListener(e -> {
      figuraActual = new Figura(3, color);
      figuras.add(figuraActual);
      listModel.addElement(figuraActual);
      repaint();
    });

    btnTraslacion.addActionListener(e -> {
      Figura figuraSeleccionada = listaFiguras.getSelectedValue();
      if (figuraSeleccionada != null) {
        try {
          int tx = Integer.parseInt(tfTraslacionX.getText());
          int ty = Integer.parseInt(tfTraslacionY.getText());
          figuraSeleccionada.aplicarTraslacion(tx, ty);
          lienzo.actualizarListaFiguras();
          repaint();
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(null, "Ingrese valores numéricos para traslación.");
        }
      } else {
        JOptionPane.showMessageDialog(null, "Seleccione una figura para aplicar traslación.");
      }
    });

    btnEscalar.addActionListener(e -> {
      if (figuraActual != null) {
        try {
          double escalaX = Double.parseDouble(tfEscalaX.getText());
          double escalaY = Double.parseDouble(tfEscalaY.getText());
          figuraActual.aplicarEscala(escalaX, escalaY);
          lienzo.actualizarListaFiguras();
          repaint();
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(null, "Ingrese valores numéricos para escala.");
        }
      } else {
        JOptionPane.showMessageDialog(null, "Seleccione una figura para aplicar escala.");
      }
    });

    btnRotar.addActionListener(e -> {
      if (figuraActual != null) {
        try {
          double anguloRotacion = Math.toRadians(Double.parseDouble(tfAnguloRotacion.getText()));
          figuraActual.aplicarRotacion(anguloRotacion);
          lienzo.actualizarListaFiguras();
          repaint();
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(null, "Ingrese un valor numérico para el ángulo de rotación.");
        }
      } else {
        JOptionPane.showMessageDialog(null, "Seleccione una figura para aplicar rotación.");
      }
    });

    // Agregar paneles al frame
    add(panelSuperior, BorderLayout.NORTH);
    add(lienzo, BorderLayout.CENTER);
    add(panelInferior, BorderLayout.SOUTH);
  }

  class Lienzo extends JPanel {

    void actualizarListaFiguras() {
      listaFiguras.repaint();
    }

    Lienzo() {
      setBackground(Color.WHITE);
    }

    void dibujarFigura(Point punto) {
      if (figuraActual != null) {
        figuraActual.agregarPunto(punto);
        actualizarListaFiguras();
        repaint();
      }
    }

    void seleccionarFigura() {
      Figura figuraSeleccionada = listaFiguras.getSelectedValue();
      figuraActual = figuraSeleccionada;
      listaFiguras.setSelectedValue(figuraActual, true);  // Actualiza la selección en la lista
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (Figura figura : figuras) {
        figura.dibujar(g);
      }
    }
  }

  Color getColor() {
    return JColorChooser.showDialog(null, "Seleccione un color", color);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new EntornoGrafico2D().setVisible(true);
    });
  }
}
