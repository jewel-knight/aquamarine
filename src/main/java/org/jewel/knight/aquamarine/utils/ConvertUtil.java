package org.jewel.knight.aquamarine.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author impactCn
 * @date 2023/11/30 11:13
 */
public class ConvertUtil {

    public static void toPdf(String content, String cssUrl, String name) {
        try {
            ConverterProperties converterProperties = new ConverterProperties()
                    .setBaseUri(cssUrl);

            PdfWriter pdfWriter = new PdfWriter(new File(name));
            HtmlConverter.convertToPdf(content, pdfWriter, converterProperties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
