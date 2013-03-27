package fengfei.forest.database.pool;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 
 * <pre>
 * 
 * <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
 *     <!-- 基本属性 url、user、password -->
 *     <property name="url" value="${jdbc_url}" />
 *     <property name="username" value="${jdbc_user}" />
 *     <property name="password" value="${jdbc_password}" />
 *       
 *     <!-- 配置初始化大小、最小、最大 -->
 *     <property name="initialSize" value="1" />
 *     <property name="minIdle" value="1" />
 *     <property name="maxActive" value="20" />
 *  
 *     <!-- 配置获取连接等待超时的时间 -->
 *     <property name="maxWait" value="60000" />
 *  
 *     <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
 *     <property name="timeBetweenEvictionRunsMillis" value="60000" />
 *  
 *     <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
 *     <property name="minEvictableIdleTimeMillis" value="300000" />
 *   
 *     <property name="validationQuery" value="SELECT 'x'" />
 *     <property name="testWhileIdle" value="true" />
 *     <property name="testOnBorrow" value="false" />
 *     <property name="testOnReturn" value="false" />
 *  
 *     <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
 *     <property name="poolPreparedStatements" value="true" />
 *     <property name="maxPoolPreparedStatementPerConnectionSize" value="20" />
 *  
 *     <!-- 配置监控统计拦截的filters -->
 *     <property name="filters" value="stat" />
 * </bean>
 * </pre>
 * 
 * @author tietang
 * 
 */
public class DruidPoolableDataSourceFactory implements
		PoolableDataSourceFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(DruidPoolableDataSourceFactory.class);

	@Override
	public DataSource createDataSource(String driverClass, String url,
			String user, String password, Map<String, String> params)
			throws PoolableException {

		DruidDataSource ds = null;
		try {

			ds = new DruidDataSource();
			ds.setUrl(url);
			ds.setDriverClassName(driverClass);
			ds.setUsername(user);
			ds.setPassword(password);
			BeanUtils.copyProperties(ds, params);
		} catch (Exception e) {
			logger.error("create DruidDataSource error", e);
			throw new PoolableException("create DruidDataSource  error", e);
		}

		return ds;

	}

	@Override
	public void destory(DataSource dataSource) throws PoolableException {
		if (dataSource == null) {
			return;
		}
		try {
			DruidDataSource ds = (DruidDataSource) dataSource;
			ds.close();
		} catch (Throwable e) {
			throw new PoolableException("destory DruidDataSource error", e);
		}

	}

}
