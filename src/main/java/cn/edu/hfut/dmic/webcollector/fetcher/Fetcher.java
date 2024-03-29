/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.fetcher;

import cn.edu.hfut.dmic.webcollector.crawldb.DBManager;
import cn.edu.hfut.dmic.webcollector.crawldb.Generator;
import cn.edu.hfut.dmic.webcollector.crawldb.GeneratorFilter;
import cn.edu.hfut.dmic.webcollector.conf.CommonConfigured;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抓取器
 *
 * @author hu
 */
public class Fetcher extends CommonConfigured{

    public static final Logger LOG = LoggerFactory.getLogger(Fetcher.class);

    public DBManager dbManager;

    public Executor executor;
    public NextFilter nextFilter = null;

    private AtomicInteger activeThreads;
    private AtomicInteger startedThreads;
    private AtomicInteger spinWaiting;
    private AtomicLong lastRequestStart;
    private QueueFeeder feeder = null;
    private FetchQueue fetchQueue = null;

    /**
     *
     */
    public static final int FETCH_SUCCESS = 1;

    /**
     *
     */
    public static final int FETCH_FAILED = 2;
    private int threads = 50;
    //private boolean isContentStored = false;

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * 内部类,封装需要要下的任务
     */
    public static class FetchItem {

        public CrawlDatum datum;

        public FetchItem(CrawlDatum datum) {
            this.datum = datum;
        }
    }

    /**
     * url队列,使用同步列表存储待爬取的任务
     *
     */
    public static class FetchQueue {

        public AtomicInteger totalSize = new AtomicInteger(0);

        public final List<FetchItem> queue = Collections.synchronizedList(new LinkedList<FetchItem>());

        public void clear() {
            queue.clear();
        }

        public int getSize() {
            return queue.size();
        }

        public synchronized void addFetchItem(FetchItem item) {
            if (item == null) {
                return;
            }
            queue.add(item);
            totalSize.incrementAndGet();
        }

        public synchronized FetchItem getFetchItem() {
            if (queue.isEmpty()) {
                return null;
            }
            return queue.remove(0);
        }

        public synchronized void dump() {
            for (int i = 0; i < queue.size(); i++) {
                FetchItem it = queue.get(i);
                LOG.info("  " + i + ". " + it.datum.url());
            }

        }

    }

    /**
     * “取DB，存FetchQueue”
     *  feeder,负责获取任务然后将其投喂到fetchQueyue的线程,负责从DBmanager中获取任务任务生成器generator,generator产生任务,插入FetchQueue中
     */
    public static class QueueFeeder extends Thread {

        public FetchQueue queue;        // url队列

        public DBManager dbManager;
        public Generator generator = null;
        public GeneratorFilter generatorFilter = null;
        public int size;

        public QueueFeeder(FetchQueue queue, DBManager dbManager, GeneratorFilter generatorFilter, int size) {
            this.queue = queue;
            this.dbManager = dbManager;
            this.generatorFilter = generatorFilter;
            this.size = size;
        }

        public void stopFeeder(){
            running = false;
            while (this.isAlive()) {
                try {
                    Thread.sleep(1000);
                    LOG.info("stopping feeder......");
                } catch (InterruptedException ex) {
                }
            }
        }

        public void closeGenerator() throws Exception {
            if(generator!=null) {
                generator.close();
                LOG.info("close generator:" + generator.getClass().getName());
            }
        }

        public volatile boolean running = true;

        @Override
        public void run(){

            try {
                generator = dbManager.createGenerator(generatorFilter); // 从任务管理器中创建Generator，generatorFilter任务状态过滤器
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            LOG.info("create generator:" + generator.getClass().getName());
            String generatorFilterClassName = (generatorFilter==null)?"null":generatorFilter.getClass().getName();
            LOG.info("use generatorFilter:" + generatorFilterClassName);

            boolean hasMore = true;
            running = true;
            while (hasMore && running) {

                int feed = size - queue.getSize();
                if (feed <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    continue;
                }
                while (feed > 0 && hasMore && running) {

                    CrawlDatum datum = generator.next();
                    hasMore = (datum != null);

                    if (hasMore) {
                        queue.addFetchItem(new FetchItem(datum));
                        feed--;
                    }

                }

            }

        }

    }


