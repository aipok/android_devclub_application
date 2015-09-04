package com.divapps.aipok.devclub;

import android.test.AndroidTestCase;

import com.divapps.aipok.devclub.utils.ImageUtils;

/**
 * Created by Vitali Nartov on 24/06/15.
 *
 */
public class ImageUtilsTest extends AndroidTestCase {

    public void setUp() throws Exception { super.setUp(); }
    public void tearDown() throws Exception { super.tearDown(); }

    /**
     * Test for dynamic image height calculation
     * @throws Exception
     */
    public void testGetHMSTimeString() throws Exception {
        ImageUtils.Size size = ImageUtils.calculateSizeBasedOnWidthAndAspectRatio(300, 615, 461);
        assertEquals("height calculation failed", size.getHeight(), 225);
    }

}
