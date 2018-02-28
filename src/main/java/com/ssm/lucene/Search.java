package com.ssm.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
public class Search {

    /**
     * 搜索类
     **/
    public void search(String keyword) {
        DirectoryReader reader = null;
        try {
            // 获取索引文件的文件夹
            Directory directory = FSDirectory.open(
                    FileSystems.getDefault().getPath("/Users/lilyxiao/workspaceweb/LuceneDemo/index"));
            //创建索引文件的读取器
            reader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(reader);//创建检索对象
            Analyzer analyzer = new StandardAnalyzer();//创建分词器
            QueryParser queryParser = new QueryParser("content", analyzer);//创建查询解析器
            Query query = queryParser.parse(keyword);//对keyword进行解析
            //检索 生成结果集
            TopDocs hits = indexSearcher.search(query, 1);
            QueryScorer scorer = new QueryScorer(query);//高亮评分
            //将原始的字符串拆分成独立的片段
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            //创建html高亮标签
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
                    "<font color='red'>", "</font>");
            Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);//高亮分析器
            highlighter.setTextFragmenter(fragmenter);//设置高亮片段
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                String content = document.get("content"); //获取content
                if (content != null) {
                    TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content));
                    String hContent = highlighter.getBestFragment(tokenStream, content);
                    System.out.println(hContent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public void search2(String keyword) {

        IndexReader reader = null;

        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(
                    "/Users/lilyxiao/workspaceweb/LuceneDemo/index"));

            reader = DirectoryReader.open(directory);

            IndexSearcher searcher = new IndexSearcher(reader);

            Analyzer analyzer = new StandardAnalyzer();


            /*检索*/

            //创建query 参数一：搜素域 第二参数 分词器 与添加的分词器保持一致
            QueryParser parser = new QueryParser("content", analyzer);

            //通过QueryParser对象创建query 参数为lucene的查询关键字
            Query query = parser.parse(keyword);

            //通过indexSearcher搜素索引 int条数
            TopDocs topDocs = searcher.search(query, 1);

            int count = topDocs.totalHits;

            System.out.println("查询出来的记录：" + count);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

                //获取当前scoreDoc的id
                int docId = scoreDoc.doc;
                //通过document的id来获取每个field域的值
                Document doc = searcher.doc(docId);

                System.out.println("文件名字" + doc.get("filename"));

                System.out.println("文件内容：" + doc.get("content"));

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
