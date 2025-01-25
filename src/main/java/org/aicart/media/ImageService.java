package org.aicart.media;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import com.sksamuel.scrimage.webp.WebpWriter;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Singleton
@RegisterForReflection
public class ImageService {

    public byte[] resizeAndConvertToWebp(BufferedImage image, int width) throws IOException {

        // Wrap the BufferedImage with ImmutableImage
        ImmutableImage imageI = ImmutableImage.wrapAwt(image);

        // Resize the image to the desired width and height
        ImmutableImage resizedImage = imageI.scaleToWidth(width, ScaleMethod.Bicubic, true);

        // Return the byte array containing the WebP image data
        return resizedImage.bytes(WebpWriter.MAX_LOSSLESS_COMPRESSION);
    }

    public byte[] resizeAndConvertToWebp(BufferedImage image, int width, int height) throws IOException {

        // Wrap the BufferedImage with ImmutableImage
        ImmutableImage imageI = ImmutableImage.wrapAwt(image);

        // Resize the image to the desired width and height
        ImmutableImage resizedImage = imageI.scaleTo(width, height);

        // Return the byte array containing the WebP image data
        return resizedImage.bytes(WebpWriter.MAX_LOSSLESS_COMPRESSION);
    }
}
