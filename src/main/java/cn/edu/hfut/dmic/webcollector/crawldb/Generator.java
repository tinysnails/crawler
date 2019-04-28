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
package cn.edu.hfut.dmic.webcollector.crawldb;


import cn.edu.hfut.dmic.webcollector.conf.Configuration;
import cn.edu.hfut.dmic.webcollector.conf.DefaultConfigured;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抓取任务生成器
 *
 * @author hu
 */
public abstract class Generator extends DefaultConfigured{

    public static final Logger LOG = LoggerFactory.getLogger(Generator.class);

    protected GeneratorFilter filter = null;        //只对状态进行过滤，未成功爬取的任务（包括未爬取和爬取失败）才能被输出
    protected int totalGenerate;


    public Generator() {
        this.totalGenerate = 0;
    }


    /**
     * return null if there is no CrawlDatum to generate,获取下一个任务
     * @return
     */
    public CrawlDatum next(){
        int topN = getConf().getTopN();     //每个网页解析时，保存链接的数量上限(如果为null，则链接数量无上限)
        // 单个任务最大执行次数
        int maxExecuteCount = getConf().getOrDefault(Configuration.KEY_MAX_EXECUTE_COUNT, Integer.MAX_VALUE);

        if(topN > 0 && totalGenerate >= topN){      //FIXME 这里topN是限制爬取最大网页数吗？
            return null;
        }

        CrawlDatum datum;
        while (true) {
            try {
                datum = nextWithoutFilter();
                if (datum == null) {
                    return datum;
                }
                if(filter == null || (datum = filter.filter(datum))!=null){// 如果没有过滤器，或者状态过滤器(datum未被[成功]爬取)返回datum
                    if (datum.getExecuteCount() > maxExecuteCount) {    // 如果该任务执行次数最大，就放弃该任务
                        continue;
                    }
                    totalGenerate += 1;                 // 一共产生的任务数，但不一定就是爬取到的url数
                    return datum;
                }

            } catch (Exception e) {
                LOG.info("Exception when generating", e);
                return null;
            }

        }
    }

    /**
     * 从数据库的crawldb中获取下一个Datum,迭代方式;
     * 未经过过滤的任务
     * @return
     * @throws Exception
     */
    public abstract CrawlDatum nextWithoutFilter() throws Exception;


//    public int getTopN() {
//        return topN;
//    }

//    public void setTopN(int topN) {
//        this.topN = topN;
//    }


//    public int getMaxExecuteCount() {
//        return maxExecuteCount;
//    }
//
//    public void setMaxExecuteCount(int maxExecuteCount) {
//        this.maxExecuteCount = maxExecuteCount;
//    }

    public int getTotalGenerate(){
        return totalGenerate;
    }

    public abstract void close() throws Exception;

    public GeneratorFilter getFilter() {
        return filter;
    }

    public void setFilter(GeneratorFilter filter) {
        this.filter = filter;
    }
}
