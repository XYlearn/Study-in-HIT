package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by XHWhy on 2017/6/13.
 */
public class ImageConverter {
    public static byte[] image_to_bytes(Image image) {
        byte[] dest;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedImage bufferedImage = image_to_buffered(image);
        try {
            ImageIO.write(bufferedImage, "png", out);
        } catch (IOException e){
            e.printStackTrace();
        }
        dest = out.toByteArray();
        return  dest;
    }

    public static BufferedImage image_to_buffered(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        return bufferedImage;
    }

    public static Image bytes_to_image(byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            bufferedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        }

        return (Image)bufferedImage;
    }
}
