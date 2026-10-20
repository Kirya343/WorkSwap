package org.workswap.storage.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public static Dimension getImageSize(MultipartFile file) throws Exception {

        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new RuntimeException("Unsupported or corrupted image");
        }

        return new Dimension(image.getWidth(), image.getHeight());
    }

    public record Dimension(int width, int height) {}
}
