package strcollection;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

public class DaoTest {
    private Dao dao;
    private SqlSession sqlSession;
    
    @Before
    public void setUp() throws Exception {
        Reader reader = Resources.getResourceAsReader("strcollection/mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();

        sqlSession = sqlSessionFactory.openSession();
        Connection conn = sqlSession.getConnection();
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setLogWriter(null);
        runner.runScript(new StringReader("drop table if exists testTable;"));
        runner.runScript(new StringReader("create table testTable (id int not null, str varchar(255) not null);"));
        runner.runScript(new StringReader("insert into testTable (id, str) values (1, 'A'),(1,'B');"));
        reader.close();
        dao = sqlSession.getMapper(Dao.class);
    }

    @After
    public void tearDown() throws Exception {
        sqlSession.close();
    }

    @Test
    public void testOK() throws Exception {
        final Map<String, Object> actual = dao.selectStringsOk();

        Assert.assertArrayEquals(new Object[]{"A", "B"}, ((Collection) actual.get("strings")).toArray());
    }

    @Test
    public void unexpectedlyReturnsIdColumnAsStrings() throws Exception {
        final Map<String, Object> actual = dao.selectWrongColumnMapping();

        Assert.assertArrayEquals(new Object[]{"A", "B"}, ((Collection) actual.get("strings")).toArray());
    }

    @Test
    public void unexpectedlyReturnsOnlySingleString() throws Exception {
        final Map<String, Object> actual = dao.selectNoJavaType();

        Assert.assertArrayEquals(new Object[]{"A", "B"}, ((Collection) actual.get("strings")).toArray());
    }
}