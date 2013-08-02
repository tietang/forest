package fengfei.shard.performance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Count implements Runnable {

    private int time = 1;
    private static AtomicInteger atomic = new AtomicInteger();
    private static Date lastDate = new Date();
    private SimpleDateFormat format = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    public static String SAVE_FILE_PATH = "./count.csv";
    private static String saveFilePath = SAVE_FILE_PATH;
    private static int maxRowNums = Integer.MAX_VALUE;
    private static AtomicInteger writeNum = new AtomicInteger();
    private static AtomicInteger readNum = new AtomicInteger();
    protected static Random rand = new Random(System.currentTimeMillis());

    public static void setMaxRowNums(int maxRowNums) {
        Count.maxRowNums = maxRowNums;
    }

    public static int incrementWriteNum() {
        return writeNum.incrementAndGet();
    }

    public static void setStartWriteNum(int startNum) {
        writeNum.set(startNum);
    }

    public static int incrementReadNum() {
        return readNum.incrementAndGet();
    }

    public static int getRandomRow() {
        return rand.nextInt(Integer.MAX_VALUE) % maxRowNums;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(getRandomRow());
        }
    }

    public static void setSaveFilePath(String saveFilePath) {
        Count.saveFilePath = saveFilePath;
    }

    public static void start(int time) {
        new Count(time);
    }

    private Count(int time) {
        this.time = time;
        new Thread(this, "count-queue").start();
    }

    @Override
    public void run() {

        File file = new File(saveFilePath);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            try {
                lastDate = new Date();
                StringBuffer sb = new StringBuffer(format.format(lastDate));
                sb.append(",");
                int count = atomic.get();
                sb.append(count);
                sb.append(",");
                sb.append(writeNum.get());
                sb.append(",");
                sb.append(readNum.get());
                sb.append("\n");
                FileOutputStream out = new FileOutputStream(file, true);
                out.write(sb.toString().getBytes());
                System.out.print("Count Info:" + sb);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000 * 60 * time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int increment() {
        return atomic.incrementAndGet();
    }
}