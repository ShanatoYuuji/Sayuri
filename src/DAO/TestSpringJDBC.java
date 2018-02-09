package DAO;

import java.io.File;
import java.io.InputStream;

import javax.sql.DataSource;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sayuri.mapper.BlogMapper;

public class TestSpringJDBC {
	  private JdbcTemplate jt;  
	  
	  
	    public void setDataSource(DataSource dataSource) {  
	        this.jt = new JdbcTemplate(dataSource);  
	    }  
	  
	  
	    public int get() {  
	        int rowCount = this.jt.queryForObject("select count(*) from category", Integer.class);  
	        return rowCount;  
	    }  
	  
	  
	    public static void main(String[] args) { 
	    	System.out.println(TestSpringJDBC.class.getClassLoader().getResource("").getPath());
	    	String filename=TestSpringJDBC.class.getClassLoader().getResource("").getPath();
	    	File file=new File(filename);
//	    	System.out.println(file.getParent());
	    	String filepath=file.getParent()+"/applicationContext.xml";
	        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml"); 
//	    	ApplicationContext context=new FileSystemXmlApplicationContext(filepath); 
	    	
	        TestSpringJDBC t = ((TestSpringJDBC) context.getBean("testBean"));  
	        System.out.println("the number in employee"+t.get()); 
	        
	        
	    }  
}
