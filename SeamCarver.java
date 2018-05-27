/******************************************************************************
 * Description: Seam carves (a content-aware image resizing algorithm) a given
 * photo vertically or horizontally. Resizes a W-by-H image using the 
 * seam-carving technique and preserves aspect ratio and objects of interest.
 ******************************************************************************/
import java.awt.Color;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

// A data type that resizes a W-by-H image by assigning all pixels an "energy"
// and removing a seam of lowest energy level pixels
public class SeamCarver 
{
    private Picture pic; // The image to be resized
    
    // SeamCarver constructor
    public SeamCarver(Picture picture)
    {
        if (picture == null) // Picture argument cannot be null
            throw new IllegalArgumentException("Picture can't be null.");
        
        pic = new Picture(picture);
    }
    
    // Returns the Picture object
    public Picture picture()
    {
        return new Picture(pic);
    }
    
    // Returns the width of the Picture object
    public int width()
    {
        return pic.width();
    }
    
    // Returns the height of the Picture object
    public int height()
    {
        return pic.height();
    }
    
    // Calculates "energy" of pixel described by dual-gradient energy function
    public double energy(int x, int y)
    {
        // Pixel coordinate arguments must be in bounds
        if (x >= width() || x < 0 || y >= height() || y < 0)
            throw new IllegalArgumentException("Coordinate outside of range.");
        
        // Coordinates of neighboring pixels in 4 cardinal directions
        // Modulo enables wrap around of pixels in the energy function
        int left = (x - 1) % width();
        int right = (x + 1) % width();
        int above = (y - 1) % height();
        int below = (y + 1) % height();
        
        // Handling wrap-around for pixels on left and top borders
        if (above < 0)
            above += height();
        if (left < 0)
            left += width();
        
        // Calculate energy gradient for pixel based on neighbors and orient.
        double xGradient = gradient(left, right, y, true);
        double yGradient = gradient(above, below, x, false);
        
        // Return sqrt of gradients summed as energy of the pixel
        return Math.sqrt(xGradient + yGradient);
    }
    
    // Helper method to calculate gradient of vertical/horiz. neighbor pixels
    // First two args are left/right or above/below, Coordinate of the other 
    // direction, and a boolean describing the direction
    private double gradient(int cOne, int cTwo, int direcCoor, boolean isHoriz)
    {
        Color coordOne; // Color at coordinate one (left or above)
        Color coordTwo; // Color at coordinate two (right or below)
        
        if (isHoriz) // Horizontal orientation
        {
            coordOne = pic.get(cOne, direcCoor); // Color of left pixel
            coordTwo = pic.get(cTwo, direcCoor); // Color of right pixel
        }
        
        else // Vertical orientation
        {
            coordOne = pic.get(direcCoor, cOne); // Color of above pixel
            coordTwo = pic.get(direcCoor, cTwo); // Color of below pixel
        }

        // Difference in RGB values between the two pixels
        double deltaR = coordTwo.getRed() - coordOne.getRed();
        double deltaG = coordTwo.getGreen() - coordOne.getGreen();
        double deltaB = coordTwo.getBlue() - coordOne.getBlue();
        
        // Return sum of squares of each RGB difference value
        return (deltaR * deltaR) + (deltaG * deltaG) + (deltaB * deltaB);
    }
    
    // Returns array where values are col.num. of pixel to be removed from row y
    // Uses topological sort to find shortest path to each pixel
    public int[] findVerticalSeam()
    {
        // Matrix of energy values for each pixel
        double[][] energyMatrix = new double[width()][height()];
        // Minimum distance to reach each pixel
        double[][] distTo = new double[width()][height()];
        // Parent pixel to reach specified pixel
        int[][] edgeTo = new int[width()][height()];
             
        
        // Initialize all distances to pos. inf. and populates energies matrix
        for (int i = 0; i < width(); i++)
            for (int j = 0; j < height(); j++)
            {
                energyMatrix[i][j] = energy(i, j);
                // If top row, set min dist. to be its own energy value
                if (j == 0)
                    distTo[i][j] = energyMatrix[i][j];
                else
                    distTo[i][j] = Double.POSITIVE_INFINITY;
            }

        // Perform topological sort to find min. distances to each pixel
        // Each non-border pixel has 3 parent pixels
        for (int i = 1; i < height(); i++) // Start from second row
            for (int j = 0; j < width(); j++)
            {
                // Pixel directly above is default minimum energy
                double minEnergy = distTo[j][i - 1]; // Lowest energy
                int minCol = j; // Column position of pixel
                
                if (j != 0) // Upper left
                    if (distTo[j - 1][i - 1] < minEnergy)
                    {
                        minEnergy = distTo[j - 1][i - 1]; // Lowest energy
                        minCol = j - 1; // Column position of pixel
                    }
                
                if (j != width() - 1) // Upper right
                    if (distTo[j + 1][i - 1] < minEnergy)
                    {
                        minEnergy = distTo[j + 1][i - 1]; // Lowest energy
                        minCol = j + 1; // Column position of pixel
                    }
                
                // Update lowest energy and position of lowest energy pixel
                if (energyMatrix[j][i] + minEnergy < distTo[j][i])
                {
                    distTo[j][i] = energyMatrix[j][i] + minEnergy;
                    edgeTo[j][i] = minCol;
                }
            }
        
        // Find pixel in last row with min distance and find col to track seam
        double minEnergyLastRow = Double.POSITIVE_INFINITY; // Min. dist. energy
        int minColLastRow = 0; // Min. dist. position
        for (int i = 0; i < width(); i++)
            if (distTo[i][height() - 1] < minEnergyLastRow)
            {
                minEnergyLastRow = distTo[i][height() - 1];
                minColLastRow = i;
            }
        
        // Follow seam of minimum energy pixels
        int[] vertSeam = new int[height()];
        for (int i = height() - 1; i >= 0; i--)
        {
            vertSeam[i] = minColLastRow;
            minColLastRow = edgeTo[minColLastRow][i];
        }
        
        return vertSeam; // Return seam of pixel positions
    }
    
