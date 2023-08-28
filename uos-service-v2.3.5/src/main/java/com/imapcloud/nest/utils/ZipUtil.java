package com.imapcloud.nest.utils;

/**
 * @deprecated 2.2.3，将在后续版本删除
 */
@Deprecated
public class ZipUtil {

//    public static  void downloadZip(List<MissionPhotoDefectDto> resultList, String zipName ,HttpServletResponse response) throws Exception {
//
//        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
//        NumberFormat numberFormat = NumberFormat.getNumberInstance();
//        numberFormat.setMinimumIntegerDigits(4);
//        numberFormat.setGroupingUsed(false);
//        String downloadFilename = zipName;// 文件的名称
//        downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");// 转换中文否则可能会产生乱码
//        response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
//        response.setHeader("Content-Disposition","attachment;filename="+downloadFilename);// 设置在下载框默认显示的文件名
//        for (int j = 1; j <= resultList.size(); j++) {
//            zos.putNextEntry(new ZipEntry(resultList.get(j-1).getFileZipPath()));
//            InputStream fis = new FileInputStream(new File(resultList.get(j-1).getFilePath()));
//            byte[] buffer = new byte[1024];
//            int r = 0;
//            while ((r = fis.read(buffer)) != -1) {
//                zos.write(buffer, 0, r);
//            }
//            fis.close();
//        }
//        zos.flush();
//        zos.close();
//    }

}
