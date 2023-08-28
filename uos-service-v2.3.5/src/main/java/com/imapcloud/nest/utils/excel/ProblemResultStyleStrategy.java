package com.imapcloud.nest.utils.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

/**
 * 问题结果列表 导出格式
 *
 * @author boluo
 * @date 2022-07-19
 */
public class ProblemResultStyleStrategy {

    public static class WidthStyleStrategy extends AbstractColumnWidthStyleStrategy {
        @Override
        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

            if (!isHead && (cell.getColumnIndex() >= 11 || cell.getColumnIndex() == 8)) {
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 12000);
            } else if(!isHead && cell.getColumnIndex() == 2) {
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 3000);
            } else if(!isHead && (cell.getColumnIndex() == 3 || cell.getColumnIndex() == 4)) {
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 6000);
            } else {
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 5000);
            }
        }
    }

    public static class RowHeightStyleStrategy extends AbstractRowHeightStyleStrategy {

        @Override
        protected void setHeadColumnHeight(Row row, int i) {

        }

        @Override
        protected void setContentColumnHeight(Row row, int i) {

            row.setHeight((short) 3372.99);
        }
    }
}
