package com.ssm.test;

import com.ssm.domain.Student;
import com.ssm.domain.StudentParamter;
import com.ssm.lucene.Index;
import com.ssm.lucene.LuceneUtil;
import com.ssm.lucene.Search;
import com.ssm.mapper.StudentDao;
import com.ssm.service.StudentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-*.xml"})
public class MainTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentDao studentDao;

    @Resource
    private LuceneUtil luceneUtil;


    @Test
    public void testService() {
//        Student student = studentService.selectByID(1);
//        System.out.println("***************");
//        System.out.println(student);
//        System.out.println("***************");
//
//        student = studentService.selectByID(1);
//        System.out.println("***************");
//        System.out.println(student);
//        System.out.println("***************");

//        student = studentService.selectByName("kim");
//        System.out.println("***************");
//        System.out.println(student);
//        System.out.println("***************");

        StudentParamter paramter = new StudentParamter();
        paramter.setPageIndex(1);
        paramter.setPageSize(5);

        List<Student> students = studentDao.select(
                paramter);

        System.out.println(students);
    }

    @Test
    public void testIndex() {
        Index index = new Index();
        index.index();
    }

    @Test
    public void testSearch() {
        Search search = new Search();
        search.search2("矮拉山隧道");
    }

    @Test
    public void testLucene() {
        luceneUtil.reCreatIndex();

        List<Student> students = luceneUtil.search("ee");

        System.out.println(students);
    }


}
