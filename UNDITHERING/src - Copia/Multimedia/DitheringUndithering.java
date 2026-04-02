package Multimedia;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class DitheringUndithering {
    
    public static BufferedImage applyDithering(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage ditheredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                Color oldColor = new Color(image.getRGB(x, y));
                int r = oldColor.getRed();
                int g = oldColor.getGreen();
                int b = oldColor.getBlue();
                
                int newR = (r > 128) ? 255 : 0;
                int newG = (g > 128) ? 255 : 0;
                int newB = (b > 128) ? 255 : 0;
                
                Color newColor = new Color(newR, newG, newB);
                ditheredImage.setRGB(x, y, newColor.getRGB());
                
                int quantErrorR = r - newR;
                int quantErrorG = g - newG;
                int quantErrorB = b - newB;
                
                distributeError(image, x, y, quantErrorR, quantErrorG, quantErrorB);
            }
        }
        return ditheredImage;
    }
    
    private static void distributeError(BufferedImage image, int x, int y, int errR, int errG, int errB) {
        distribute(image, x + 1, y, errR, errG, errB, 7.0 / 16);
        distribute(image, x - 1, y + 1, errR, errG, errB, 3.0 / 16);
        distribute(image, x, y + 1, errR, errG, errB, 5.0 / 16);
        distribute(image, x + 1, y + 1, errR, errG, errB, 1.0 / 16);
    }
    
    private static void distribute(BufferedImage image, int x, int y, int errR, int errG, int errB, double factor) {
        if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
            Color color = new Color(image.getRGB(x, y));
            int newR = Math.min(255, Math.max(0, color.getRed() + (int) (errR * factor)));
            int newG = Math.min(255, Math.max(0, color.getGreen() + (int) (errG * factor)));
            int newB = Math.min(255, Math.max(0, color.getBlue() + (int) (errB * factor)));
            image.setRGB(x, y, new Color(newR, newG, newB).getRGB());
        }
    }
    
    public static double calculateImprovementPercentage(BufferedImage original, BufferedImage dithered) {
        int width = original.getWidth();
        int height = original.getHeight();
        long totalDifference = 0;
        long totalPixels = width * height * 3; // 3 per RGB
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = new Color(original.getRGB(x, y));
                Color ditheredColor = new Color(dithered.getRGB(x, y));
                
                int diffR = Math.abs(originalColor.getRed() - ditheredColor.getRed());
                int diffG = Math.abs(originalColor.getGreen() - ditheredColor.getGreen());
                int diffB = Math.abs(originalColor.getBlue() - ditheredColor.getBlue());
                
                totalDifference += diffR + diffG + diffB;
            }
        }
        
        double percentage = 100.0 - (totalDifference / (double) totalPixels * 100);
        return percentage;
    }
    
    public static void processImage(String inputPath, String outputDitherPath, String outputUnditherPath) throws Exception {
        BufferedImage image = ImageIO.read(new File(inputPath));
        BufferedImage dithered = applyDithering(image);
        BufferedImage undithered = applyDithering(dithered);
        
        double improvement = calculateImprovementPercentage(image, dithered);
        System.out.println("Percentuale di miglioria: " + improvement + "%");
        
        ImageIO.write(dithered, "jpg", new File(outputDitherPath));
        ImageIO.write(undithered, "jpg", new File(outputUnditherPath));
    }
    
    public static void main(String[] args) {
        try {
            processImage("C:\\Users\\cicci\\OneDrive\\Documenti\\Desktop\\immagini_ciccio\\cavalli\\Autunno3_grainy.png", "C:\\Users\\cicci\\OneDrive\\Documenti\\Desktop\\immagini_ciccio\\dithered.png", "C:\\Users\\cicci\\OneDrive\\Documenti\\Desktop\\immagini_ciccio\\undithered.png");
        } catch (Exception e) {
            e.printStackTrace();
}
    }
}