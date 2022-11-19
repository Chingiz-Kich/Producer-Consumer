package kz.learn;

import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        ProducerConsumer producerConsumer = new ProducerConsumer();

        Thread thread1 = new Thread(() -> {
            try {
                producerConsumer.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                producerConsumer.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}

class ProducerConsumer {
    /**
     * Мы пишем свой собственный ArrayBlockingQueue
     * Поскольку Queue не потокобезопасный, мы будем выехжать за счет wait() & notify()
     */
    private final Queue<Integer> queue = new LinkedList<>();
    private final Object lock = new Object();
    private static final Integer LIMIT = 10;
    private static final Integer ZERO = 0;

    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            /**
             * Если не поставить synchronized блок
             * IllegalMonitorStateException: current thread is not owner
             */
            synchronized (lock) {
                /**
                 * Почему мы используем while(), а не if()
                 * потому что хотим себя обезопасить
                 * Во время while() - как только producer/consumer вызвал метод notify()
                 * нам надо сделать дополнительную проверку - эта доп проверка идет через блок while()
                 */
                while (queue.size() == LIMIT) {
                    lock.wait();
                }
                queue.add(value++);
                lock.notify();
            }
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (queue.size() == ZERO) {
                    lock.wait();
                }

                int value = queue.poll();
                System.out.println("Polled element: " + value);
                System.out.println("Queue size: " + queue.size());
                lock.notify();
            }
            Thread.sleep(2000);
        }
    }
}
