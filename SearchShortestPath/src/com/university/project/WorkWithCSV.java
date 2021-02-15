package com.university.project;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class WorkWithCSV {
    List<String[]> importFile(JFrame frame) {
        List<String[]> rows = new ArrayList<>();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV (*.csv)", "csv"));
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            String file = fileChooser.getSelectedFile().getName();
            String[] splitData = file.split("\\.");
            if (splitData.length > 0) {
                if (splitData[1].equalsIgnoreCase("csv")) {
                    try {
                        rows = (readCSV(file));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, "Файл некорректно заполнен!");
                    }
                }
            }
        }

        return rows;
    }

   List<String[]> readCSV(String file) throws Exception {
        List<String[]> rows;
        char separator = defineSeparator(file);
        CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
        CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();
        rows = reader.readAll();
        checkCorrectedCSVFile(rows);
        return rows;
    }

    private char defineSeparator(String file) {
        String separator = null;
        try (Scanner scanner = new Scanner(new File(file))) {
            separator = scanner.findInLine("[/,!.;:#\\-]");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(separator).charAt(0);
    }

    private void checkCorrectedCSVFile(List<String[]> rows) throws Exception {
        String[][] matrix = listToMatrix(rows);
        checkCorrectedMatrix(matrix);

    }

    private String[][] listToMatrix(List<String[]> rows) {
        final int SIZE = rows.size();
        String[][] array = new String[SIZE][SIZE];

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).length; j++) {
                array[i][j] = rows.get(i)[j];
            }
        }
        return array;
    }

    private void checkCorrectedMatrix(String[][] matrix) throws Exception {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != null) {
                    if(!matrix[j][i].equals(matrix[i][j])) {
                        throw new Exception();
                    }
                }
            }
        }

        for (int i = 1; i < matrix.length; i++) {
            boolean thereIsASymbol = false;
            boolean thereIsANumber = false;
            for (int j = 1; j < matrix.length; j++) {
                if (matrix[i][j].matches("\\d+(\\.\\d+)?")) {
                    thereIsANumber = true;
                } else if ((matrix[i][j].equals("@") || matrix[i][j].equals("+") || matrix[i][j].equals("=") || matrix[i][j].equals("%"))) {
                    thereIsASymbol = true;
                }
                if (thereIsANumber && thereIsASymbol) throw new Exception();
            }
        }
    }
}
