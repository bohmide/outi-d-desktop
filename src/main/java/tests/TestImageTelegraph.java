package tests;

import utils.TelegraphUploader;

import java.io.File;
import java.io.IOException;

public class TestImageTelegraph {
    public static void main(String[] args) {
        try {
            File image = new File("src/main/resources/images/image1.png");
            String imageUrl = TelegraphUploader.uploadImageToTelegraph(image);
            System.out.println("Telegraph Image URL: " + imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
