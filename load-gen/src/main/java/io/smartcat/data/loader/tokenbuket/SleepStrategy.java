package io.smartcat.data.loader.tokenbuket;

/**
 * Encapsulation of a strategy for relinquishing control of the CPU.
 */
interface SleepStrategy {
    /**
     * Sleep for a short period of time to allow other threads and system processes to execute.
     */
    void sleep();
}