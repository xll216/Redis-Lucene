package com.ssm.lucene;

import com.ssm.domain.Student;
import com.ssm.domain.StudentParamter;
import com.ssm.mapper.StudentDao;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
public class LuceneUtil {
    public static final String INDEXPATH = "/Users/lilyxiao/workspaceweb/LuceneDemo/index";

    private static RAMDirectory ramDirectory;

    private static IndexWriter ramWriter;

    private static final String FIELD_ID = "id";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_ADDRESS = "address";

    @Autowired
    private StudentDao studentDao;

    static {
        try {
            FSDirectory fsDirectory = FSDirectory.open(Paths.get(INDEXPATH));
            ramDirectory = new RAMDirectory(fsDirectory, IOContext.READONCE);
            fsDirectory.close();

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                    new StandardAnalyzer());

            indexWriterConfig.setIndexDeletionPolicy(new SnapshotDeletionPolicy(
                    new KeepOnlyLastCommitDeletionPolicy()));

            ramWriter = new IndexWriter(ramDirectory, indexWriterConfig);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 于磁盘创建索引
     **/
    public void reCreatIndex() {

        try {
            Path path = Paths.get(INDEXPATH);
            //删除原有索引文件
            for (File file : path.toFile().listFiles()) {
                file.delete();
            }

            FSDirectory fsDirectory = FSDirectory.open(path);

            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

            IndexWriter writer = new IndexWriter(fsDirectory, indexWriterConfig);

            StudentParamter paramter = new StudentParamter();
            paramter.setPageSize(Integer.MAX_VALUE);

            List<Student> students = studentDao.select(
                    paramter);

            for (Student student : students) {
                writer.addDocument(toDocument(student));
            }
            writer.close();
            System.out.println("*******创建索引成功******" + students.size());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 实体student对象转成document索引对象
     **/
    private Document toDocument(Student student) {
        Document doc = new Document();
        doc.add(new TextField(FIELD_ID, String.valueOf(student.getId())
                , Field.Store.YES));

        doc.add(new TextField(FIELD_USERNAME, student.getUsername(),
                TextField.Store.YES));

        doc.add(new TextField(FIELD_PASSWORD, student.getPassword(),
                TextField.Store.YES));

        doc.add(new TextField(FIELD_ADDRESS, student.getAddress(),
                TextField.Store.YES));
        return doc;
    }

    /**
     * 搜素
     **/
    public List<Student> search(String keyword) {
        List<Student> students = new ArrayList<>();
        try {
            IndexSearcher indexSearcher = new IndexSearcher(
                    DirectoryReader.open(ramDirectory));
            String[] fields = {FIELD_ID, FIELD_USERNAME, FIELD_PASSWORD, FIELD_ADDRESS};

            Analyzer analyzer = new StandardAnalyzer();

            QueryParser queryParser = new MultiFieldQueryParser(
                    fields, analyzer);

            Query query = queryParser.parse(keyword);

            TopDocs hits = indexSearcher.search(query, Integer.MAX_VALUE);

            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Student student = new Student();
                Document doc = indexSearcher.doc(scoreDoc.doc);
                student.setId(Integer.valueOf(doc.get(FIELD_ID)));
                student.setUsername(doc.get(FIELD_USERNAME));
                student.setPassword(doc.get(FIELD_PASSWORD));
                student.setAddress(doc.get(FIELD_ADDRESS));

                students.add(student);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return students;
    }


    /**
     * 同步索引到磁盘
     **/
    public void indexSync() {

        IndexWriterConfig config = null;
        SnapshotDeletionPolicy snapshotDeletionPolicy = null;
        IndexCommit indexCommit = null;

        try {
            config = (IndexWriterConfig) ramWriter.getConfig();

            snapshotDeletionPolicy = (SnapshotDeletionPolicy) config.getIndexDeletionPolicy();

            indexCommit = snapshotDeletionPolicy.snapshot();

            config.setIndexCommit(indexCommit);

            Collection<String> fileNames = indexCommit.getFileNames();

            Path toPath = Paths.get(INDEXPATH);

            Directory toDir = FSDirectory.open(toPath);

            //删除所有原有索引文件
            for (File file : toPath.toFile().listFiles()) {
                file.delete();
            }

            for (String fileName : fileNames) {
                toDir.copyFrom(ramDirectory, fileName, fileName, IOContext.DEFAULT);
            }
            toDir.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
