package com.bcabuddies.fitsteps.StepsData;

class SensorFilter {
    private SensorFilter() {
    }

    static float sum(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray;
        }
        return retval;
    }

    static float norm(float[] array) {
        float retval = 0;
        for (float anArray : array) {
            retval += anArray * anArray;
        }
        return (float) Math.sqrt(retval);
    }

    static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
}
