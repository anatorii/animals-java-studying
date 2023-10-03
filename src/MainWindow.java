import org.json.JSONArray;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


public class MainWindow extends JFrame {
    private static int width = 800;
    private static int height = 600;
    private JPanel panel;
    private JButton catsButton;
    private JButton foxesButton;
    private JButton dogsButton;
    private JPanel imagePanel;
    int countFoxes = 0;

    public MainWindow() {
        super("Загрузить картинку");

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(MainWindow.width, MainWindow.height);
        this.setLocation(d.width / 2 - MainWindow.width / 2, d.height / 2 - MainWindow.height / 2);
        this.getContentPane().add(panel);

        catsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    putImage(0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        foxesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    putImage(1);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        dogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    putImage(2);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void putImage(int i) throws IOException {
        URL url;
        switch (i) {
            case 0: url = getCatUrl(); break;
            case 1: url = getFoxUrl(); break;
            case 2: url = getDogUrl(); break;
            default: url = null;
        }
        if (url == null) {
            return;
        }
        Image image = ImageIO.read(url);
        imagePanel.getGraphics().clearRect(0, 0, imagePanel.getSize().width, imagePanel.getSize().height);
        imagePanel.getGraphics().drawImage(image, 20, 20, null);
    }

    private URL getDogUrl() throws IOException {
        String response = getResponse("https://dog.ceo/api/breeds/image/random");
        JSONArray json = new JSONArray("[" + response + "]");
        System.out.println((String) json.getJSONObject(0).get("message"));
        return new URL((String) json.getJSONObject(0).get("message"));
    }

    private URL getFoxUrl() throws IOException {
        if (countFoxes == 0) {
            countFoxes = Integer.parseInt(getResponse("https://foxes.cool/counts/fox"));
        }
        int number = (int) (Math.random() * countFoxes);
        System.out.println("https://img.foxes.cool/fox/" + number + ".jpg?width=800&height=600");
        return new URL("https://img.foxes.cool/fox/" + number + ".jpg?width=800&height=600");
    }

    private URL getCatUrl() throws IOException {
        String response = getResponse("https://cataas.com/cat?json=true");
        JSONArray json = new JSONArray("[" + response + "]");
        System.out.println("https://cataas.com" + json.getJSONObject(0).get("url"));
        return new URL("https://cataas.com" + json.getJSONObject(0).get("url"));
    }

    String getResponse(String uri) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.setRequestProperty("Content-Type", "application/json");

        InputStream responseStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);

        return bufferedReader.lines().collect(Collectors.joining(""));
    }
}
