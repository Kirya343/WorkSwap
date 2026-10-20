package org.workswap.storage.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

@Service
public class ImageConvertService {

    public byte[] convertToWebp(MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            throw new RuntimeException("Empty file");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only images allowed");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        var writers = ImageIO.getImageWritersByMIMEType("image/webp");
        if (!writers.hasNext()) {
            throw new RuntimeException("WebP writer not found");
        }

        var writer = writers.next();
        var ios = ImageIO.createImageOutputStream(output);

        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), null);

        ios.close();
        writer.dispose();

        return output.toByteArray();
    }
}