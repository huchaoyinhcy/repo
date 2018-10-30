package com.itheima;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;


public class test {

    private IndexWriter indexWriter;

    @Before
    public void init() throws Exception {
        indexWriter =
                new IndexWriter(FSDirectory.open(new File("D:\\TEXT\\INDEX").toPath()),
                        new IndexWriterConfig(new IKAnalyzer()));
    }

    /**
     * 5.2创建索引
     *
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        //指定所有库存放的位置
        Directory directory = FSDirectory.open(new File("D:\\TEXT\\INDEX").toPath());
        //创建indexwriterCofig对象
        IndexWriterConfig config = new IndexWriterConfig();
        //创建indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //原始稳文档的路径
        File dir = new File("D:\\TEXT\\searchsource");
        for (File file : dir.listFiles()) {
            //文件名
            String fileName = file.getName();
            //文件内容
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            //文件路径
            String filePath = file.getPath();
            //文件大小
            long fielSize = FileUtils.sizeOf(file);
            //创建文件名域
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储
            Field fileNameField = new TextField("filename", fileName, Field.Store.YES);
            //文件内容域
            Field fileContentField = new TextField("content", fileContent, Field.Store.YES);
            //文件路径域（不分析、不索引、只存储）
            Field filePathFiled = new TextField("path", filePath, Field.Store.YES);
            //文件大小域
            Field fileSizeField = new TextField("size", fielSize + "", Field.Store.YES);
            //创建document对象
            Document document = new Document();
            document.add(fileContentField);
            document.add(fileNameField);
            document.add(fileSizeField);
            document.add(filePathFiled);
            //创建索引，并写入索引库
            indexWriter.addDocument(document);
        }
        //关闭indexwriter
        indexWriter.close();
    }

    /**
     * 查询索引库
     *
     * @throws Exception
     */
    @Test
    public void searchIndex() throws Exception {
        //指定索引库存放的路径
        //D:\temp\index
        Directory directory = FSDirectory.open(new File("D:\\TEXT\\INDEX").toPath());
        //创建indexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建indexsearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建查询
        Query query = new TermQuery(new Term("filename", "apache"));
        //执行查询
        //第一个参数是查询对象，第二个参数是查询结果返回的最大值
        TopDocs topDocs = indexSearcher.search(query, 10);
        //查询结果的总条数
        System.out.println("查询结果的总条数:" + topDocs.totalHits);
        //遍历查询结果
        //topDocs.scoreDocs存储了document对象的id
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //scoreDoc.doc属性就是document对象的id
            //根据document的id找到document对象
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("filename"));
//            System.out.println(document.get("content"));
            System.out.println(document.get("size"));
            System.out.println(document.get("path"));
        }
        indexReader.close();
    }

    /**
     * 添加索引
     *
     * @throws Exception
     */
    @Test
    public void addDocument() throws Exception {
        //索引库存放路径
        Directory directory = FSDirectory.open(new File("D:\\TEXT\\INDEX").toPath());
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        //创建一个indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //创建一个Document对象
        Document document = new Document();
        //向document对象中添加域。
        //不同的document可以有不同的域，同一个document可以有相同的域。
        document.add(new TextField("filename", "新添加的文档", Field.Store.YES));
        document.add(new TextField("content", "新添加的文档的内容", Field.Store.NO));
        //LongPoint创建索引
        document.add(new LongPoint("size", 10001));
        //StoreField存储数据
        document.add(new StoredField("SIZE", 10001));
        //不需要创建索引的就使用StoreField存储
        document.add(new StoredField("path", "D:\\TEXT\\2.txt"));
        //添加文档到索引库
        indexWriter.addDocument(document);
        //关闭indexwriter
        indexWriter.close();

    }

    /**
     * 根据查询条件查询索引库
     *
     * @throws Exception
     */
    @Test
    public void deleteIndexByQuery() throws Exception {
        indexWriter.deleteDocuments(new Term("filename", "apache"));
        indexWriter.close();
    }
}
