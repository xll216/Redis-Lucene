package com.ssm.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
public class Index {

    private IndexWriter indexWriter;

    /**
     * 使用Lucene做文件索引
     * 索引的写入
     **/
    public void index() {
        IndexWriter indexWriter = null;
        try {
            //创建索引的目录对象
            FSDirectory directory = FSDirectory.open(FileSystems.getDefault()
                    .getPath("/Users/lilyxiao/workspaceweb/LuceneDemo/index"));

            //创建分词器
            Analyzer analyzer = new StandardAnalyzer();

            //创建写入对象的配置
            IndexWriterConfig indexWriterConfig =
                    new IndexWriterConfig(analyzer);

            //创建写入索引的对象 需要传入索引的保存路径和分词器配置
            indexWriter = new IndexWriter(directory, indexWriterConfig);

            //写入之前清除之前的所有索引
            indexWriter.deleteAll();

            //读取要进行索引的文件
            File dataFile = new File("/Users/lilyxiao/workspaceweb/LuceneDemo/data");

            File[] files = dataFile.listFiles();

            for (File file : files) {

                //将每个文件写入到索引中
                Document doc = new Document();

                //Field构造函数的三个参数
                //name：自定义的key值，方便搜索时确认范围
                //value：实际需要写入索引的内容
                //type：是否需要持久化
                doc.add(new Field("content",
                        FileUtils.readFileToString(file, "utf-8"),
                        TextField.TYPE_STORED));
                doc.add(new Field("filename",
                        file.getName(),
                        TextField.TYPE_STORED));


                //写入
                indexWriter.addDocument(doc);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
