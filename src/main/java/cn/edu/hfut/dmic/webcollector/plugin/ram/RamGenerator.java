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

import cn.edu.hfut.dmic.webcollector.crawldb.Generator;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.util.Config;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *  基于内存的任务生成器
 *
 * @author hu
 */
public class RamGenerator extends Generator {

    RamDB ramDB;

    public RamGenerator(RamDB ramDB) {
        this.ramDB = ramDB;
        iterator = ramDB.crawlDB.entrySet().iterator();     //对crawlDB的迭代器。里面会包含已经爬取的任务，所以使用generatorfilter进行过滤
    }

    Iterator<Entry<String, CrawlDatum>> iterator;       //迭代器是全局的，所以直接调用方法就行

    /**
     * 从内存数据库RamDB的crawlDB中取任务,不过滤
     * 对crawlDB的迭代器。里面会包含已经爬取的任务，所以后续使用generatorfilter进行过滤
     * 过滤发生在Generator.next().这里的未过滤指的是未针对datum状态进行过滤
     * @return
     * @throws Exception
     */
    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        if(iterator.hasNext()){
            CrawlDatum datum = iterator.next().getValue();
            return datum;
        }else{
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        // 内存数据库不需要关闭generato
    }


}
