package com.example.demo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.*;
import javafx.scene.image.Image;


import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        HBox h1 = new HBox();
        HBox h2 = new HBox();
        ImageView original = new ImageView();
        ImageView result = new ImageView();
        original.setFitWidth (300);
        original.setFitHeight (500);
        result.setFitWidth (300);
        result.setFitHeight(500);

        Button img = new Button ("choose image");
        ComboBox <String> choise = new ComboBox <> ();
        choise.getItems ().addAll("Grayscale", "Inverse", "Blur");
        Button processing =  new Button("process");
        Button save = new Button("save");
        root.getChildren().addAll(h1, h2, img, choise, processing, save, original, result);


        img.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                Image image = new Image(file.toURI().toString());
                original.setImage(image);
                Image resultImage = new Image(file.toURI().toString());
                result.setImage(resultImage);
            }
                });
        processing.setOnAction(e -> {
            Image image = original.getImage();
            String selected = choise.getValue();

            if (image != null && selected != null) {
                Image processed = null;

                switch (selected) {
                    case "Inverse":
                        processed = processInverse(image);
                        break;
                    case "Grayscale":
                        processed = processGrayscale(image);
                        break;
                    case "Blur":
                        processed = processBlur(image);
                        break;
                }

                if (processed != null) {
                    result.setImage(processed);
                }
            }
        });

        Scene scene = new Scene(root, 1400, 1800);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static Image processInverse(Image a) {
        int width = (int) a.getWidth();
        int height = (int) a.getHeight();

        PixelReader Reader = a.getPixelReader();
        Pixel[][] pixels = new Pixel[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = Reader.getArgb(x, y);

                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = (argb) & 0xff;

                int invred = 255 - red;
                int invgreen = 255 - green;
                int invblue = 255 - blue;

                pixels[y][x] = new Pixel(invred, invgreen, invblue);
            }
        }
        return pixelArrayToImage(pixels, width, height);
    }
    public static Image processGrayscale(Image a) {
        int width = (int) a.getWidth();
        int height = (int) a.getHeight();

        PixelReader reader = a.getPixelReader();
        Pixel[][] pixels = new Pixel[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = argb & 0xff;

                int gray = (red + green + blue) / 3;
                pixels[y][x] = new Pixel(gray, gray, gray);
            }
        }

        return pixelArrayToImage(pixels, width, height);
    }
    public static Image processBlur(Image a) {
        int width = (int) a.getWidth();
        int height = (int) a.getHeight();

        PixelReader reader = a.getPixelReader();
        Pixel[][] originalPixels = new Pixel[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = argb & 0xff;

                originalPixels[y][x] = new Pixel(red, green, blue);
            }
        }

        Pixel[][] blurred = new Pixel[height][width];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int r = 0, g = 0, b = 0;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        Pixel p = originalPixels[y + dy][x + dx];
                        r += p.getRed();
                        g += p.getGreen();
                        b += p.getBlue();
                    }
                }

                blurred[y][x] = new Pixel(r / 9, g / 9, b / 9);
            }
        }

        for (int y = 0; y < height; y++) {
            blurred[y][0] = originalPixels[y][0];
            blurred[y][width - 1] = originalPixels[y][width - 1];
        }
        for (int x = 0; x < width; x++) {
            blurred[0][x] = originalPixels[0][x];
            blurred[height - 1][x] = originalPixels[height - 1][x];
        }

        return pixelArrayToImage(blurred, width, height);
    }

    // Общая функция для сборки изображения из массива Pixel из чата гпт т.к. я не достаточно умная т.е. тупая ))
    private static Image pixelArrayToImage(Pixel[][] pixels, int width, int height) {
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter writer = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel p = pixels[y][x];
                writer.setColor(x, y, Color.rgb(p.getRed(), p.getGreen(), p.getBlue()));
            }
        }



        return  writableImage;

    }

    public static void main(String[] args) {
        launch();
    }
}