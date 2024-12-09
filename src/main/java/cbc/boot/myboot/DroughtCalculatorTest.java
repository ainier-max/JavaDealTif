package cbc.boot.myboot;

import cbc.boot.myboot.dealtif.DealGeoTif;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DroughtCalculatorTest {
    @Test
    public void testDroughtInsertMany() throws Exception{
        //植被结构遥感影像插入，自动统计该表占比统计
        String FilePath="E:\\workspaceForMe\\dealTif\\data\\geo_tif_drought\\2024年06月10日_干旱监测数据.tif";
        String tif_type="drought";
        //用于插入指定期数的数据，正常来说是默认数据插入的当天时间
        Date date=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String TimeString = dateFormat.format(date);//期数为数据插入时间，格式为：20241127
        DealGeoTif dealGeoTif=new DealGeoTif();
        dealGeoTif.tifInsertMany(tif_type,FilePath,TimeString);
    }

    @Test
    public void testDroughtDeleteByTime() throws Exception{
        //植被结构遥感数据原始数据根据时间期数删除
        String sqlString="geo_tif.droughtDeleteByTime";
        String timeString="20241202";
        DealGeoTif dealGeoTif=new DealGeoTif();
        dealGeoTif.excuteDeleteSql(timeString,sqlString);
        System.out.println("删除干旱遥感数据成功");
    }

    @Test
    public void textDroughtExcuteInsertCountSql() throws Exception{
        //执行testDroughtInsertMany会自动进行统计，植被结构遥感数据原始数据根据时间期数统计到geo_tif_count表中
        String tif_type="drought";
        int sum=6552236;//某期的表数据量
        String timeString="20241202";
        String fileName="2024年06月10日_干旱监测数据";
        DealGeoTif dealGeoTif=new DealGeoTif();
        dealGeoTif.excuteInsertCountSql(tif_type,sum,timeString,fileName);
    }
    /**
     * 表结构
     CREATE TABLE `geo_tif_drought` (
     `id` varchar(100) CHARACTER SET utf8 NOT NULL,
     `pixel_value` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
     `time` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
     `x` double(30,20) DEFAULT NULL,
     `y` double(30,20) DEFAULT NULL,
     `width` int(11) DEFAULT NULL,
     `height` int(11) DEFAULT NULL,
     KEY `geo_tif_drought_x_IDX` (`x`,`y`) USING BTREE,
     KEY `geo_tif_drought_width_IDX` (`width`,`height`) USING BTREE,
     KEY `geo_tif_drought_pixelValue_IDX` (`pixel_value`) USING BTREE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
     *
     *
     */
}
