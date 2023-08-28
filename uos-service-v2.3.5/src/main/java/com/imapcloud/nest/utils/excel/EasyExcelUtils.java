package com.imapcloud.nest.utils.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.bytearray.ByteArrayImageConverter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName EasyExcelUtils.java
 * @Description EasyExcelUtils
 * @createTime 2022年03月24日 19:40:00
 */
@Slf4j
public class EasyExcelUtils {

    public static final String decodeStr = "UTF-8";

    public static final String sheetName = "导出记录";

    /**
     * 默认方式
     *
     * @param fileName
     * @param clazz
     * @param list
     * @param response
     */
    public static void writeAndResponse(String fileName, Class clazz, List list, HttpServletResponse response) {
        // 这里 需要指定写用哪个class去写
        ExcelWriter excelWriter = null;
        ExcelWriter write = null;
        OutputStream outputStream = null;
        try {
            fileName = URLEncoder.encode(fileName, decodeStr);
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            writeSheet.setSheetNo(1);
            writeSheet.setSheetName(sheetName);
            writeSheet.setAutoTrim(true);
            outputStream = response.getOutputStream();
            //设置页面不缓存
            response.reset();
            //设置返回文件名的编码格式
            response.setCharacterEncoding(decodeStr);
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType("application/vnd.ms-excel");
            write = EasyExcelFactory.write(outputStream, clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(new ExcelWidthStyleStrategy())
                    .build().write(list, writeSheet);
            write.finish();
            outputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
            if (write != null) {
                write.finish();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 允许自由指定Style
     *
     * @param fileName
     * @param clazz
     * @param list
     * @param response
     * @param handler
     */
    public static <T extends WriteHandler> void writeAndResponseForHandler(String fileName, Class clazz, List list, HttpServletResponse response, List<T> handler) {
        // 这里 需要指定写用哪个class去写
        ExcelWriter excelWriter = null;
        ExcelWriter write = null;
        OutputStream outputStream = null;
        try {
            fileName = URLEncoder.encode(fileName, decodeStr);
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            writeSheet.setSheetNo(1);
            writeSheet.setSheetName(sheetName);
            writeSheet.setAutoTrim(true);
            outputStream = response.getOutputStream();
            //设置页面不缓存
            response.reset();
            //设置返回文件名的编码格式
            response.setCharacterEncoding(decodeStr);
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType("application/vnd.ms-excel");
            ExcelWriterBuilder builder = EasyExcelFactory.write(outputStream, clazz);
            handler.stream().forEach(styleStrategy -> {
                builder.registerWriteHandler(styleStrategy);
            });
            write = builder.build().write(list, writeSheet);
            write.finish();
            outputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
            if (write != null) {
                write.finish();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T extends WriteHandler> void download(String fileName, Class clazz, List list, HttpServletResponse response, List<T> handler) {

        ExcelWriter write = null;
        OutputStream outputStream = null;
        try {
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            writeSheet.setSheetNo(1);
            writeSheet.setSheetName(sheetName);
            writeSheet.setAutoTrim(true);
            outputStream = response.getOutputStream();
            //设置页面不缓存
            response.reset();
            //设置返回文件名的编码格式
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setCharacterEncoding(decodeStr);
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            ExcelWriterBuilder builder = EasyExcelFactory.write(outputStream, clazz);
            handler.forEach(builder::registerWriteHandler);
            write = builder.build().write(list, writeSheet);
            write.finish();
            outputStream.close();
        } catch (Exception e) {
            log.error("#EasyExcelUtils.download#", e);
        } finally {
            if (write != null) {
                write.finish();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error("#EasyExcelUtils.download# close", e);
                }
            }
        }
    }

    /**
     * 问题列表动态导出功能
     */
    public static void exportResultGroup(HttpServletResponse response, List<List<Object>> dataList,
                                         List<List<String>> head, String fileName) {

        OutputStream outputStream = null;
        ExcelWriter write = null;
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        try {
            outputStream = response.getOutputStream();
            response.reset();
            //设置返回文件名的编码格式
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream);
            writerBuilder.registerWriteHandler(EasyExcelStyleUtils.getStyleStrategy())
                    .registerWriteHandler(new ProblemResultStyleStrategy.WidthStyleStrategy())
                    .registerWriteHandler(new ProblemResultStyleStrategy.RowHeightStyleStrategy())
                    .registerConverter(new ByteArrayImageConverter());

            writerBuilder.head(head)
                    .sheet("导出记录")
                    .doWrite(dataList);
            write = writerBuilder.build();
            write.finish();
            outputStream.flush();
        } catch (IOException e) {
            log.error("#EasyExcelUtils.exportResultGroup#", e);
            throw new BusinessException(e.getMessage());
        } finally {
            if (write != null) {
                write.finish();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("#EasyExcelUtils.exportResultGroup#", e);
                }
            }
        }
    }

    public static <T extends WriteHandler> void postDownLoad(String fileName, Class clazz, List list, HttpServletResponse response, List<T> handler) {

        ExcelWriter write = null;
        OutputStream outputStream = null;
        try {
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            writeSheet.setSheetNo(1);
            writeSheet.setSheetName(sheetName);
            writeSheet.setAutoTrim(true);
            outputStream = response.getOutputStream();
            //设置页面不缓存
            response.reset();
            //设置返回文件名的编码格式
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setCharacterEncoding(decodeStr);
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType("application/vnd.ms-excel");
            // response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            ExcelWriterBuilder builder = EasyExcelFactory.write(outputStream, clazz);
            handler.forEach(builder::registerWriteHandler);
            // write = builder.build().write(list, writeSheet);
            builder.sheet(fileName + ".xlsx").doWrite(list);
            write.finish();
            outputStream.close();
        } catch (Exception e) {
            log.error("#EasyExcelUtils.download#", e);
        } finally {
            if (write != null) {
                write.finish();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error("#EasyExcelUtils.download# close", e);
                }
            }
        }
    }
}
