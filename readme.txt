/******************************************************************************
 *  Name:     Justin Tran
 *  NetID:    jctran
 *  Precept:  P02
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Hours to complete assignment (optional):
 *
 ******************************************************************************/

Programming Assignment 7: Seam Carving


/******************************************************************************
 *  Describe concisely your algorithm to compute the horizontal and
 *  vertical seam. 
 *****************************************************************************/
To find the vertical seam, I first created a 2D array of energy values for each 
pixel. All unvisited pixels had energies of 0 except for the top row. I then 
performed topological sort to find the minimum distances to each pixel
 (which considered 3 pixels above it as possible choices). 
 We used this to calculate the distTo and edgeTo arrays. Finally we found the 
 minimum distTo value in the final row and followed its edgeTo value to the 
 pixel in the top row. This completed the vertical seam. 

 The process for the horizontal seam was the same but we transposed the image.


/******************************************************************************
 *  Describe what makes an image ideal for this seamCarving algorithm and what
 *   kind of image would not work well.
 *****************************************************************************/
An ideal image would have areas of similar color adjacent to each other but with
other areas of adjacent pixels with very different colors or "content".

An unideal image would be a single color or very nearly a single color.


/******************************************************************************
 *  Give a formula (using tilde notation) for the running time (in seconds)
 *  required to reduce the image by one row and a second formula for the
 *  running time to reduce the image by one column. Both should be functions
 *  of W and H. Reducing the image by one row/column involves exactly one
 *  call to the appropriate find() method and one call to the corresponding
 *  remove() method.
 * 
 *  Justify your answer experimentally. To do so, fill in the two tables
 *  below. Each table must have 4-10 data points. Do not include data points
 *  that takes less than 0.5 seconds. To dampen system effects, you may 
 *  perform many trials for a given value of W and H and average the results.
 *  
 *  For the leading coefficients and exponents, use 2 digits after the
 *  decimal point. Show your calculations.
 *****************************************************************************/

(keep W constant) W = 800
(removed 50 rows or columns respectively)
 H           Row removal time (seconds)     Column removal time (seconds)
--------------------------------------------------------------------------
100                 1.396                               0.878
200                 3.425                               1.728
400                 6.321                               3.765
800                 12.167                              6.992


(keep H constant) H = 800
(removed 50 rows or columns respectively)
 W           Row removal time (seconds)     Column removal time (seconds)
--------------------------------------------------------------------------
100                 2.021                               1.376
200                 4.146                               3.043
400                 8.074                               5.1
800                 18.279                              9.526


Running time to remove one row as a function of both W and H:  ~ 
~ (2.9E-5) * W*H^1.00


Running time to remove one column as a function of both W and H:  ~ 
~ (1.1E-5) * W*H^1.00


/******************************************************************************
 *  Known bugs / limitations.
 *****************************************************************************/
None

/******************************************************************************
 *  Describe whatever help (if any) that you received.
 *  Don't include readings, lectures, and precepts, but do
 *  include any help from people (including course staff, lab TAs,
 *  classmates, and friends) and attribute them by name.
 *****************************************************************************/
None

/******************************************************************************
 *  Describe any serious problems you encountered.                    
 *****************************************************************************/
None

/******************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on how much you learned from doing the assignment, and whether    
 *  you enjoyed doing it.                                             
 *****************************************************************************/
 I didn't manage to figure out how to use getRGB() and convert it into
 individual RGB components from the 32 bit int that was returned. Just a small 
 personal annoyance.