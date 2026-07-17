package com.example.quanxiankongzhi.kb.service;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.grpc.SearchResults;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {
    private final MilvusServiceClient milvusClient;
    private static final String COLLECTION_NAME = "kb_chunks";
    private static final int VECTOR_DIM = 768; // BGE 模型输出维度
    /**
     * 启动时自动创建 Collection（如果不存在）
     */
    @PostConstruct
    public void init(){
        // 检查 Collection 是否存在
        boolean hasCollection = false;
        try{
            hasCollection = milvusClient.hasCollection(
                    io.milvus.param.collection.HasCollectionParam.newBuilder()
                            .withCollectionName(COLLECTION_NAME)
                            .build()
            ).getData();
        }catch (Exception e){
            log.warn("检查Collection失败: {}", e.getMessage());

        }
        if (!hasCollection){
            createCollection();
        }else{
            log.info("Milvus Collection '{}' 已存在", COLLECTION_NAME);
        }

    }
    private void createCollection(){
        // 定义字段
        FieldType idField = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
        FieldType chunkIdField = FieldType.newBuilder()
                .withName("chunk_id")
                .withDataType(DataType.Int64)
                .build();
        FieldType docIdField = FieldType.newBuilder()
                .withName("doc_id")
                .withDataType(DataType.Int64)
                .build();
        FieldType kbIdField = FieldType.newBuilder()
                .withName("kb_id")
                .withDataType(DataType.Int64)
                .build();
        FieldType textField = FieldType.newBuilder()
                .withName("text")
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();
        FieldType vectorField = FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(VECTOR_DIM)
                .build();
        CreateCollectionParam param = CreateCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withDescription("知识库文档分块向量")
                .addFieldType(idField)
                .addFieldType(chunkIdField)
                .addFieldType(docIdField)
                .addFieldType(kbIdField)
                .addFieldType(textField)
                .addFieldType(vectorField)
                .build();
        var response = milvusClient.createCollection(param);
        log.info("创建Milvus Collection: {}", response.getData());
        // 创建索引
        createIndex();
    }
    private void createIndex(){
        try{
            milvusClient.createIndex(
                    io.milvus.param.index.CreateIndexParam.newBuilder()
                            .withCollectionName(COLLECTION_NAME)
                            .withFieldName("vector")
                            .withIndexType(io.milvus.param.IndexType.IVF_FLAT)
                            .withMetricType(io.milvus.param.MetricType.IP)
                            .withExtraParam("{\"nlist\":128}")
                            .build()
            );
            log.info("Milvus 索引创建成功");
        }catch (Exception e){
            log.warn("索引创建失败: {}", e.getMessage());
        }
    }
    /**
     * 插入向量
     */
    public void insert(Long chunkId, Long docId, Long kbId, String text, List<Float> vector){
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("chunk_id", Arrays.asList(chunkId)));
        fields.add(new InsertParam.Field("doc_id", Arrays.asList(docId)));
        fields.add(new InsertParam.Field("kb_id", Arrays.asList(kbId)));
        fields.add(new InsertParam.Field("text", Arrays.asList(text)));
        fields.add(new InsertParam.Field("vector", Arrays.asList(vector)));
        InsertParam param = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build();
        milvusClient.insert(param);
    }
    /**
     * 向量检索
     */
    public List<Long> search(List<Float> queryVector, Long kbId, int topK){
        String filter = "kb_id == " + kbId;
        SearchParam param = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName("vector")
                .withVectors(Arrays.asList(queryVector))
                .withExpr(filter)
                .withTopK(topK)
                .withMetricType(io.milvus.param.MetricType.IP)
                .withParams("{\"nprobe\":16}")
                .withOutFields(Arrays.asList("chunk_id", "text"))
                .build();
        R<SearchResults> result = milvusClient.search(param);
        // 返回匹配的 chunk_id 列表
        List<Long> chunkIds = new ArrayList<>();
        if (result.getData() != null){
            result.getData().getResults().getFieldsDataList().forEach(fd -> {
                // 提取 chunk_id
            });
        }
        return chunkIds;
    }
}