    /**
     *  “取FetchQueue，存DB”
     * 负责从待爬队列FetchQueue中获取任务item,对该任务item进行execut(datum,next),
     * 其中next是datums,其刚开始保存探测到的链接,接下来保存过滤后的链接
     * next更新到任务数据库DBmageger中
     */
    private class FetcherThread extends Thread {

        @Override
        public void run() {
            startedThreads.incrementAndGet();
            activeThreads.incrementAndGet();
            FetchItem item = null;
            try {

                while (running) {
                    try {
                        item = fetchQueue.getFetchItem();
                        if (item == null) {
                            if (feeder.isAlive() || fetchQueue.getSize() > 0) {
                                spinWaiting.incrementAndGet();      //FIXME spinWaiting 什么意思?

                                try {
                                    Thread.sleep(500);
                                } catch (Exception ex) {
                                }

                                spinWaiting.decrementAndGet();
                                continue;
                            } else {    //feeder已经死掉并且fetchQueue已经空了,说明没有任务了
                                return;
                            }
                        }

                        lastRequestStart.set(System.currentTimeMillis());

                        CrawlDatum crawlDatum = item.datum;
                        //String url = crawlDatum.getUrl();
                        //Page page = getPage(crawlDatum);

                        //crawlDatum.incrRetry(page.getRetry());
//                        crawlDatum.setFetchTime(System.currentTimeMillis());
                        CrawlDatums next = new CrawlDatums();       // 存储该CrawlDatum对应页提取的所有CrawlDatum
                        try {
                            executor.execute(crawlDatum, next);     // 此处next应该已经被存有该页面所有链接(autoparse为true时.paser所有符合正则连接;为false,不parse连接,则next=[])
                            if (nextFilter != null) {               // 此处需要自定义nextFilter,若不自定义,则为null
                                CrawlDatums filteredNext = new CrawlDatums();
                                for (int i = 0; i < next.size(); i++) {
                                    CrawlDatum filterResult = nextFilter.filter(next.get(i), crawlDatum);
                                    if (filterResult != null) {
                                        filteredNext.add(filterResult);
                                    }
                                }
                                next = filteredNext;        // 经过NextFilter过滤后的Datum列表
                            }

                            LOG.info(String.format("done: %s", crawlDatum.briefInfo()));   // 该任务寿终正寝

                            crawlDatum.setStatus(CrawlDatum.STATUS_DB_SUCCESS);
                        } catch (Exception ex) {
                            LOG.info(String.format("failed: %s", crawlDatum.briefInfo()), ex);
                            crawlDatum.setStatus(CrawlDatum.STATUS_DB_FAILED);
                        }

                        crawlDatum.incrExecuteCount(1);
                        crawlDatum.setExecuteTime(System.currentTimeMillis());
                        try {
                            dbManager.writeFetchSegment(crawlDatum);     // 将已经执行过execute()的datum放入内存数据库的fetchDB,这里可能是爬取成功的，可能是爬取失败的。
                            if (crawlDatum.getStatus() == CrawlDatum.STATUS_DB_SUCCESS && !next.isEmpty()) {
                                dbManager.writeParseSegment(next);      // 对内存数据库来说，是存入了linkDB,存入的是解析出来未爬取的过滤后的链接
                            }
                        } catch (Exception ex) {
                            LOG.info("Exception when updating db", ex);
                        }
                        long executeInterval = getConf().getExecuteInterval();  // 该线程的间隔时长
                        if (executeInterval > 0) {
                            try {
                                Thread.sleep(executeInterval);
                            } catch (Exception sleepEx) {
                            }
                        }

                    } catch (Exception ex) {
                        LOG.info("Exception", ex);
                    }
                }

            } catch (Exception ex) {
                LOG.info("Exception", ex);

            } finally {
                activeThreads.decrementAndGet();
            }

        }

    }

