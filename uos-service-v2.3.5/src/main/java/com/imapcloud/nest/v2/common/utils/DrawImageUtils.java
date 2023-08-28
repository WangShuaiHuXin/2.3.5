package com.imapcloud.nest.v2.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.geoai.common.core.util.DateUtils;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterDefectMarkOutDO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DrawImage.java
 * @Description DrawImageUtils
 * @createTime 2022年07月18日 10:56:00
 */
@Slf4j
public class DrawImageUtils {

    /**
     * 固定5k图片按5.0f线宽 ,需要用原图乘以比例算出当前图片要用多少大小的线
     * 同时还得根据放大缩小比例，等比除以放大倍率
     */
    public final static BigDecimal linePower = new BigDecimal("0.002");

    /**
     * 固定5k图片按线宽 ,需要用原图乘以比例算出当前图片要用多少大小的线
     * 同时还得根据放大缩小比例，等比除以放大倍率
     */
    public final static BigDecimal fontPower = new BigDecimal("0.02");

    /**
     * 标注图片，根据缩放、裁剪比例
     *
     * @param pic
     * @return
     */
    public static InputStream drawImage(Pic pic, String srcImageName, InputStream inputStream) throws IOException {
        if(StringUtils.isEmpty(srcImageName) || Objects.isNull(inputStream)){
            return null;
        }
        log.info("打印:【DrawImageUtils】-【drawImage】:srcImageName->{},pic->{}", srcImageName, pic.toString());

        BufferedImage imageIO = ImageIO.read(inputStream);
        BigDecimal width = BigDecimal.valueOf(imageIO.getWidth()), height = BigDecimal.valueOf(imageIO.getHeight());
        pic.computeValue(width, height);

        //绘制-总的
        Graphics gs = imageIO.getGraphics();
        Graphics2D g2 = (Graphics2D) gs;
        //黄色线框
        g2.setColor(new Color(255, 0, 0));
        //框的线长度
        BigDecimal lineWidth = width.multiply(linePower).divide(pic.getScale(), BigDecimal.ROUND_CEILING).setScale(1, RoundingMode.HALF_UP);
        log.info("【drawImage】字体大小：lineWidth -> {}", lineWidth);
        g2.setStroke(new BasicStroke(lineWidth.floatValue()));
        g2.drawRect(pic.getX1().intValue(), pic.getY1().intValue()
                , pic.getW1().intValue(), pic.getH1().intValue());
        //==============================================================================
        String str = pic.getDesc();
        if (StringUtils.hasText(str)) {

            BigDecimal fontSize = width.multiply(fontPower).divide(pic.getScale(), BigDecimal.ROUND_CEILING).setScale(1, RoundingMode.HALF_UP);
            log.info("【drawImage】字体大小：fontSize -> {}", fontSize);
            Font font = new Font("宋体", Font.PLAIN, fontSize.intValue());
            g2.setFont(font);
            g2.setColor(Color.WHITE);
//            g2.fillRect(pic.getX1().intValue(),pic.getY1().intValue()-font.getSize(),str.length()*10,font.getSize());

            int x = pic.getX1().intValue(), y = pic.getY1().intValue() - lineWidth.intValue();
            //如果没有缩放，需要判断是否在最上或者最右，调整文字位置
            if (BigDecimal.ONE.compareTo(pic.getScale()) == 0) {
                if (pic.getY1().compareTo(fontSize.add(lineWidth)) < 0) {
                    y = pic.getY1().add(fontSize).add(lineWidth).add(pic.getH1()).intValue();
                }
                if (width.compareTo(pic.getX1().add(BigDecimal.valueOf(str.length()).multiply(fontSize))) < 0) {
                    x = pic.getX1().add(pic.getW1()).subtract(BigDecimal.valueOf(str.length()).multiply(fontSize)).intValue();
                }
            }
//            g2.setColor(Color.blue);
//            g2.fillRect(x,y-fontSize.intValue(),str.length()*10,fontSize.intValue());
//            g2.setColor(Color.WHITE);
            //写文字
            g2.drawString(str, x, y);
            //如果是现场取证，需要添加水印
//            if(DataAnalysisPicSourceEnum.DATA_SCENE.getType() == pic.getSrcDataType().intValue()){
//
//            }
            // 问题图片添加时间水印
            LocalDateTime time = pic.getPhotoCreateTime();
            if (time == null) {
                time = LocalDateTime.now();
            }
            String dateStr = time.format(DateUtils.DATE_TIME_FORMATTER_OF_CN);
            //偏移量 + 截图框宽75%\截图框高95%的位置
            x = pic.getX2().abs().add(pic.getW2().multiply(BigDecimal.valueOf(0.75))).intValue();
            y = pic.getY2().abs().add(pic.getH2().multiply(BigDecimal.valueOf(0.95))).intValue();
            g2.drawString(dateStr, x, y);

        }
        //缩放
        //定义框大小，这里转化后，还需要根据缩放比例，进行缩放
        BufferedImage bi = new BufferedImage((pic.getW2().multiply(pic.getScale())).intValue()
                , (pic.getH2().multiply(pic.getScale())).intValue()
                , BufferedImage.TYPE_INT_RGB);
        Graphics2D g2dd = (Graphics2D) bi.getGraphics();

        g2dd.drawImage(imageIO, (pic.getX2().multiply(pic.getScale())).intValue()
                , (pic.getY2().multiply(pic.getScale())).intValue()
                , (pic.getW().multiply(pic.getScale())).intValue()
                , (pic.getH().multiply(pic.getScale())).intValue(), null);
        g2dd.dispose();
        g2.dispose();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(bi, "jpg", os);
            return new ByteArrayInputStream(os.toByteArray());
//            try(InputStream in = new ByteArrayInputStream(os.toByteArray())){
//                MinIoUnit.putObject(descPath, in);
//            }
            //上传到MinIO
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 标注图片，根据缩放、裁剪比例
     */
    public static InputStream drawImages(List<Pic> pics, String srcImageName, InputStream inputStream) throws IOException {
        if(StringUtils.isEmpty(srcImageName) || Objects.isNull(inputStream)){
            return null;
        }
        log.info("打印:【DrawImageUtils】-【drawImage】:srcImageName->{}, pics->{}", srcImageName, pics.toString());
        BufferedImage imageIO = ImageIO.read(inputStream);
        BigDecimal width = BigDecimal.valueOf(imageIO.getWidth()), height = BigDecimal.valueOf(imageIO.getHeight());


        //绘制-总的
        Graphics gs = imageIO.getGraphics();
        Graphics2D g2 = (Graphics2D) gs;
        //框的线长度
        BigDecimal lineWidth = width.multiply(linePower).setScale(1, RoundingMode.HALF_UP);
        g2.setStroke(new BasicStroke(lineWidth.floatValue()));
        for (Pic p : pics) {
            g2.setColor(new Color(255, 0, 0));
            p.computeValue(width, height);
            g2.drawRect(p.getX1().intValue(), p.getY1().intValue()
                    , p.getW1().intValue(), p.getH1().intValue());

            String str = p.getDesc();
            if (StringUtils.hasText(str)) {
                BigDecimal fontSize = width.multiply(fontPower).divide(p.getScale(), BigDecimal.ROUND_CEILING).setScale(1, RoundingMode.HALF_UP);
                log.info("【drawImage】字体大小： fontSize -> {}", fontSize);
                Font font = new Font("宋体", Font.PLAIN, fontSize.intValue());
                g2.setFont(font);

                g2.setColor(Color.white);
                //写文字
                int x = p.getX1().intValue(), y = p.getY1().intValue() - lineWidth.intValue();
                //如果没有缩放，需要判断是否在最上或者最右，调整文字位置
                if (BigDecimal.ONE.compareTo(p.getScale()) == 0) {
                    if (p.getY1().compareTo(fontSize.add(lineWidth)) < 0) {
                        y = p.getY1().add(fontSize).add(lineWidth).add(p.getH1()).intValue();
                    }
//                    log.info("打印文字:{},{},{}",width,p.getX1(),BigDecimal.valueOf(str.length()).multiply(fontSize));
                    if (width.compareTo(p.getX1().add(BigDecimal.valueOf(str.length()).multiply(fontSize))) < 0) {
                        x = p.getX1().add(p.getW1()).subtract(BigDecimal.valueOf(str.length()).multiply(fontSize)).intValue();
                    }
                }
//                g2.setColor(Color.blue);
//                g2.fillRect(x,y,str.length()*10,fontSize.intValue());
//                g2.setColor(Color.WHITE);
                g2.drawString(str, x, y);
            }
        }
        //==============================================================================
        Pic pic = pics.get(0);
        //缩放
        //定义框大小，这里转化后，还需要根据缩放比例，进行缩放
        BufferedImage bi = new BufferedImage(pic.getW().intValue()
                , pic.getH().intValue()
                , BufferedImage.TYPE_INT_RGB);
        Graphics2D g2dd = (Graphics2D) bi.getGraphics();

        g2dd.drawImage(imageIO, 0
                , 0
                , width.intValue()
                , height.intValue(), null);
        log.info("打印日志：pic-all->{}", pic.toString());
        g2dd.dispose();
        g2.dispose();
        String filenameExtension = StringUtils.getFilenameExtension(srcImageName);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(bi, StringUtils.hasText(filenameExtension) ? filenameExtension : "jpg", os);
            return new ByteArrayInputStream(os.toByteArray());
//            try(InputStream in = new ByteArrayInputStream(os.toByteArray())){
//                MinIoUnit.putObject(descPath, in);
//            }
        }catch (Exception e){
            log.error("ImageIO.write error", e);
            throw new BusinessException(e.getMessage());
        }
    }

//    /**
//     * 生成缩略图
//     * @return
//      * @Deprecated 2.2.3
//     */
//    @Deprecated
//    public static String thumbnailImage(String srcPath , String descPath) throws IOException {
//        if(StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(descPath)){
//            return "";
//        }
//        String descName = descPath.substring(descPath.lastIndexOf("/")+1);
//        String descRoute = descPath.substring(0,descPath.lastIndexOf("/"));
//        log.info("打印:【DrawImageUtils】-【thumbnailImage】:srcPath->{},descPath->{}",srcPath,descPath);
//        BufferedImage originalImage = ImageIO.read(MinIoUnit.getObject(srcPath));
//        BufferedImage thumbnail = Thumbnails.of(originalImage)
//                .scale(0.54)
//                .asBufferedImage();
//        try(ByteArrayOutputStream os = new ByteArrayOutputStream()){
//            ImageIO.write(thumbnail, "jpg", os);
//            try(InputStream in = new ByteArrayInputStream(os.toByteArray())){
//                MinIoUnit.putObject(descPath, in);
//            }
//            //上传到MinIO
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new BusinessException(e.getMessage());
//        }
//        return descPath;
//    }

//    /**
//     * @deprecated 2.2.3，使用文件服务提供的生成缩略图接口，将在后续版本删除
//     */
//    @Deprecated
//    public static boolean thumbnailImage(InputStream is, String thumbnailPath, BigDecimal scale) {
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        InputStream inputStream = null;
//        try {
//            BufferedImage read = ImageIO.read(is);
//            Img.from(read).scale(scale.floatValue()).write(os);
//            inputStream = new ByteArrayInputStream(os.toByteArray());
//            return MinIoUnit.putObject(thumbnailPath, inputStream);
//        } catch (Exception e) {
//            log.error("#DrawImageUtils.thumbnailImage# thumbnailPath={}", thumbnailPath, e);
//        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//                os.close();
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            } catch (Exception e) {
//                log.error("#DrawImageUtils.thumbnailImage# close thumbnailPath={}", thumbnailPath, e);
//            }
//        }
//        return false;
//    }

    public static Pair<Integer, Integer> getImageWidthAndHeight(InputStream inputStream) {
        try {
            BufferedImage originalImage = ImageIO.read(inputStream);
            return new Pair<>(originalImage.getWidth(), originalImage.getHeight());
        } catch (IOException e) {
            log.error("#DrawImageUtils.getImageWidthAndHeight#", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("#DrawImageUtils.getImageWidthAndHeight# close ", e);
                }
            }
        }
        return new Pair<>(0, 0);
    }

