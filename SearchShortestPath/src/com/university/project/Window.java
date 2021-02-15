package com.university.project;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.GraphDecorator;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Frame.MAXIMIZED_BOTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Window  {
    private WorkWithCSV workWithCSV = new WorkWithCSV();
    private Logic logic = new Logic();
    private JFrame frame = new JFrame();
    private Graph<String, StringBuilder> graph;
    private JButton btnSearch;
    private JMenu algorithmsMenu;
    private JLabel jLabelAlgorithmRuntime;
    private List<String[]> rows;
    private BasicVisualizationServer<String, StringBuilder> vv;
    private JScrollPane scrollBar;
    private boolean graphWithWeight;

    public Window(String title) {
        frame.setTitle(title);
        createGUI();
    }

    public void createGUI() {
        createWindow();
        ActionListener actionListener = new ListenerAction();

        btnSearch = new JButton("Поиск");
        btnSearch.setVisible(false);
        btnSearch.addActionListener(actionListener);

        JMenuBar jMenuBar = new JMenuBar();

        JMenu fileMenu = createFileMenu();
        for (int i = 0; i < fileMenu.getMenuComponentCount(); i++) {
            fileMenu.getItem(i).addActionListener(actionListener);
        }

        algorithmsMenu = createAlgorithmsMenu();
        for (int i = 0; i < algorithmsMenu.getMenuComponentCount(); i++) {
            algorithmsMenu.getItem(i).addActionListener(actionListener);
        }

        jMenuBar.add(fileMenu);
        jMenuBar.add(algorithmsMenu);
        jMenuBar.add(Box.createHorizontalGlue());
        jMenuBar.add(btnSearch);

        jLabelAlgorithmRuntime = new JLabel();
        jLabelAlgorithmRuntime.setPreferredSize(new Dimension(260, 20));
        jLabelAlgorithmRuntime.setVisible(false);

        JPanel jPanel = new JPanel();
        jPanel.add(jLabelAlgorithmRuntime);
        jPanel.add(btnSearch);

        frame.setLayout(new BorderLayout(5,5));
        frame.add(jMenuBar, BorderLayout.NORTH);
        frame.add(jPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void createWindow() {
        final Dimension SCREEN_RESOLUTION = Toolkit.getDefaultToolkit().getScreenSize();
        final int WINDOW_HEIGHT = SCREEN_RESOLUTION.height / 2;
        final int WINDOW_WIDTH = SCREEN_RESOLUTION.width / 2;

        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JMenu createFileMenu() {
        JMenu jMenuFile = new JMenu("Файл");
        JMenuItem importItem = new JMenuItem("Импорт");
        JMenuItem saveItem = new JMenuItem("Сохранить");
        jMenuFile.add(importItem);
        jMenuFile.add(saveItem);
        return jMenuFile;
    }

    private JMenu createAlgorithmsMenu() {
        JMenu jMenuAlgorithms = new JMenu("Алгоритм");
        JRadioButtonMenuItem takoi1 = new JRadioButtonMenuItem("Дейкстры");
        JRadioButtonMenuItem takoi2 = new JRadioButtonMenuItem("Флойда");
        JRadioButtonMenuItem takoi3 = new JRadioButtonMenuItem("Форда-Беллмана");

        takoi1.setSelected(true);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(takoi1);
        buttonGroup.add(takoi2);
        buttonGroup.add(takoi3);

        jMenuAlgorithms.add(takoi1);
        jMenuAlgorithms.add(takoi2);
        jMenuAlgorithms.add(takoi3);

        return jMenuAlgorithms;
    }

    private void renderGraph(List<String[]> rows) {
        removeScrollbarIfPresentOnFrame();

        graph = new GraphDecorator<>(logic.getGraph(rows));

        Layout<String, StringBuilder> layout = new CircleLayout<>(graph);
        vv = new BasicVisualizationServer<>(layout);

        graphDisplaySetting();

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        jPanel.add(vv, gbc);
        scrollBar = new JScrollPane(jPanel);

        graphWithWeight = false;
        for (StringBuilder stringBuilder : graph.getEdges()) {
            if (!stringBuilder.toString().equals("")) {
                graphWithWeight = true;
                break;
            }
        }

        if (graphWithWeight) {
            jLabelAlgorithmRuntime.setVisible(true);
            jLabelAlgorithmRuntime.setText("Нажмите, для поиска кратчайшего пути:");
            btnSearch.setVisible(true);
        } else {
            jLabelAlgorithmRuntime.setVisible(false);
            btnSearch.setVisible(false);
        }

        frame.add(scrollBar, BorderLayout.CENTER);
        frame.repaint();
        frame.revalidate();
    }

    private void removeScrollbarIfPresentOnFrame() {
        if (frame.isAncestorOf(scrollBar)) {
            frame.remove(scrollBar);
        }
    }

    private void graphDisplaySetting() {
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());

        vv.getRenderContext().setLabelOffset(20);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
    }

    private void redrawGraph(ArrayList<String> distVertex) {
        Transformer<String, Paint> vertexPaint = getColorVertex(distVertex);
        Transformer<StringBuilder, Paint> edgePaint = getColorEdge(distVertex);

        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);

        frame.repaint();
        frame.revalidate();
    }

    private Transformer<String, Paint> getColorVertex(ArrayList<String> distVertex) {
        return i -> {
            for (String vertex : distVertex)
                if (i.equals(vertex))
                    return Color.GREEN;
            return Color.RED;
        };
    }

    private Transformer<StringBuilder, Paint> getColorEdge(ArrayList<String> distVertex) {
        return i -> {
            for (String vertex : distVertex) {
                for (String vertex2 : distVertex) {
                    StringBuilder edge = graph.findEdge(vertex, vertex2);
                    if (i == edge)
                        return Color.GREEN;
                }
            }
            return Color.BLACK;
        };
    }

    private void saveGraph() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Сохранить как: ");
        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Документ word (*.doc)", "doc"));
        jFileChooser.setSelectedFile(new File("Граф.doc"));
        jFileChooser.showSaveDialog(frame);

        VisualizationImageServer<String, StringBuilder> vis = new VisualizationImageServer<>(vv.getGraphLayout(), vv.getGraphLayout().getSize());

        vis.setBackground(Color.WHITE);
        vis.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
        vis.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        vis.getRenderContext().setLabelOffset(20);
        vis.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        BufferedImage imageOneBI = (BufferedImage) vis.getImage(
                new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
                        vv.getGraphLayout().getSize().getHeight() / 2),
                new Dimension(vv.getGraphLayout().getSize()));

        vis.setBackground(Color.WHITE);
        vis.getRenderContext().setEdgeLabelTransformer(vv.getRenderContext().getEdgeLabelTransformer());
        vis.getRenderContext().setVertexFillPaintTransformer(vv.getRenderContext().getVertexFillPaintTransformer());
        vis.getRenderContext().setEdgeDrawPaintTransformer(vv.getRenderContext().getEdgeDrawPaintTransformer());
        vis.getRenderContext().setLabelOffset(vv.getRenderContext().getLabelOffset());
        vis.getRenderer().getVertexLabelRenderer().setPosition(vv.getRenderer().getVertexLabelRenderer().getPosition());

        BufferedImage imageTwoBI = (BufferedImage) vis.getImage(
                new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
                        vv.getGraphLayout().getSize().getHeight() / 2),
                new Dimension(vv.getGraphLayout().getSize()));

        File imageOneF = new File("image1.png");
        File imageTwoF = new File("image2.png");

        try {
            ImageIO.write(imageOneBI, "png", imageOneF);
            ImageIO.write(imageTwoBI, "png", imageTwoF);
            FileInputStream fis1 = new FileInputStream(imageOneF);
            FileInputStream fis2 = new FileInputStream(imageTwoF);

            File file = new File(String.valueOf(jFileChooser.getSelectedFile()));
            ResettableOutputStream out = new ResettableOutputStream(file);
            WorkWithDoc.WriteInDocFile writeInDocFile = new WorkWithDoc.WriteInDocFile(out);

            writeInDocFile.writeImage(fis1);
            writeInDocFile.writeText("Рисунок 1 — Неориентированный граф");

            if (graphWithWeight) {
                writeInDocFile.writeImage(fis2);
                writeInDocFile.writeText("Рисунок 2 — Кратчайший путь в неориентированном графе");
            }

            Files.deleteIfExists(Path.of(imageOneF.getAbsolutePath()));
            Files.deleteIfExists(Path.of(imageTwoF.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     class ListenerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            switch (ae.getActionCommand()) {
                case "Импорт": {
                    rows = workWithCSV.importFile(frame);
                    if (rows.size() != 0) {
                        renderGraph(rows);
                    }
                    break;
                }
                case "Сохранить": {
                    saveGraph();
                    break;
                }
                case "Поиск": {
                    for (int i = 0; i < algorithmsMenu.getMenuComponentCount(); i++) {
                        if (algorithmsMenu.getItem(i).isSelected()) {
                            if (graph != null) {
                                switch (algorithmsMenu.getItem(i).getText()) {
                                    case "Дейкстры": {
                                        double timeStart = System.nanoTime();
                                        redrawGraph(logic.distDijkstra(rows));
                                        double timeEnd = System.nanoTime();
                                        double nanoTime = timeEnd - timeStart;
                                        double milliSecondTime = nanoTime / 1000000;
                                        jLabelAlgorithmRuntime.setText("Время выполнения алгоритма: " + milliSecondTime + " мс.");
                                        break;
                                    }
                                    case "Флойда": {
                                        double timeStart = System.nanoTime();
                                        redrawGraph(logic.distFloyd(rows));
                                        double timeEnd = System.nanoTime();
                                        double nanoTime = timeEnd - timeStart;
                                        double milliSecondTime = nanoTime / 1000000;
                                        jLabelAlgorithmRuntime.setText("Время выполнения алгоритма: " + milliSecondTime + " мс.");
                                        break;
                                    }
                                    case "Форда-Беллмана": {
                                        double timeStart = System.nanoTime();
                                        redrawGraph(logic.distBellmanFord(rows));
                                        double timeEnd = System.nanoTime();
                                        double nanoTime = timeEnd - timeStart;
                                        double milliSecondTime = nanoTime / 1000000;
                                        jLabelAlgorithmRuntime.setText("Время выполнения алгоритма: " + milliSecondTime + " мс.");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
