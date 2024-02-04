import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class MagpieCounter {
    private BufferedImage testImage;
    private BufferedImage referenceImage;
    private int[][] testImageBinaryArray;
    private int[][] referenceImageBinaryArray;

    public MagpieCounter(String testImageFileName, String referenceImageFileName) {
        //test image
        testImage = loadImage(testImageFileName);

        //reference image
        referenceImage = loadImage(referenceImageFileName);

        //test image to binary array
        if (testImage != null) {
            testImageBinaryArray = imageToArray(testImage);
        }

        //reference image to binary array
        if (referenceImage != null) {
            referenceImageBinaryArray = imageToArray(referenceImage);
        }
    }

    private BufferedImage loadImage(String fileName) {
        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private int[][] imageToArray(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] binaryArray = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = img.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                int threshold = 128;

                if ((red + green + blue) / 3 > threshold) {
                    binaryArray[x][y] = 1; //Whitet pixel
                } else {
                    binaryArray[x][y] = 0; //Dark pixel
                }
            }
        }
        return binaryArray;
    }

    public List<MagpieCoordinates> getMagpies() {
        List<MagpieCoordinates> magpieList = new ArrayList<>();

        if (testImageBinaryArray == null) {
            System.err.println("Test image is not loaded.");
            return magpieList;
        }

        if (referenceImageBinaryArray == null) {
            System.err.println("Rreference image is not loaded.");
            return magpieList;
        }

        int testWidth = testImageBinaryArray.length;
        int testHeight = testImageBinaryArray[0].length;
        int refWidth = referenceImageBinaryArray.length;
        int refHeight = referenceImageBinaryArray[0].length;

        if (testWidth < refWidth || testHeight < refHeight) {
            System.err.println("Reference image dimensions are larger than the test image.");
            return magpieList;
        }

        for (int x = 0; x <= testWidth - refWidth; x++) {
            for (int y = 0; y <= testHeight - refHeight; y++) {
                if (isMagpieAtLocation(x, y, refWidth, refHeight)) {
                    magpieList.add(new MagpieCoordinates(x, y));
                }
            }
        }

        System.out.println("Number of magpies found: " + magpieList.size());
        return magpieList;
    }

    private boolean isMagpieAtLocation(int x, int y, int refWidth, int refHeight) {
        for (int i = 0; i < refWidth; i++) {
            for (int j = 0; j < refHeight; j++) {
                if (testImageBinaryArray[x + i][y + j] != referenceImageBinaryArray[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    //This ClearImage method shows white pixel blocks at the location where magpies are present.

    public int[][] clearImage() {
        if (testImageBinaryArray == null) {
            System.err.println("Test image has not been loaded.");
            return null;
        }

        if (referenceImageBinaryArray == null) {
            System.err.println("Reference image has not been loaded.");
            return null;
        }

        int testWidth = testImageBinaryArray.length;
        int testHeight = testImageBinaryArray[0].length;
        int refWidth = referenceImageBinaryArray.length;
        int refHeight = referenceImageBinaryArray[0].length;

        if (testWidth < refWidth || testHeight < refHeight) {
            System.err.println("Reference image dimensions are larger than the test image.");
            return null;
        }

        int[][] clearedImageArray = new int[testWidth][testHeight];

        for (int x = 0; x <= testWidth - refWidth; x++) {
            for (int y = 0; y <= testHeight - refHeight; y++) {
                if (isMagpieAtLocation(x, y, refWidth, refHeight)) {
                    for (int i = 0; i < refWidth; i++) {
                        for (int j = 0; j < refHeight; j++) {
                            clearedImageArray[x + i][y + j] = 1; //Creating White Pixel Blocks
                        }
                    }
                }
            }
        }

        return clearedImageArray;
    }

    //This ClearImage method shows the Magpies in the saved image.
    public int[][] clearImage2() {
        if (testImageBinaryArray == null) {
            System.err.println("Test image has not been loaded.");
            return null;
        }

        if (referenceImageBinaryArray == null) {
            System.err.println("Reference image has not been loaded.");
            return null;
        }

        int testWidth = testImageBinaryArray.length;
        int testHeight = testImageBinaryArray[0].length;
        int refWidth = referenceImageBinaryArray.length;
        int refHeight = referenceImageBinaryArray[0].length;

        if (testWidth < refWidth || testHeight < refHeight) {
            System.err.println("Reference image dimensions are larger than the test image.");
            return null;
        }

        int[][] clearedImageArray2 = new int[testWidth][testHeight];

        for (int x = 0; x <= testWidth - refWidth; x++) {
            for (int y = 0; y <= testHeight - refHeight; y++) {
                if (isMagpieAtLocation(x, y, refWidth, refHeight)) {
                    for (int i = 0; i < refWidth; i++) {
                        for (int j = 0; j < refHeight; j++) {
                            clearedImageArray2[x + i][y + j] = referenceImageBinaryArray[i][j]; 
                        }
                    }
                }
            }
        }

        return clearedImageArray2;
    }
    

    public void displayImage(int[][] array, String outputFileName) {
        if (array == null) {
            System.err.println("Input array is null. Cannot create and save the image.");
            
            return;
        }

        int width = array.length;
        int height = array[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (array[x][y] == 1) {
                    Color white = new Color(255, 255, 255);
                    int rgb = white.getRGB();
                    image.setRGB(x, y, rgb);
                }
            }
        }

        try {
            File output = new File(outputFileName);
            ImageIO.write(image, "tif", output);
            System.out.println("Image saved as " + outputFileName);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static class MagpieCoordinates {
        int x;
        int y;

        MagpieCoordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        MagpieCounter magpieCounter = new MagpieCounter("testimage.tif", "refimage.tif");
        List<MagpieCoordinates> magpieList = magpieCounter.getMagpies();
        

        //clearImage() to display white pixel Blocks at magpie locations
        int[][] clearedImageArray = magpieCounter.clearImage();
        magpieCounter.displayImage(clearedImageArray, "clearedimage.tif");

        //clearImage2() to display the magpies
        if(clearedImageArray!=null){
        int[][] clearedImageArray2 = magpieCounter.clearImage2();
        magpieCounter.displayImage(clearedImageArray2, "clearedimage2.tif");}
    }
}
