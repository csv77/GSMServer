package gsmserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Locale;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GSM extends Application {
    private ServerSocket serverSocket;
    private Socket socket;
    private TextArea taStatus = new TextArea();
    private Thread thread;
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(new ScrollPane(taStatus));
        
        Button btStart = new Button("Start Server");
        Button btStop = new Button("Stop Server");
        Button btClear = new Button("Clear Window");
        
        HBox hBoxForButtons = new HBox(10);
        hBoxForButtons.getChildren().addAll(btStart, btStop, btClear);
        hBoxForButtons.setAlignment(Pos.CENTER);
        borderPane.setBottom(hBoxForButtons);
        
        Scene scene = new Scene(borderPane, 500, 200);
        primaryStage.setTitle("GSMServer");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        thread = new Thread(new TaskClass());
        thread.start();
        
        btStart.setOnAction(e -> {
            thread = new Thread(new TaskClass());
            thread.start();
        });
        
        btStop.setOnAction(e -> {
            if(thread != null) {
                try {
                    if(socket != null) {
                        socket.close();
                    }
                    if(serverSocket != null) {
                        serverSocket.close();
                    }
                    thread.join();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException e1) {
					e1.printStackTrace();
				}
                taStatus.appendText("Server was shut down.\n");
            }
        });
        
        btClear.setOnAction(e -> {
        	taStatus.setText("");
        });
    }
    
    private class TaskClass implements Runnable {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(2048);
                Platform.runLater(() -> taStatus.appendText("Server started at " + new Date() + "\n"));
                
                while(true) {
                    socket = serverSocket.accept();
                    InetAddress inetAddress = socket.getInetAddress();
                    
                    Platform.runLater(() -> taStatus.appendText("Connected to a client at " + new Date() + "\n" + 
                            "Client IP address is " + inetAddress.getHostAddress() + "\n"));
                    
                    DBManagement dbManagement = new DBManagement();
                    DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                    
                    while(true) {
                        Byte ch = inputFromClient.readByte();
                        if(ch == '0') {
                            Platform.runLater(() -> taStatus.appendText("Client has disconnected.\n"));
                            break;
                        }
                        
                        Float temperature = inputFromClient.readFloat();
                        Platform.runLater(() -> taStatus.appendText("Received a temperature value from the Client: " + 
                                String.format(Locale.US, "%.2f", temperature) + " C°\n"));

                        Float humidity = inputFromClient.readFloat();
                        Platform.runLater(() -> taStatus.appendText("Received a humidity value from the Client: " + 
                                String.format(Locale.US, "%.2f", humidity) + " %\n"));
                        dbManagement.insertValuesToDB(temperature, humidity);
                        Platform.runLater(() -> taStatus.appendText("Data was loaded into the database.\n"));
                    }
                    dbManagement.closeConnection();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
