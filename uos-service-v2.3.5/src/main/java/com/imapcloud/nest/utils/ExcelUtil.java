package com.imapcloud.nest.utils;


import com.imapcloud.nest.v2.manager.rest.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;


/**
 * Excel相关处理
 *
 * @author chenwt
 */
@Component
@Slf4j
public class ExcelUtil<T> {

    @Resource
    private FileManager fileManager;

    private static FileManager staticFileManager;

    @PostConstruct
    public void init() {
        staticFileManager = fileManager;
    }


    /**
     * 导出excel
     *
     * @param response
     * @param header
     * @param dataList
     * @throws Exception
     */
    public static void exportExcel(HttpServletResponse response, List<String> header, List<List<String>> dataList, String name, String[] keys) throws Exception {
        exportExcel(response, name, name, header, dataList, keys);
    }

    /**
     * 导出excel
     *
     * @param response
     * @param title
     * @param subheading
     * @param header
     * @param dataList
     * @throws Exception
     */
    public static void exportExcel(HttpServletResponse response, String title, String subheading, List<String> header, List<List<String>> dataList, String[] keys) throws Exception {
        //获取一个HSSFWorkbook对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle style = getHSSFCellStyle(workbook);
        //创建一个sheet
        HSSFSheet sheet = workbook.createSheet("Sheet1");

        //表头
        HSSFRow row = sheet.createRow(0);

        //数据
        for (int i = 0; i < dataList.size(); i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < dataList.get(i).size(); j++) {
                if (dataList.get(i).get(j) != null && dataList.get(i).get(j).length() > 0 && dataList.get(i).get(j).trim().length() > 0) {
                    if (i > 0 && keys != null && keys.length > 0
                            && StringUtils.isNotBlank(dataList.get(i).get(j))
                            && ("photo".equals(keys[j]) || "meterPhoto".equals(keys[j]) || "pie".equals(keys[j]))) {
                        try (InputStream inputSteam = staticFileManager.getInputSteam(dataList.get(i).get(j))){
                            if (inputSteam != null) {
                                BufferedImage bufferedImage = ImageIO.read(inputSteam);
                                ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "jpg", byteArrayOut);
                                row.setHeight((short) (900));
                                byte[] data = byteArrayOut.toByteArray();
                                HSSFPatriarch drawingPatriarch = sheet.createDrawingPatriarch();
                                HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) (j), i, (short) (j), i);
                                drawingPatriarch.createPicture(anchor, workbook.addPicture(data, HSSFWorkbook.PICTURE_TYPE_JPEG));
                            } else {
                                row.createCell(j).setCellType(CellType.STRING);
                                row.createCell(j).setCellValue("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        HSSFCell cell = row.createCell(j);
                        HSSFCellStyle cellStyle = workbook.createCellStyle();
                        HSSFFont font = workbook.createFont();
                        if (i == 0) {
                            // 加粗
                            font.setBold(true);
                            font.setFontHeightInPoints((short) 13);
                            cellStyle.setFont(font);
                            sheet.setColumnWidth(j, 5000);
                        }
                        cellStyle.setWrapText(true);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(dataList.get(i).get(j));
                    }
                }
            }
        }

