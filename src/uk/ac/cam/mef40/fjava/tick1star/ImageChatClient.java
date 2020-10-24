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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ImageChatClient extends JFrame {
    public static void main(String[] args) {
        String server = null;
        int port = 0;

        // Validate input

        try {
            server = args[0];
            port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("This application requires two arguments: <machine> <port>");
            return;
        }

        var client = new ImageChatClient(server, port);
    }

    public ImageChatClient(String server, int port) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Image Chat");

        // use one thread to listen on a socket object connected to server and
        // draw any image sent by the server to the canvas object
        try {
            final Socket s = new Socket(server, port);

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
                int uploaded = chooser.showOpenDialog(this.getParent());
                if (uploaded == JFileChooser.APPROVE_OPTION) {
                    // replace with send to server
                    System.out.println("Uploaded " + chooser.getSelectedFile().getName());
                    try {
                        RenderedImage img = ImageIO.read(chooser.getSelectedFile());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(img, "jpg", baos);
                        s.getOutputStream().write(baos.toByteArray());
                        // canvas.getGraphics().drawImage(img, 0,0, null);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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

            Thread output = new Thread() {
                @Override
                public void run() {
                    try {
                        // Listen for images sent by the server
                        byte[] buffer = new byte[1024];
                        InputStream is = s.getInputStream();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // Read the data from the socket into a small buffer
                        // Perform some manual checks to see if end of JPEG reached
                        // Then write data out to a second stream to feed into ImageIO.read(InputStream is).

                        byte prev = 0; // Initialise to anything not -1
                        boolean eof = false; // true if -1, 39 seen
                        int imgAt = 0;

                        while (true) {
                            int bytesRead = is.read(buffer);
                            // check whether two consecutive bytes have -1, 39

                            // Draw image
                            if (bytesRead > 0) {
                                for (int i = 0; !eof && i < bytesRead; i++) {
                                    if (prev == -1 && buffer[i] == -39) {
                                        eof = true; // image found
                                        imgAt = i + 1;
                                    }

                                    prev = buffer[i];
                                }

                                if (eof) {
                                    baos.write(buffer, 0, imgAt);
                                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                                    Image img = ImageIO.read(bais);
                                    canvas.getGraphics().drawImage(img, 0,0, null);
                                    baos.reset();
                                    // baos.write(buffer, imgAt, bytesRead - imgAt);
                                } else {
                                    baos.write(buffer, 0, bytesRead);
                                }
                            }

                            eof = false;
                        }

                    } catch (IOException e) {
                        System.err.println("Could not read data from server.");
                        return;
                    }
                }
            };
            output.setDaemon(true);
            output.start();
        } catch (IOException e) {
            System.err.format("Cannot connect to %s on port %d\n", server, port);
        }


        // use other thread to listen for user input
    }

}
