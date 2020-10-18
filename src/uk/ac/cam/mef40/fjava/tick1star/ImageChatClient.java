/*
 * Copyright 2020 Andrew Rice <acr31@cam.ac.uk>, Alastair Beresford <arb33@cam.ac.uk>, M.E. Finch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.mef40.fjava.tick1star;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageChatClient extends JFrame {
    public static void main(String[] args) {
        String server = "example";
        int port = 0;

        var client = new ImageChatClient(server, port);
    }

    public ImageChatClient(String server, int port) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        // create a swing gui with an AWT canvas and an AWT button labelled 'Upload'
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Image Chat");

        // SET UP CANVAS
        var canvasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        final Canvas canvas = new Canvas();
        canvas.setSize(400, 400);
        canvas.setBackground(new Color(255,255,255));
        canvasPanel.add(canvas);

        // SET UP UPLOAD BUTTON
        var uploadPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var button = new Button("Upload");
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG images", "jpg", "jpeg");
            chooser.setFileFilter(filter);
            int img = chooser.showOpenDialog(this.getParent());
            if (img == JFileChooser.APPROVE_OPTION) {
                System.out.println("Uploaded " + chooser.getSelectedFile().getName());
                // canvas.getGraphics().drawBytes();
            }
        });
        uploadPanel.add(button);

        // LAYOUT CANVAS AND BUTTON
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(canvasPanel, BorderLayout.NORTH);
        borderPanel.add(uploadPanel, BorderLayout.SOUTH);

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container.add(borderPanel);
        this.getContentPane().add(container);

        this.pack();
        this.setResizable(false);
        this.setVisible(true);

        // use one thread to listen on a socket object connected to server and
        // draw any image sent by the server to the canvas object
        // Thread output =

        // use other thread to listen for user input
    }

}