//    /**
//     * 生成水印
//     *
//     * @param srcPath
//     * @param descPath
//     * @return
//     */
//    public static String createWaterMark(String srcPath, String descPath) throws IOException {
//        BufferedImage originalImage = ImageIO.read(MinIoUnit.getObject(srcPath));
//        BufferedImage watermarkImage = ImageIO.read(MinIoUnit.getObject(srcPath));
//        String descName = descPath.substring(descPath.lastIndexOf("/") + 1);
//        String descRoute = descPath.substring(0, descPath.lastIndexOf("/"));
//
//        BufferedImage thumbnail = Thumbnails.of(originalImage)
//                .scale(0.5)
//                .watermark(Positions.BOTTOM_RIGHT, watermarkImage, 0.5f)
//                .asBufferedImage();
//        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
//            ImageIO.write(thumbnail, "jpg", os);
//            try (InputStream in = new ByteArrayInputStream(os.toByteArray())) {
//                MinIoUnit.putObject(descPath, in);
//            }
//            //上传到MinIO
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BusinessException(e.getMessage());
//        }
//
//        return descPath;
//    }

    /**
     * H20T：640×512
     * 御二进阶：640×512
     * 御二行业双光：640×480（4:3）；640×360（16:9）
     * 道通：640*512
     *
     * @return
     */
    public static boolean imageIsRedType(InputStream inputStream) {

        Pair<Integer, Integer> imageWidthAndHeight = getImageWidthAndHeight(inputStream);
        if (imageWidthAndHeight.getKey() == 640 && imageWidthAndHeight.getValue() == 512) {
            return true;
        }
        if (imageWidthAndHeight.getKey() == 640 && imageWidthAndHeight.getValue() == 480) {
            return true;
        }
        if (imageWidthAndHeight.getKey() == 640 && imageWidthAndHeight.getValue() == 360) {
            return true;
        }
        return false;
    }

