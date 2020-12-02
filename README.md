# Part IB Further Java: Image Chat Client
A simple image chat client written for ticklet 1* of the Part IB Further Java course.

## :clipboard: Task

> Write a client which connects to a server and sends and receives images in JPEG format.

Your program should have the following features:

1. It should contain a class called `ImageChatClient` which extends `JFrame`.
1. `ImageChatClient` must contain a constructor which takes the name of the server as a `String` and the port number to connect to as an `int`.
1. When an object of type `ImageChatClient` is instantiated it should create a Swing GUI with an AWT `Canvas` object to render images sent by the server, and an AWT `Button` with the text label "Upload".
1. If the `Button` object is clicked, you should create and show an instance of the `JFileChooser` class. Your program should send the bytes representing the JPEG file selected by the user to the server over a `Socket` object.
1. Clicking on the close button in the corner of the Swing window should close the application.
