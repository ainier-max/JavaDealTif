package cbc.boot.myboot;

import cbc.boot.myboot.dealtif.DealGeoTif;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertZzjgDroughtCountCalculatorTest {
    @Test
    public void testDroughtInsertMany() throws Exception{
        //插入植被干旱统计数据成功,执行时间约50S
        Date date1=new Date();
        String sqlString="geo_tif.insertZzjgDroughtCount";
        String zzjg_time="20241203";
        String drought_time="20241203";
        Date date=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String periods = dateFormat.format(date);//期数为数据插入时间，格式为：20241127
        DealGeoTif dealGeoTif=new DealGeoTif();
        dealGeoTif.excuteInsertZzjgDroughtCount(zzjg_time,drought_time,periods,sqlString);
        Date date2=new Date();
        String durationTime=(date2.getTime() - date1.getTime()) + "MS";
        System.out.println("插入植被干旱统计数据成功，总执行时间："+durationTime);
    }
}
