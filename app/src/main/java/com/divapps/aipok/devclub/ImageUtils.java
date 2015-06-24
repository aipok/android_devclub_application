package com.divapps.aipok.devclub;


/**
 * Created by Vitali Nartov on 24/06/15.
 * Image utility for resizing and image formatting
 */
public class ImageUtils {

    public static Size calculateSizeBasedOnWidthAndAspectRatio(int width, int imageWidth, int imageHeight){
        int actualHeight = (int) Math.ceil( ((double)width) * imageHeight / imageWidth);
        return new Size(width, actualHeight);
    }

    public static class Size{
        private int width;
        private int height;

        Size(int width, int height){
            this.width = width;
            this.height = height;
        }

        public int getWidth(){
            return width;
        }

        public int getHeight(){
            return height;
        }
    }
}
