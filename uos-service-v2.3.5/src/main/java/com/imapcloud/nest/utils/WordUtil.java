package com.imapcloud.nest.utils;

import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
public class WordUtil {

    public static void exportWord(String fileName, Map<String, Object> params, HttpServletResponse response) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        ClassPathResource oldDoc = new ClassPathResource("word/airspaceTemplate.docx");
        InputStream inputStream = null;
        try {
            inputStream = oldDoc.getInputStream();
            XWPFTemplate template = XWPFTemplate.compile(inputStream)
                    .render(params);
            OutputStream os = response.getOutputStream();
            template.writeAndClose(os);
            os.close();
        } catch (IOException e) {
            log.error("#WordUtil.exportWord# error:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("#WordUtil.exportWord# close error:", e);
                }
            }
        }
    }
}
