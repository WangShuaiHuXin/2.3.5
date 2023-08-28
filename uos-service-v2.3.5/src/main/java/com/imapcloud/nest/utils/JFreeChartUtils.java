package com.imapcloud.nest.utils;

import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class JFreeChartUtils {

//    @Value("${file.filePath}")
//    private String filePath;


    public static class PieKey{
        public Color getPieColor() {
            return pieColor;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "PieKey{" +
                    "pieColor=" + pieColor +
                    ", value='" + value + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PieKey pieKey = (PieKey) o;
            return Objects.equals(pieColor, pieKey.pieColor) && Objects.equals(value, pieKey.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pieColor, value);
        }

        public void setPieColor(Color pieColor) {
            this.pieColor = pieColor;
        }

        private Color pieColor;

        private String value = "";

        public PieKey(Color pieColor, String value){
            this.pieColor = pieColor;
            this.value = value;
        }
    }

//    private void isChartPathExist(String chartPath) {
//        File file = new File(chartPath);
//        if (!file.exists()) {
//            file.mkdirs();
//            // log.info("filePath="+filePath+"create.");
//        }
//    }

//    // 饼状图 数据集
//    public PieDataset getDataPieSetByUtil(List<Double> data,
//                                          List<String> datadescription) {
//
//        if (data != null && datadescription != null) {
//            if (data.size() == datadescription.size()) {
//                DefaultPieDataset dataset = new DefaultPieDataset<>();
//                for (int i = 0; i < data.size(); i++) {
//                    dataset.setValue(datadescription.get(i), data.get(i));
//                }
//                return dataset;
//            }
//
//        }
//
//        return null;
//    }

    /**
     * 生成饼状图
     */
    public void makePieChart(List<Double> data, List<String> keys) {
//        Double[] data = { 9.0, 91.0 };
//        String[] keys = { "失败率", "成功率" };

        data = new LinkedList<>();
        data.add(9.0);
        data.add(91.0);

        keys = new LinkedList<>();
        keys.add("失败率");
        keys.add("成功率");

        List<PieKey> pieKeyList = new LinkedList<>();

        //createValidityComparePimChar(getDataPieSetByUtil(data, keys), "饼状图",
                //UUID.randomUUID().toString().replaceAll("-", "") + ".jpg", pieKeyList);
    }

    public static void main(String[] args) {
        JFreeChartUtils utils = new JFreeChartUtils();
        utils.makePieChart(null, null);
    }

    /**
     * 获取图片流
     *
     * @param jfreeChart
     * @param weight
     * @param height
     * @return
     */
    public InputStream getChartStream(JFreeChart jfreeChart, int weight, int height) {
        InputStream input = null;
        try {
            BufferedImage bufferedImage = jfreeChart.createBufferedImage(weight, height);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            input = new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            log.error("获取图片流时发生错误", e);
        }
        return input;
    }

}