    // Returns array where values are row.num. of pixel to be removed from col x    
    public int[] findHorizontalSeam()
    {
        // Transposes image, calls findVerticalSeam(), transposes again
        transpose();
        int[] horizSeam = findVerticalSeam();
        transpose();
        return horizSeam;
    }
    
    // Helper method to transpose an image
    private void transpose()
    {
        Picture transposePic = new Picture(height(), width());
        
        // Re-orient every pixel in the image so it's position at (i,j) is (j,i)
        for (int i = 0; i < height(); i++)
            for (int j = 0; j < width(); j++)
                transposePic.set(i, j, pic.get(j, i));
        
        pic = transposePic; // Transposed picture to be manipulated
    }
    
    // Remove a horizontally-running seam
    public void removeHorizontalSeam(int[] seam)
    {
        // Check validity of the argument, transpose, remove seam, transpose
        checkValidSeam(seam, true);
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }
    
    // Remove a vertically running seam
    public void removeVerticalSeam(int[] seam)
    {
        checkValidSeam(seam, false);
        // New picture will be width of original picture - 1
        Picture seamedPic = new Picture(width() - 1, height());
        
        // Create new picture
        for (int i = 0; i < seamedPic.height(); i++)
            for (int j = 0; j < seamedPic.width(); j++)
            {
                if (j < seam[i]) // Copy all pixels before the seam position
                    seamedPic.set(j, i, pic.get(j, i));
                else // Skip the seam pixels and copy those after it
                    seamedPic.set(j, i, pic.get(j + 1, i));
            }
        
        pic = seamedPic; // New picture is the picture with removed pixels
    }
    
    // Check that a seam input is valid
    private void checkValidSeam(int[] seam, boolean isHoriz)
    {
        if (seam == null) // Seam cannot be null
            throw new IllegalArgumentException("No null arguments.");
        
        // Check for errors based on orientation
        if (isHoriz) // Horizontal seam
        {
            // Seam length must equal picture's width
            if (seam.length != width())
                throw new IllegalArgumentException("Array is wrong length.");
            
            for (int i = 0; i < width(); i++)
            {
                // Seam values must be in bounds
                if (seam[i] < 0 || seam[i] >= height())
                    throw new IllegalArgumentException("Invalid seam.");
                
                // Adjacent seam values cannot differ by more than 1
                if (i == 0)
                    continue;
                if (Math.abs(seam[i] - seam[i - 1]) > 1)
                    throw new IllegalArgumentException("Invalid seam.");
            }
            // Height of picture cannot be 1 pixel
            if (height() == 1)
                throw new IllegalArgumentException("Height must be > 1px.");
        }
        
        else // Vertical seam
        {
            // Seam length must equal picture's height
            if (seam.length != height())
                throw new IllegalArgumentException("Array is wrong length.");
            
            for (int i = 0; i < height(); i++)
            {
                // Seam values must be in bounds
                if (seam[i] < 0 || seam[i] >= width())
                    throw new IllegalArgumentException("Invalid seam.");
                
                // Adjacent seam values cannot differ by more than 1
                if (i == 0)
                    continue;
                if (Math.abs(seam[i] - seam[i - 1]) > 1)
                    throw new IllegalArgumentException("Invalid seam.");
            }
            // Width of picture cannot be 1 pixel
            if (width() == 1)
                throw new IllegalArgumentException("Width must be > 1px.");
        }
    }
    
    // Testing all public methods
    public static void main(String[] args)
    {
        // Command line input of image to be used and cols + rows to be removed
        SeamCarver picCarve = new SeamCarver(new Picture(args[0]));
        int removeCols = Integer.parseInt(args[1]);
        int removeRows = Integer.parseInt(args[2]);
        
        // Show original image, width, height, and energy of top left pixel
        picCarve.picture().show();
        StdOut.println("Width: " + picCarve.width());
        StdOut.println("Height: " + picCarve.height());
        StdOut.println("Energy at (0,0): " + picCarve.energy(0, 0));
        
        // Remove rows and begin timing
        StdOut.println("Removing " + removeCols + " columns");
        StdOut.println("Removing " + removeRows + " rows");
        Stopwatch sw = new Stopwatch();
        
        // Begin to find and remove specified number of verti. and horiz. seams
        for (int i = 0; i < removeCols; i++)
        {
            int[] columnSeam = picCarve.findVerticalSeam();
            picCarve.removeVerticalSeam(columnSeam);
        }
        
        for (int j = 0; j < removeRows; j++)
        {
            int[] rowSeam = picCarve.findHorizontalSeam();
            picCarve.removeHorizontalSeam(rowSeam);
        }

        // Show seam-carved image
        StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        picCarve.picture().show();
    }
}
