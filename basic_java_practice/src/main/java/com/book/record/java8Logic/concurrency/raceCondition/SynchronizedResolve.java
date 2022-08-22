package com.book.record.java8Logic.concurrency.raceCondition;

/**
 * @author Alexis
 * @data 2022-08-10
 * @description :
 */
public class SynchronizedResolve implements Runnable {

    private static int counter;

    public synchronized void incCount() {
        counter++;
    }

    @Override
    public   void run() {
        for (int i = 0; i < 1000; i++) {
            incCount();
        }
    }



    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[1000];

        for (int i = 0; i < 1000; i++) {
            threads[i]= new Thread(new SynchronizedResolve());
            threads[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            threads[i].join();
        }

        System.out.println(counter);
    }
}
