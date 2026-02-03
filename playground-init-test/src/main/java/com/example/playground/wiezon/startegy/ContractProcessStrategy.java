package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;
import static com.example.playground.wiezon.util.CommonUtil.valueMap;

@Component
public class ContractProcessStrategy implements MetaDataProcessStrategy{

    private final TableClassifier classifier;
    private final DataSource dataSource;
    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;

    public ContractProcessStrategy(TableClassifier classifier, DataSource dataSource, FileReadService fileReadService, DBProcessService dbProcessService) {
        this.classifier = classifier;
        this.dataSource = dataSource;
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isContractRelated(metaData);
    }

    @Override
    public void process(MetaData metaData, InitData propertiesData) {

        String cono = getCoNo(dataSource);

        if(cono == null){
            throw new RuntimeException("등록될 영세 사업자번호가 존재하지 않습니다. 확인 부탁드립니다.");
        }


        List<Map<String, Map<String, Object>>> newRows = metaData.getRows().stream().map(
                templateRow -> {
                    Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);

                    if (isTemplateMatched(deepRow, "CO_NO", "${CO_NO}"))
                        deepRow.put("CO_NO", valueMap(cono));

                    return deepRow;
                }).toList();

        MetaData newMetaData = new MetaData(metaData.getTable(), newRows);

        fileReadService.dataPreProcess(newMetaData);
        dbProcessService.save(newMetaData);


    }


    private static String getCoNo(DataSource dataSource) {
        String sql =
                "SELECT tmma.CO_NO " +
                        "FROM TBSI_MS_MBS_ALL tmma " +
                        "LEFT JOIN TBSI_CO tsc ON tmma.CO_NO = tsc.CO_NO " +
                        "WHERE tmma.SM_MBS_CD = ? " +
                        "AND tsc.CO_NO IS NULL " +
                        "ORDER BY tmma.TO_DT DESC " +
                        "LIMIT 1";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, "A1");

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return rs.getString("CO_NO");
                }else{
                    throw new RuntimeException("영세를 가진 사업자번호가 존재하지 않습니다.");
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
