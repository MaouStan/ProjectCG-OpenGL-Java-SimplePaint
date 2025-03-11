package com.sample.paint.util;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

/**
 * Utility class for reading pixel data from OpenGL and saving it to image files
 */
public class GLReadBufferUtil {
    private ByteBuffer pixelBuffer;
    private int width;
    private int height;
    private boolean hasAlpha;

    public GLReadBufferUtil(boolean hasAlpha) {
        this.hasAlpha = hasAlpha;
    }

    /**
     * Read the pixels from the current OpenGL context
     * @return true if reading pixels was successful, false otherwise
     */
    public boolean readPixels(GL gl, boolean flip) {
        // Get the viewport dimensions
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        width = viewport[2];
        height = viewport[3];

        // Validate dimensions before proceeding
        if (width <= 0 || height <= 0) {
            return false;
        }

        // Allocate buffer to hold the pixels
        int bytesPerPixel = hasAlpha ? 4 : 3;
        pixelBuffer = ByteBuffer.allocate(width * height * bytesPerPixel);

        // Read the pixels
        gl.glReadPixels(0, 0, width, height,
                hasAlpha ? GL.GL_RGBA : GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE, pixelBuffer);

        // Flip the image vertically if requested (OpenGL has origin at bottom-left)
        if (flip && pixelBuffer != null) {
            flipImageVertically();
        }

        return true;
    }

    /**
     * Flip the image data vertically
     */
    private void flipImageVertically() {
        int bytesPerPixel = hasAlpha ? 4 : 3;
        int rowSize = width * bytesPerPixel;
        byte[] rowBuffer = new byte[rowSize];

        // Get a copy of the buffer for manipulation
        byte[] data = pixelBuffer.array();

        // Swap rows (top with bottom, etc.)
        for (int i = 0; i < height / 2; i++) {
            int topIdx = i * rowSize;
            int botIdx = (height - i - 1) * rowSize;

            // Copy top row to temp buffer
            System.arraycopy(data, topIdx, rowBuffer, 0, rowSize);

            // Copy bottom row to top
            System.arraycopy(data, botIdx, data, topIdx, rowSize);

            // Copy temp buffer (original top) to bottom
            System.arraycopy(rowBuffer, 0, data, botIdx, rowSize);
        }

        // Update the pixel buffer with flipped data
        pixelBuffer.clear();
        pixelBuffer.put(data);
        pixelBuffer.rewind();
    }

    /**
     * Convert pixel data to a BufferedImage
     */
    public BufferedImage toBufferedImage() {
        if (pixelBuffer == null) {
            throw new GLException("No pixel data available");
        }

        // Validate dimensions before creating image
        if (width <= 0 || height <= 0) {
            throw new GLException("Invalid dimensions: width=" + width + ", height=" + height);
        }

        // Create a BufferedImage with the appropriate type
        int type = hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage image = new BufferedImage(width, height, type);

        // Get the pixel data as bytes
        byte[] bytes = pixelBuffer.array();

        // Fill the image pixel by pixel
        int bytesPerPixel = hasAlpha ? 4 : 3;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = (y * width + x) * bytesPerPixel;
                int argb;

                if (hasAlpha) {
                    // RGBA format
                    int r = bytes[i] & 0xff;
                    int g = bytes[i + 1] & 0xff;
                    int b = bytes[i + 2] & 0xff;
                    int a = bytes[i + 3] & 0xff;
                    argb = (a << 24) | (r << 16) | (g << 8) | b;
                } else {
                    // RGB format
                    int r = bytes[i] & 0xff;
                    int g = bytes[i + 1] & 0xff;
                    int b = bytes[i + 2] & 0xff;
                    argb = (0xff << 24) | (r << 16) | (g << 8) | b;
                }

                image.setRGB(x, y, argb);
            }
        }

        return image;
    }

    /**
     * Write the pixel data to a file
     * @return true if writing was successful, false otherwise
     */
    public boolean write(File file) throws IOException {
        try {
            BufferedImage image = toBufferedImage();
            String formatName = "PNG";
            return ImageIO.write(image, formatName, file);
        } catch (GLException e) {
            return false;
        }
    }
}
