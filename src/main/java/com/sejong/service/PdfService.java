package com.sejong.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class PdfService {

    /**
     * 텍스트 내용을 PDF로 변환하여 바이트 배열로 반환
     */
    public byte[] createPdfBytesFromText(String content, String uploaderName) throws Exception {
        // U+000D (CR) 문자 제거 - 폰트가 지원하지 않아 에러 발생 방지
        content = content.replace("\r", "");

        try (PDDocument document = new PDDocument()) {
            // 한글 폰트 로드
            // ClassPathResource를 사용하여 JAR/Docker 환경에서도 폰트 파일을 찾을 수 있도록 수정
            org.springframework.core.io.ClassPathResource fontResource = new org.springframework.core.io.ClassPathResource(
                    "fonts/NanumGothic.ttf");

            // 폰트 스트림도 try-with-resources로 관리
            PDType0Font font;
            try (java.io.InputStream fontStream = fontResource.getInputStream()) {
                log.info("폰트 로딩 시작: {}", fontResource.getFilename());
                font = PDType0Font.load(document, fontStream);
                log.info("폰트 로딩 성공");
            } catch (IOException e) {
                log.error("폰트 로딩 실패: {}", e.getMessage());
                throw new IOException("폰트 파일을 로드할 수 없습니다.", e);
            }

            // 페이지 설정
            float pageWidth = 595; // A4 너비
            float pageHeight = 842; // A4 높이
            float marginLeft = 50;
            float marginRight = 50;
            float marginTop = 50;
            float marginBottom = 50;
            float contentWidth = pageWidth - marginLeft - marginRight;

            // 첫 페이지 생성
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                // 제목
                contentStream.beginText();
                contentStream.setFont(font, 18);
                contentStream.newLineAtOffset((pageWidth - getTextWidth("족보", font, 18)) / 2, pageHeight - 80);
                contentStream.showText("족보");
                contentStream.endText();

                // 업로더 정보
                String uploaderText = "업로더: " + uploaderName;
                contentStream.beginText();
                contentStream.setFont(font, 10);
                contentStream.newLineAtOffset(pageWidth - marginRight - getTextWidth(uploaderText, font, 10),
                        pageHeight - 110);
                contentStream.showText(uploaderText);
                contentStream.endText();

                // 구분선
                contentStream.setLineWidth(1);
                contentStream.moveTo(marginLeft, pageHeight - 130);
                contentStream.lineTo(pageWidth - marginRight, pageHeight - 130);
                contentStream.stroke();

                // 텍스트 내용 처리
                float yPosition = pageHeight - 160;
                float lineHeight = 18;
                int fontSize = 12;

                // 문단별로 분리
                String[] paragraphs = content.split("\n\n");

                for (String paragraph : paragraphs) {
                    if (paragraph.trim().isEmpty()) {
                        continue;
                    }

                    // 각 문단을 줄바꿈 처리
                    java.util.List<String> wrappedLines = wrapText(paragraph.trim(), font, fontSize, contentWidth);

                    for (String line : wrappedLines) {
                        // 페이지 끝에 도달하면 새 페이지 생성
                        if (yPosition < marginBottom + lineHeight) {
                            contentStream.close(); // 현재 스트림 닫기
                            page = new PDPage();
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            yPosition = pageHeight - marginTop;
                        }

                        // 텍스트 출력
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);
                        contentStream.newLineAtOffset(marginLeft, yPosition);
                        contentStream.showText(line);
                        contentStream.endText();

                        yPosition -= lineHeight;
                    }

                    // 문단 간격 추가
                    yPosition -= lineHeight / 2;
                }
            } finally {
                // 마지막 스트림 닫기
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);

            return baos.toByteArray();
        }
    }

    /**
     * 텍스트 너비를 계산합니다
     */
    private float getTextWidth(String text, PDType0Font font, int fontSize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }

    /**
     * 텍스트를 주어진 너비에 맞게 줄바꿈 처리합니다
     */
    private java.util.List<String> wrapText(String text, PDType0Font font, int fontSize, float maxWidth)
            throws IOException {
        java.util.List<String> lines = new java.util.ArrayList<>();

        // 이미 줄바꿈이 있는 경우 먼저 분리
        String[] existingLines = text.split("\n");

        for (String line : existingLines) {
            if (line.trim().isEmpty()) {
                lines.add("");
                continue;
            }

            // 한 줄이 최대 너비를 넘지 않으면 그대로 추가
            if (getTextWidth(line, font, fontSize) <= maxWidth) {
                lines.add(line);
                continue;
            }

            // 긴 줄은 단어 단위로 분리하여 줄바꿈
            String[] words = line.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

                if (getTextWidth(testLine, font, fontSize) <= maxWidth) {
                    currentLine = new StringBuilder(testLine);
                } else {
                    // 현재 줄이 비어있지 않으면 추가
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else {
                        // 단어 자체가 너무 긴 경우 문자 단위로 분리
                        lines.addAll(wrapLongWord(word, font, fontSize, maxWidth));
                    }
                }
            }

            // 마지막 줄 추가
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }

        return lines;
    }

    /**
     * 너무 긴 단어를 문자 단위로 분리합니다
     */
    private java.util.List<String> wrapLongWord(String word, PDType0Font font, int fontSize, float maxWidth)
            throws IOException {
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (char c : word.toCharArray()) {
            String testLine = currentLine.toString() + c;

            if (getTextWidth(testLine, font, fontSize) <= maxWidth) {
                currentLine.append(c);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(String.valueOf(c));
                } else {
                    // 단일 문자도 너비를 초과하는 경우 (폰트 크기가 너무 큰 경우)
                    lines.add(String.valueOf(c));
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}
