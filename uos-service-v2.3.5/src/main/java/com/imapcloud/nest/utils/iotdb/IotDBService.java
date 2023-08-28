package com.imapcloud.nest.utils.iotdb;

import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.session.SessionDataSet;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IotDBService {

    @Autowired
    private Session iotSession;

    public List<Map<String, String>> listLogs(String nestUuid, long startTime, long endTime) {
        try {
            List<Map<String, String>> maps = new ArrayList<>();
            String s = nestUuid.replaceAll("-", "");
            String path = "root.zkyt." + s;
            List<String> paths = Collections.singletonList(path);
            iotSession.open();
            SessionDataSet sessionDataSet = iotSession.executeRawDataQuery(paths, startTime, endTime);
            SessionDataSet.DataIterator iterator = sessionDataSet.iterator();
            while (iterator.next()) {
                Map<String, String> map = new HashMap<>(4);
                long milliseconds = iterator.getLong("Time");
                String received = iterator.getString(path + ".received");
                String delivered = iterator.getString(path + ".delivered");
                map.put("msgTime", String.valueOf(milliseconds));
                map.put("received", received);
                map.put("delivered", delivered);
                map.put("uuid", path.split("\\.")[2]);
                maps.add(map);
            }
            return maps;
        } catch (StatementExecutionException | IoTDBConnectionException | NullPointerException e) {
            log.info("IotDB查询错误");
        } finally {
            try {
                iotSession.close();
            } catch (IoTDBConnectionException e) {
                log.info("iotSession关闭错误");
            }
        }
        return Collections.emptyList();
    }

    public List<Map<String, String>> listLogs(List<String> nestUuidList, long startTime, long endTime) {
        List<String> pathList = nestUuidList.stream()
                .map(uuid -> "root.zkyt." + uuid.replaceAll("-", ""))
                .collect(Collectors.toList());
        List<Map<String, String>> maps = new ArrayList<>();
        try {
            iotSession.open();
            SessionDataSet sessionDataSet = iotSession.executeQueryStatement("select * from root.zkyt.* where timestamp > " + startTime + " and timestamp < " + endTime + " align by device");
            List<String> columnNames = sessionDataSet.getColumnNames();
            SessionDataSet.DataIterator iterator = sessionDataSet.iterator();
            long start = System.currentTimeMillis();
            while (iterator.next()) {
                Map<String, String> map = new HashMap<>(4);
                for (String column : columnNames) {
                    if (column.equals("received")) {
                        map.put("received", iterator.getString(column));
                    }
                    if (column.equals("delivered")) {
                        map.put("delivered", iterator.getString(column));
                    }
                    if (column.equals("Time")) {
                        map.put("msgTime", iterator.getString(column));
                    }
                    if (column.equals("Device")) {
                        map.put("uuid", iterator.getString(column).split("\\.")[2]);
                    }
                }
                maps.add(map);
            }
            System.out.println("遍历总时间：" + (System.currentTimeMillis() - start));
            return maps;
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.info("IotDB查询错误");
        } finally {
            try {
                iotSession.close();
            } catch (IoTDBConnectionException e) {
                log.info("iotSession关闭错误");
            }
        }
        return Collections.emptyList();
    }

}
