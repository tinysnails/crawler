/*
 * Copyright (C) 2015 hu
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
package cn.edu.hfut.dmic.webcollector.plugin.ram;

import cn.edu.hfut.dmic.webcollector.crawldb.DBManager;
import cn.edu.hfut.dmic.webcollector.crawldb.Generator;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  关于数据库的操作
 *  实现爬虫任务的插入,
 * @author hu
 */
public class RamDBManager extends DBManager {

    Logger LOG = LoggerFactory.getLogger(DBManager.class);

    public RamDB ramDB;
    public RamGenerator generator=null;

    public RamDBManager(RamDB ramDB) {
        this.ramDB = ramDB;
        this.generator=new RamGenerator(ramDB);
    }

    @Override
    public boolean isDBExists() {
        return true;
    }

    @Override
    public void clear() throws Exception {
        ramDB.crawlDB.clear();
        ramDB.fetchDB.clear();
        ramDB.linkDB.clear();
        ramDB.redirectDB.clear();
    }

    @Override
    protected Generator createGenerator() {
        return new RamGenerator(ramDB);
    }


    @Override
    public void open() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    /**
     * 内存数据库中插入数据
     * @param datum 任务
     * @param force 如果数据库中存在key,是否强制插入
     * @throws Exception
     */
    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        String key = datum.key();
        if (!force) {
            if (ramDB.crawlDB.containsKey(key)) {
                return;
            }
        }
        ramDB.crawlDB.put(key, datum);
    }
    
    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {
        for(CrawlDatum datum:datums){
            inject(datum,force);
        }
    }

    /**
     * 讲fetchDb和linkDb里面的数据合并到crawlDB,即加入任务数据库
     * @throws Exception
     */
    @Override
    public void merge() throws Exception {
        LOG.info("start merge");

        /*合并fetch库*/
        LOG.info("merge fetch database");
        for (Entry<String, CrawlDatum> fetchEntry : ramDB.fetchDB.entrySet()) {
            ramDB.crawlDB.put(fetchEntry.getKey(), fetchEntry.getValue());
        }

        /*合并link库*/
        // 在此处,有个去重
        LOG.info("merge link database");
        for (String key : ramDB.linkDB.keySet()) {
            if (!ramDB.crawlDB.containsKey(key)) {
                ramDB.crawlDB.put(key, ramDB.linkDB.get(key));
            }
        }

        LOG.info("end merge");

        ramDB.fetchDB.clear();
        LOG.debug("remove fetch database");
        ramDB.linkDB.clear();
        LOG.debug("remove link database");

    }

    @Override
    public void initSegmentWriter() throws Exception {
    }

    @Override
    public synchronized void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {
        ramDB.fetchDB.put(fetchDatum.key(), fetchDatum);        //将已经执行过execute()的datum放入内存数据库的fetchDB,这里可能是爬取成功的，可能是爬取失败的。
    }

    @Override
    public synchronized void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum datum : parseDatums) {
            ramDB.linkDB.put(datum.key(), datum);
        }
    }

    @Override
    public void closeSegmentWriter() throws Exception {
    }
 
}
