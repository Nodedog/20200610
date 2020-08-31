
/*
*
*                   阻塞队列
*
*
*
* */
public class Test1 {
    static class BlockingQueue{
        //普通队列实现方式两种:1.基于链表2.基于数组
        private int[] array = new int[100];
        //head和tail构成一个前闭后开区间 当两者重合的时候,可能表示队列空或者满
        private int head = 0;
        private int tail = 0;
        private int size = 0;

        //队列的基本操作:1.入队列2.出队列3.取队首元素
        //对于阻塞队列来说只之前入队列和出队列

        //入阻塞队列
        public  void put(int value) throws InterruptedException {
            synchronized (this) {
                if (size == array.length){
                    wait();
                }
                //把value放在队尾即可
                array[tail] = value;
                tail++;
                if (tail == array.length){
                    tail = 0;
                }
                size++;
                notify();
            }
        }


        //出阻塞队列
        public  int take() throws InterruptedException {
            int ret = -1;
            synchronized (this) {
                if (size == 0){
                    this.wait();
                }
                ret = array[head];
                head++;
                if (head == array.length){
                    head = 0;
                }
                size--;
                notify();
            }
            return ret;
        }

    }


    public static void main(String[] args) {
        //创建两个线程,模拟生产者和消费者
        //第一种情况,让生产者,生产的慢一点,消费者消费的快一点
        //这样预期,消费者线程会发生阻塞,只能等生产者生产出来,才能消费
        //第二种情况,让生产者,生产的快一点,消费者消费的慢一点
        //预期生产者线程刚开始的时候回快速的往队列中插入元素,插满之后就会发生阻塞等待
        //只能等消费者 消费一个之后 ,再生产一个
        BlockingQueue blockingQueue = new BlockingQueue();
        Thread producer = new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    try {
                        blockingQueue.put(i);
                        System.out.println("生产元素" + i);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        producer.start();

        Thread consumer = new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        int ret = blockingQueue.take();
                        System.out.println("消费元素" + ret);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        consumer.start();
    }
}
