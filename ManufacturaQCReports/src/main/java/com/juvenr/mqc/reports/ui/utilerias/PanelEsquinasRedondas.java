/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juvenr.mqc.reports.ui.utilerias;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author carli
 */
public class PanelEsquinasRedondas extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 15;

    // Default constructor
    public PanelEsquinasRedondas() {
        this.backgroundColor = new Color(100, 149, 237); // Light blue background
        this.cornerRadius = 30;
        setOpaque(false); // This makes the background transparent
    }

    // Constructor with parameters
    public PanelEsquinasRedondas(Color bgColor, int radius) {
        this.backgroundColor = bgColor;
        this.cornerRadius = radius;
        setOpaque(false); // This makes the background transparent
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing for smooth rounded corners
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the background color and draw the rounded rectangle
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        g2.dispose(); // Release the Graphics2D object
    }

    @Override
    protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing for smooth borders
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the border with rounded corners
        g2.setColor(Color.BLACK); // Border color
        g2.setStroke(new BasicStroke(2)); // Border thickness
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        g2.dispose(); // Release the Graphics2D object
    }

    // Optional setters and getters for corner radius and background color
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint(); // Request a re-draw of the component
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint(); // Request a re-draw of the component
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}