        OutputStream outputStream = response.getOutputStream();
        //设置页面不缓存
        response.reset();
        String filename = title + ".xls";
        //设置返回文件名的编码格式
        response.setCharacterEncoding("utf-8");
        filename = URLEncoder.encode(filename, "utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + filename);
        response.setContentType("application/vnd.ms-excel");
        workbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 导出excel
     *
     * @param response
     * @param title
     * @param subheading
     * @param header
     * @param dataList
     * @throws Exception
     */
    public static void exportExcel2(HttpServletResponse response, String title, String subheading, List<String> header, List<List<String>> dataList, String[] keys) {
        //获取一个HSSFWorkbook对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        int size = dataList.size();
        int sheetRecords = 50000;
        int sheetCount = (size / sheetRecords) + ((size % sheetRecords > 0) ? 1 : 0);
        List<List<List<String>>> dataSubList = new ArrayList<>(sheetCount);
        for (int x = 0; x < sheetCount; x++) {
            int fromIndex = x * sheetRecords;
            int toIndex = Math.min(((x + 1) * sheetRecords), size);
            dataSubList.add(dataList.subList(fromIndex, toIndex));
        }
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        for (int x = 0; x < dataSubList.size(); x++) {
            //创建一个sheet
            XSSFSheet sheet = workbook.createSheet("Sheet" + (x + 1));
            List<List<String>> lists = dataSubList.get(x);
            //添加表头
            XSSFRow row0 = sheet.createRow(0);
            for (int k = 0; k < header.size(); k++) {
                XSSFCell cell = row0.createCell(k);
                sheet.setColumnWidth(k, 10000);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(header.get(k));
            }
            //数据
            for (int i = 0; i < lists.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                for (int j = 0; j < lists.get(i).size(); j++) {
                    sheet.setColumnWidth(j, 10000);
                    XSSFCell cell = row.createCell(j);
                    if (lists.get(i).get(j) != null) {
                        if (lists.get(i).get(j).length() > 32767) {
                            cell.setCellValue("字符串长度过长，可进行单独查询");
                        } else {
                            cell.setCellValue(lists.get(i).get(j));
                        }
                    }
                }
            }
        }


        try {
            OutputStream outputStream = response.getOutputStream();
            //设置页面不缓存
            response.reset();
            String filename = title + ".xlsx";
            //设置返回文件名的编码格式
            response.setCharacterEncoding("utf-8");
            filename = URLEncoder.encode(filename, "utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + filename);
            response.setContentType("application/vnd.ms-excel");
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据（单页）
     *
     * @param file        文件
     * @param sheetIndex  页名的索引(从0开始，-1代表全部页)
     * @param headerIndex 表头的索引（用于获取共多少列以及第几行开始读数据）
     * @return
     * @throws Exception
     */
    public static List<List<Object>> importExcel(MultipartFile file, int sheetIndex, int headerIndex) throws Exception {
        Workbook workbook = null;
        //返回的data
        List<List<Object>> data = new ArrayList<>();
        workbook = getWorkbook(file);
        //导入某一页
        if (sheetIndex != -1 && sheetIndex > -1) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            List<List<Object>> lists = importOneSheet(sheet, headerIndex);
            data.addAll(lists);
        } else {
            //导入全部
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                List<List<Object>> lists = importOneSheet(sheet, headerIndex);
                data.addAll(lists);
            }
        }
        return data;
    }


    /**
     * 导入数据（所有页）
     *
     * @param file        文件
     * @param headerIndex 表头的索引（用于获取共多少列以及第几行开始读数据）
     * @return
     * @throws Exception
     */
    public static List<List<Object>> importExcel(MultipartFile file, int headerIndex) throws Exception {
        return importExcel(file, 0, headerIndex);
    }

    /**
     * 创建一个style
     *
     * @param workbook
     * @return
     */
    private static HSSFCellStyle getHSSFCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        //居中
        cellStyle.setAlignment(CENTER);
        return cellStyle;
    }


    /**
     * 获取一个sheet里的数据
     *
     * @param sheet
     * @param headerIndex
     * @return
     * @throws Exception
     */
    private static List<List<Object>> importOneSheet(Sheet sheet, int headerIndex) throws Exception {
        List<List<Object>> data = new ArrayList<>();
        int row = sheet.getLastRowNum();
        //row = -1 表格中没有数据
        //row = headerIndex 表格中表头以下没有数据（指没有有用数据）
        if (row == -1 || row == headerIndex) {
            throw new Exception("表格中没有有用数据!");
        }
        //通过表头获取共多少列
        int coloumNum = sheet.getRow(headerIndex).getPhysicalNumberOfCells();
        //从表头开始取数据
        for (int i = headerIndex; i <= row; i++) {
            Row row1 = sheet.getRow(i);
            List<Object> list = new ArrayList<>();
            if (row1 != null) {
                for (int j = 0; j < coloumNum; j++) {
                    list.add(row1.getCell(j) != null ? getCellValue(row1.getCell(j)) : "");
                }
            }
            data.add(list);
        }
        return data;
    }


    /**
     * 获取workbook
     *
     * @return
     */
    private static Workbook getWorkbook(MultipartFile file) throws Exception {
        Workbook workbook = null;
        //获取文件名
        String fileName = file.getOriginalFilename();
        //判断文件格式
        if (fileName.endsWith("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else if (fileName.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            throw new Exception("文件格式有误!");
        }
        return workbook;
    }


    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        String cellValue = "";
        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getRichStringCellValue().getString().trim();
                break;
            case NUMERIC:
                DecimalFormat df = new DecimalFormat("0.000000000");
                cellValue = df.format(cell.getNumericCellValue());
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = "";
        }
        return cellValue.trim();
    }

    /**
     * 导入
     *
     * @param file csv文件(路径+文件)
     * @return
     */
    public static List<String> importCsv(MultipartFile file) {
        List<String> dataList = new ArrayList<String>();

        BufferedReader br = null;
        try {
            InputStream inputStream = file.getInputStream();
            InputStreamReader is = new InputStreamReader(inputStream, "utf-8");
            br = new BufferedReader(is);
            String line = "";
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataList;
    }

    /**
     * 写CSV并转换为字节流
     *
     * @param tableHeaderArr 表头
     * @param cellList       数据
     * @return
     */
    public static byte[] writeDataAfterToBytes(String[] headerArr, String[] tableHeaderArr, List<String> cellList) {
        byte[] bytes = new byte[0];
        ByteArrayOutputStream byteArrayOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            //excel文件需要通过文件头的bom来识别编码，而CSV文件格式不自带bom,所以写文件时，需要先写入bom头，否则excel打开乱码
            //写表头
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < headerArr.length; i++) {
                sb.append(headerArr[i] + StringUtils.CR + StringUtils.LF);
            }
            String tableHeader = String.join(",", tableHeaderArr);
            sb.append(tableHeader + StringUtils.CR + StringUtils.LF);
            for (String rowCell : cellList) {
                sb.append(rowCell + StringUtils.CR + StringUtils.LF);
            }
            bufferedWriter.write(sb.toString());
            bufferedWriter.flush();
            //把输出流转换字节流
            bytes = byteArrayOutputStream.toString("UTF-8").getBytes();
            return bytes;
        } catch (IOException e) {
            log.error("writeDataAfterToBytes IOException:{}", e.getMessage(), e);
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                log.error("iostream close IOException:{}", e.getMessage(), e);
            }
        }
        return bytes;
    }


}
