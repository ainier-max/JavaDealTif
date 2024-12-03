package cbc.boot.myboot.dealtif;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Druid的数据源工厂类
 */
public class DruidDataSourceFactory extends UnpooledDataSourceFactory {
    /**
     * 一个默认的构造函数：用意是通过代码手动的去实例化数据源对象；
     */
    public DruidDataSourceFactory(){
        this.dataSource = new DruidDataSource();
    }

    @Override
    public DataSource getDataSource() {
        try {
            ((DruidDataSource) this.dataSource).init();
        } catch (SQLException e) {
            throw new RuntimeException(e); // 如果产生错误，直接把其包装成一个运行异常抛出去；
        }
        return this.dataSource;
    }
}