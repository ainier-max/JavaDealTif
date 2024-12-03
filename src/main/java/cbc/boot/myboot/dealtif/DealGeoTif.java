package cbc.boot.myboot.dealtif;

import org.apache.ibatis.session.SqlSession;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.Hints;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.*;

public class DealGeoTif {
    /**
     * 种植结构遥感数据插入
     * 注：一次插入10000条数据
     * @param FilePath 文件路径
     * @param TimeString 期数
     */
    public void tifInsertMany(String tif_type,String FilePath,String TimeString,String sqlString,int minPixelValue,int maxPixelValue) throws Exception{
        Date date1=new Date();
        File file = new File(FilePath);

        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        System.out.println("fileName:"+fileName); // 输出：文件名

        GeoTiffReader reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
        GridCoverage2D coverage = reader.read(null);
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");//设置坐标系
        coverage = (GridCoverage2D) Operations.DEFAULT.resample(coverage, targetCRS);// 将 GridCoverage 进行重采样转换为另一个 CRS
        // 获取图像范围
        RenderedImage image = coverage.getRenderedImage();
        // 获取栅格数据集的几何属性
        GridGeometry2D geometry = coverage.getGridGeometry();
        double width = image.getWidth();
        double height = image.getHeight();
        //获取边界值
//        Envelope2D envelope2D = coverage.getEnvelope2D();
//        double minX = envelope2D.getBounds().getMinX();
//        double minY = envelope2D.getBounds().getMinY();
//        double maxX = envelope2D.getBounds().getMaxX();
//        double maxY = envelope2D.getBounds().getMaxY();
//        double pixelWidth = (maxX - minX) / width;
//        double pixelHeight = (maxY - minY) / height;
//        DirectPosition upperCorner = envelope2D.getUpperCorner();
//        DirectPosition lowerCorner = envelope2D.getLowerCorner();
//        System.out.println("minX:" + minX);
//        System.out.println("maxX:" + maxX);
//        System.out.println("minY:" + minY);
//        System.out.println("maxY:" + maxY);
//        System.out.println("pixelWidth:" + pixelWidth);
//        System.out.println("pixelHeight:" + pixelHeight);
//        System.out.println("坐标最小值:" + lowerCorner);
//        System.out.println("坐标最大值:" + upperCorner);
        List<Map<String, Object>> list =new ArrayList<>();
        int sum=0;
        //遍历所有像素
        Raster raster=image.getData();

        DealGeoTif dealGeoTif=new DealGeoTif();
        for (int i = 4000; i < 4100; i++) {
            for (int j = 2000; j < 2100; j++) {
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
                //System.out.println("i:"+i);
                //System.out.println("i,j:"+i+","+j);
                double pixelValue=0.0;
                try {
                    pixelValue = raster.getSampleDouble(i, j, 0);
                }catch (Exception e){
                    System.out.println("错误提示:"+e);
                }
                //System.out.println("pixelValue1:"+pixelValue);
                if (pixelValue >= minPixelValue && pixelValue <=maxPixelValue ) {
                    GridCoordinates2D gridPos = new GridCoordinates2D(i, j);
                    // 将像素点转换为坐标
                    DirectPosition worldPos = geometry.gridToWorld(gridPos);
                    double longitude = worldPos.getCoordinate()[1];
                    double latitude = worldPos.getCoordinate()[0];
                    Map<String, Object> objectMap=new HashMap<>();
                    objectMap.put("id", UUID.randomUUID().toString().replace("-",""));
                    objectMap.put("pixel_value",(int)pixelValue);
                    //objectMap.put("point","'POINT(" + longitude +" "+ latitude + ")'");
                    objectMap.put("time",TimeString);
                    objectMap.put("x",longitude);
                    objectMap.put("y",latitude);
                    objectMap.put("width",i);
                    objectMap.put("height",j);
                    list.add(objectMap);
                    //System.out.println(coordinate);
                    //System.out.println("像素点 (" + i + ", " + j + ") 的值： " + pixelValue + "    坐标为:" + worldPos.getCoordinate()[0] + "，" + worldPos.getCoordinate()[1]);
                    if(list.size()!=0 && list.size()%10000==0){
                        sum=sum+10000;
                        System.out.println("插入数据量:"+sum);
                        dealGeoTif.excuteInsertSql(list,sqlString);
                        try {
                            Thread.sleep(500); // 延时1秒
                            list.clear();
                            Thread.sleep(500); // 延时1秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if(list.size()>0){
            sum=sum+list.size();
            System.out.println("插入最后的数据量:"+sum);
            dealGeoTif.excuteInsertSql(list,sqlString);
            try {
                Thread.sleep(500); // 延时1秒
                list.clear();
                Thread.sleep(500); // 延时1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Date date2=new Date();
        String durationTime=(date2.getTime() - date1.getTime()) + "MS";
        System.out.println("插入遥感数据，总执行时间："+durationTime);
        System.out.println("图片宽:" + width);
        System.out.println("图片高:" + height);
        dealGeoTif.excuteInsertCountSql(tif_type,sum,TimeString,fileName);
    }

    /**
     * 执行插入语句方法
     * @param list
     * @param sqlString
     */
    public void excuteInsertCountSql(String tif_type, int sum,String timeString,String fileName){
        System.out.println("excuteInsertCountSql参数："+tif_type+"," + sum+"," +timeString+"," +fileName);
        String sqlString="insertCount";
        String tableName="";
        if(tif_type=="zzjg"){
            tableName="geo_tif_zzjg";
        }else if(tif_type=="drought"){
            tableName="geo_tif_drought";
        }else if(tif_type=="irrigation"){
            tableName="geo_tif_irrigation";
        }
        SqlSession sqlSession = MyBatisSqlSessionFactory.openSession();
        try {
            Map<String, Object> map=new HashMap<>();
            map.put("tif_type",tif_type);
            map.put("sum",sum);
            map.put("timeString",timeString);
            map.put("fileName",fileName);
            map.put("tableName",tableName);
            sqlSession.insert(sqlString, map);
            sqlSession.commit();
            System.out.println("统计遥感数据插入成功");

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
    }

    /**
     * 执行插入语句方法
     * @param list
     * @param sqlString
     */
    public void excuteInsertSql( List<Map<String, Object>> list,String sqlString){
        SqlSession sqlSession = MyBatisSqlSessionFactory.openSession();
        try {
            Map<String, Object> map=new HashMap<>();
            map.put("objects",list);
            sqlSession.insert(sqlString, map);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
    }
    /**
     * 执行删除语句方法
     * @param timeString
     * @param sqlString
     */
    public void excuteDeleteSql(String timeString,String sqlString){
        SqlSession sqlSession = MyBatisSqlSessionFactory.openSession();
        try {
            Map<String, Object> map=new HashMap<>();
            map.put("time",timeString);
            sqlSession.delete(sqlString, map);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
    }

}