    /**
     * 抓取当前所有任务，会阻塞到爬取完成
     * 每一轮的FfetchAll都会先将数据库linkDB和fetchDB进行merge()到crawlDB,在爬取时，不会再对crawlDB进行写操作
     * @throws IOException 异常
     * @return total urls
     */
    public int fetchAll(GeneratorFilter generatorFilter) throws Exception {
        if (executor == null) {
            LOG.info("Please Specify An Executor!");
            return 0;
        }

        dbManager.merge();      //爬取之前，将所有链接放入crawlDB

        try {
            dbManager.initSegmentWriter();
            LOG.info("init segmentWriter:" + dbManager.getClass().getName());
            running = true;
            lastRequestStart = new AtomicLong(System.currentTimeMillis());

            activeThreads = new AtomicInteger(0);
            startedThreads = new AtomicInteger(0);
            spinWaiting = new AtomicInteger(0);
            fetchQueue = new FetchQueue();
            feeder = new QueueFeeder(fetchQueue, dbManager, generatorFilter, 1000);
            feeder.start();

            FetcherThread[] fetcherThreads = new FetcherThread[threads];
            for (int i = 0; i < threads; i++) {
                fetcherThreads[i] = new FetcherThread();
                fetcherThreads[i].start();
            }

            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                LOG.info("-activeThreads=" + activeThreads.get()
                        + ", spinWaiting=" + spinWaiting.get() + ", fetchQueue.size="
                        + fetchQueue.getSize());

                if (!feeder.isAlive() && fetchQueue.getSize() < 5) {
                    fetchQueue.dump();
                }

                if ((System.currentTimeMillis() - lastRequestStart.get()) > getConf().getThreadKiller()) {
                    LOG.info("Aborting with " + activeThreads + " hung threads.");
                    break;
                }

            } while (running && (startedThreads.get() != threads || activeThreads.get() > 0));
            running = false;
            long waitThreadEndStartTime = System.currentTimeMillis();
            if (activeThreads.get() > 0) {
                LOG.info("wait for activeThreads to end");
            }
            /*等待存活线程结束*/
            while (activeThreads.get() > 0) {
                LOG.info("-activeThreads=" + activeThreads.get());
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
                if (System.currentTimeMillis() - waitThreadEndStartTime > getConf().getWaitThreadEndTime()) {
                    LOG.info("kill threads");
                    for (int i = 0; i < fetcherThreads.length; i++) {
                        if (fetcherThreads[i].isAlive()) {
                            try {
                                fetcherThreads[i].stop();
                                LOG.info("kill thread " + i);
                            } catch (Exception ex) {
                                LOG.info("Exception", ex);
                            }
                        }
                    }
                    break;
                }
            }
            LOG.info("clear all activeThread");
            feeder.stopFeeder();
            fetchQueue.clear();
        } finally {
            if(feeder!=null) {
                feeder.closeGenerator();
            }
            dbManager.closeSegmentWriter();
            LOG.info("close segmentWriter:" + dbManager.getClass().getName());
        }
        return feeder.generator.getTotalGenerate();
    }

    volatile boolean running;

    /**
     * 停止爬取
     */
    public void stop() {
        running = false;
    }

    /**
     * 返回爬虫的线程数
     *
     * @return 爬虫的线程数
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 设置爬虫的线程数
     *
     * @param threads 爬虫的线程数
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    public DBManager getDBManager() {
        return dbManager;
    }

    public void setDBManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }


    public NextFilter getNextFilter() {
        return nextFilter;
    }

    public void setNextFilter(NextFilter nextFilter) {
        this.nextFilter = nextFilter;
    }
    
    

}