//    /**
//     * @deprecated 2.2.3, 将在后续版本删除
//     */
//    @Deprecated
//    public static String drawImgsDefect(List<PowerMeterDefectMarkOutDO> pics, String srcPath, String descPath, LocalDateTime time) throws IOException {
//        if (StringUtils.isEmpty(srcPath)) {
//            return "";
//        }
//        if(CollectionUtil.isEmpty(pics)){
//            return "";
//        }
//        log.info("打印:【DrawImageUtils】-【drawImage】:srcPath->{},descPath->{},pics->{}", srcPath, descPath, pics.toString());
//        BufferedImage imageIO = ImageIO.read(MinIoUnit.getObject(srcPath));
//        BigDecimal width = BigDecimal.valueOf(imageIO.getWidth()), height = BigDecimal.valueOf(imageIO.getHeight());
//        //绘制-总的
//        Graphics gs = imageIO.getGraphics();
//        Graphics2D g2 = (Graphics2D) gs;
//        //框的线长度
//        BigDecimal lineWidth = width.multiply(linePower).setScale(1, RoundingMode.HALF_UP);
//        g2.setStroke(new BasicStroke(lineWidth.floatValue()));
//        for (PowerMeterDefectMarkOutDO p : pics) {
//            g2.setColor(new Color(255, 0, 0));
//            int reacWith = p.getSiteX2().subtract(p.getSiteX1()).multiply(width).intValue();
//            int reacHeight = p.getSiteY2().subtract(p.getSiteY1()).multiply(height).intValue();
//            g2.drawRect(p.getSiteX1().multiply(width).intValue(), p.getSiteY1().multiply(height).intValue()
//                    , reacWith, reacHeight);
//
//            String str = p.getTopicProblemName();
//            if (StringUtils.hasText(str)) {
//                BigDecimal fontSize = width.multiply(fontPower).setScale(1, RoundingMode.HALF_UP);
//                log.info("【drawImage】字体大小： fontSize -> {}", fontSize);
//                Font font = new Font("宋体", Font.PLAIN, fontSize.intValue());
//                g2.setFont(font);
//
//                g2.setColor(Color.WHITE);
//                //写文字
//                int x = p.getSiteX1().multiply(width).intValue(), y = p.getSiteY1().multiply(height).intValue() - lineWidth.intValue();
//                //如果没有缩放，需要判断是否在最上或者最右，调整文字位置
//                g2.drawString(str, x, y);
//            }
//
//            if (time == null) {
//                time = LocalDateTime.now();
//            }
//            String dateStr = time.format(DateUtils.DATE_TIME_FORMATTER_OF_CN);
//            //偏移量 + 截图框宽75%\截图框高95%的位置
//            g2.drawString(dateStr, p.getSiteX2().abs().add(width.multiply(BigDecimal.valueOf(0.75))).intValue(),
//                    p.getSiteY2().abs().add(height.multiply(BigDecimal.valueOf(0.95))).intValue());
//        }
//        //==============================================================================
//        log.info("打印日志：pic-all->{}", pics.toString());
//        g2.dispose();
//        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
//            ImageIO.write(imageIO, "jpg", os);
//            try (InputStream in = new ByteArrayInputStream(os.toByteArray())) {
//                String picPath = String.format("%s%s.%s", UploadTypeEnum.MINIO_COMMON_PICTURE.getPath(), BizIdUtils.snowflakeIdStr(), org.springframework.util.StringUtils.getFilenameExtension(srcPath));
//                MinIoUnit.putObject(picPath, in);
//                return descPath + picPath;
//            }
//            //上传到MinIO
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BusinessException(e.getMessage());
//        }
//    }

    public static InputStream drawDefectImage(List<PowerMeterDefectMarkOutDO> pics, InputStream srcImgInputStream, String srcFileFormat, LocalDateTime time) throws IOException {
        if (Objects.isNull(srcImgInputStream)) {
            return null;
        }
        if(CollectionUtil.isEmpty(pics)){
            return null;
        }
        log.info("打印:【DrawImageUtils】-【drawImgsDefect】:pics->{}", pics);
        BufferedImage imageIO = ImageIO.read(srcImgInputStream);
        BigDecimal width = BigDecimal.valueOf(imageIO.getWidth()), height = BigDecimal.valueOf(imageIO.getHeight());
        //绘制-总的
        Graphics gs = imageIO.getGraphics();
        Graphics2D g2 = (Graphics2D) gs;
        //框的线长度
        BigDecimal lineWidth = width.multiply(linePower).setScale(1, RoundingMode.HALF_UP);
        g2.setStroke(new BasicStroke(lineWidth.floatValue()));
        for (PowerMeterDefectMarkOutDO p : pics) {
            g2.setColor(new Color(255, 0, 0));
            int reacWith = p.getSiteX2().subtract(p.getSiteX1()).multiply(width).intValue();
            int reacHeight = p.getSiteY2().subtract(p.getSiteY1()).multiply(height).intValue();
            g2.drawRect(p.getSiteX1().multiply(width).intValue(), p.getSiteY1().multiply(height).intValue()
                    , reacWith, reacHeight);

            String str = p.getTopicProblemName();
            if (StringUtils.hasText(str)) {
                BigDecimal fontSize = width.multiply(fontPower).setScale(1, RoundingMode.HALF_UP);
                log.info("【drawImage】字体大小： fontSize -> {}", fontSize);
                Font font = new Font("宋体", Font.PLAIN, fontSize.intValue());
                g2.setFont(font);

                g2.setColor(Color.WHITE);
                //写文字
                int x = p.getSiteX1().multiply(width).intValue(), y = p.getSiteY1().multiply(height).intValue() - lineWidth.intValue();
                //如果没有缩放，需要判断是否在最上或者最右，调整文字位置
                g2.drawString(str, x, y);
            }

            if (time == null) {
                time = LocalDateTime.now();
            }
            String dateStr = time.format(DateUtils.DATE_TIME_FORMATTER_OF_CN);
            //偏移量 + 截图框宽75%\截图框高95%的位置
            g2.drawString(dateStr, p.getSiteX2().abs().add(width.multiply(BigDecimal.valueOf(0.75))).intValue(),
                    p.getSiteY2().abs().add(height.multiply(BigDecimal.valueOf(0.95))).intValue());
        }
        //==============================================================================
        log.info("打印日志：pic-all->{}", pics.toString());
        g2.dispose();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(imageIO, StringUtils.hasText(srcFileFormat) ? srcFileFormat : "jpg", os);
            return new ByteArrayInputStream(os.toByteArray());
//            try (InputStream in = new ByteArrayInputStream(os.toByteArray())) {
//                String picPath = String.format("%s%s.%s", UploadTypeEnum.MINIO_COMMON_PICTURE.getPath(), BizIdUtils.snowflakeIdStr(), org.springframework.util.StringUtils.getFilenameExtension(srcPath));
//                MinIoUnit.putObject(picPath, in);
//                return descPath + picPath;
//            }
            //上传到MinIO
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }
}
