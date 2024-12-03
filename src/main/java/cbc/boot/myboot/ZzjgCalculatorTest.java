package cbc.boot.myboot;

import cbc.boot.myboot.dealtif.DealGeoTif;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ZzjgCalculatorTest {
    @Test
    public void testZzjgInsertMany() throws Exception{
        //植被结构遥感影像插入,自动统计该表占比统计
        String FilePath="E:\\workspaceForMe\\dealTif\\data\\geo_tif_zzjg\\2024年_种植结构影像.tif";
        String sqlString="geo_tif.zzjgInsertMany";
        String tif_type="zzjg";
        int minPixelValue=1;
        int maxPixelValue=7;
        Date date=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String TimeString = dateFormat.format(date);//期数为数据插入时间，格式为：20241127
        DealGeoTif dealGeoTif=new DealGeoTif();
        dealGeoTif.tifInsertMany(tif_type,FilePath,TimeString,sqlString,minPixelValue,maxPixelValue);
    }

    @Test
    public void testZzjgDeleteByTime() throws Exception{
        //植被结构遥感数据原始数据根据时间期数删除
        String sqlString="geo_tif.zzjgDeleteByTime";
        String TimeString="20241128";
        DealGeoTif dealGeoTif=new DealGeoTif();
        dealGeoTif.excuteDeleteSql(TimeString,sqlString);
        System.out.println("删除植被结构数据成功");
    }

    @Test
    public void textZzjgExcuteInsertCountSql() throws Exception{
        //植被结构遥感数据原始数据根据时间期数统计到geo_tif_count表中
        String tif_type="zzjg";
        int sum=6552236;
        String timeString="20241128";
        String fileName="2024年_种植结构影像";
        DealGeoTif dealGeoTif=new DealGeoTif();
        dealGeoTif.excuteInsertCountSql(tif_type,sum,timeString,fileName);
    }

    /**
     * 表结构

     CREATE TABLE `geo_tif_zzjg` (
     `id` varchar(100) CHARACTER SET utf8 NOT NULL,
     `pixel_value` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
     `time` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
     `x` double(30,20) DEFAULT NULL,
     `y` double(30,20) DEFAULT NULL,
     `width` int(11) DEFAULT NULL,
     `height` int(11) DEFAULT NULL,
     KEY `geo_tif_zzjg_x_IDX` (`x`,`y`) USING BTREE,
     KEY `geo_tif_zzjg_width_IDX` (`width`,`height`) USING BTREE,
     KEY `geo_tif_zzjg_pixelValue_IDX` (`pixel_value`) USING BTREE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

     *
     *
     */
}
