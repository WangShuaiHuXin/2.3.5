package com.imapcloud.nest.utils.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public class InspectionReportStyleStrategy {
    public static class WidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

        private List<Integer> indexs;

        public WidthStyleStrategy() {

        }

        public WidthStyleStrategy(List<Integer> indexs) {
            this.indexs = indexs;
        }

        @Override
        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

           /* if (!isHead && (cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1)) {
                if(!indexs.contains(cell.getColumnIndex())){
                    writeSheetHolder.getSheet().setColumnHidden(cell.getColumnIndex(),true);
                }
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 12000);
            } else {
                if(!indexs.contains(cell.getColumnIndex())){
                    writeSheetHolder.getSheet().setColumnHidden(cell.getColumnIndex(),true);
                }
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 5000);
            }*/
            //统一都改为图片的宽度
            writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 12000);
        }
    }

    public static class RowHeightStyleStrategy extends AbstractRowHeightStyleStrategy {

        @Override
        protected void setHeadColumnHeight(Row row, int i) {

        }

        @Override
        protected void setContentColumnHeight(Row row, int i) {

            row.setHeight((short) 3000);
        }
    }
}
