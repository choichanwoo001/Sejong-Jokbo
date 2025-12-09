package com.sejong.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@Slf4j
public class PdfService {

    /**
     * 텍스트 내용을 PDF로 변환하여 바이트 배열로 반환
     */
    public byte[] createPdfBytesFromText(String title, String content, String uploaderName) throws Exception {
        // 1. HTML 구조 생성
        // 기본 스타일 및 폰트 설정
        // OpenHTMLtoPDF는 엄격한 XML 문법을 요구하므로 Jsoup을 사용하여 XHTML로 변환합니다.

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><style>");
        htmlBuilder.append("@page { size: A4; margin: 20mm; }");
        htmlBuilder.append("body { font-family: 'NanumGothic'; line-height: 1.6; font-size: 11pt; }");
        htmlBuilder.append(".title { font-size: 18pt; font-weight: bold; text-align: center; margin-bottom: 20px; }");
        htmlBuilder.append(
                ".uploader { text-align: right; margin-bottom: 20px; font-size: 10pt; color: #555; border-bottom: 1px solid #ddd; padding-bottom: 10px; }");
        htmlBuilder.append(".content { font-size: 11pt; }");
        htmlBuilder.append(".content p { margin-top: 0; margin-bottom: 5px; }");

        // 이미지 태그 스타일 (이미지가 있다면)
        htmlBuilder.append("img { max-width: 100%; height: auto; }");
        htmlBuilder.append("</style></head><body>");

        htmlBuilder.append("<div class='title'>").append(escapeHtml(title)).append("</div>");
        htmlBuilder.append("<div class='uploader'>업로더: ").append(escapeHtml(uploaderName)).append("</div>");
        htmlBuilder.append("<div class='content'>");

        // content는 이미 HTML 태그를 포함하고 있을 수 있음 (에디터 출력 등)
        // 하지만 사용자가 제공한 content가 단순 텍스트라면 줄바꿈 처리 필요
        // Jsoup으로 안전하게 정리하되, 스타일 태그 등은 허용해야 함
        // 여기서는 content가 HTML 형태라고 가정하고 body만 추출하거나, 안전하게 감쌉니다.

        // Jsoup을 사용하여 body 내용만 추출 및 정리 (XHTML 호환)
        Document doc = Jsoup.parseBodyFragment(content);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // XML 문법 강제
        doc.outputSettings().prettyPrint(false); // 소스 코드 포맷팅 비활성화 (불필요한 공백 제거)
        htmlBuilder.append(doc.body().html());

        htmlBuilder.append("</div>");
        htmlBuilder.append("</body></html>");

        // 최종 XHTML 문자열 생성
        Document finalDoc = Jsoup.parse(htmlBuilder.toString());
        finalDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // 중요: OpenHTMLtoPDF는 XML 필요
        finalDoc.outputSettings().prettyPrint(false); // 최종 결과물도 포맷팅 비활성화
        String finalHtml = finalDoc.html();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode(); // 성능 최적화
            builder.withHtmlContent(finalHtml, null);

            // 폰트 등록 (Bold, Regular)
            // ClassPathResource를 사용하여 InputStream 공급
            builder.useFont(() -> {
                try {
                    return new ClassPathResource("fonts/NanumGothic-Regular.ttf").getInputStream();
                } catch (Exception e) {
                    log.error("Regular 폰트 로드 실패", e);
                    throw new RuntimeException(e);
                }
            }, "NanumGothic", 400, PdfRendererBuilder.FontStyle.NORMAL, true);

            builder.useFont(() -> {
                try {
                    return new ClassPathResource("fonts/NanumGothic-Bold.ttf").getInputStream();
                } catch (Exception e) {
                    log.error("Bold 폰트 로드 실패", e);
                    throw new RuntimeException(e);
                }
            }, "NanumGothic", 700, PdfRendererBuilder.FontStyle.NORMAL, true);

            // Bold.ttf가 없을 경우를 대비하여 700 weight에 대해서도 대응하도록 설정
            // (만약 Bold 파일이 없다면 위에서 에러가 날 수 있으므로 파일 존재 여부 체크하거나 try-catch 주의)
            // 사용자 파일 목록에 NanumGothic-Bold.ttf가 있었으므로 안전함.

            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        }
    }

    private String escapeHtml(String text) {
        if (text == null)
            return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
