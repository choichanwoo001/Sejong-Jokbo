package com.sejong.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfService {
    
    /**
     * 텍스트 내용을 PDF로 변환하여 파일로 저장
     */
    public String createPdfFromText(String content, String uploaderName, String fileName) throws Exception {
        // 업로드 디렉토리 생성
        String uploadDir = "uploads/jokbo";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // PDF 파일 경로
        String pdfFileName = fileName + ".pdf";
        String pdfPath = uploadPath.resolve(pdfFileName).toString();
        
        // PDF 생성
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        
        // 한글 폰트 로드
        PDType0Font font = PDType0Font.load(document, new File("src/main/resources/fonts/NanumGothic.ttf"));
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        // 제목
        contentStream.beginText();
        contentStream.setFont(font, 18);
        contentStream.newLineAtOffset(250, 750);
        contentStream.showText("족보");
        contentStream.endText();
        
        // 업로더 정보
        contentStream.beginText();
        contentStream.setFont(font, 10);
        contentStream.newLineAtOffset(400, 720);
        contentStream.showText("업로더: " + uploaderName);
        contentStream.endText();
        
        // 구분선
        contentStream.setLineWidth(1);
        contentStream.moveTo(50, 710);
        contentStream.lineTo(550, 710);
        contentStream.stroke();
        
        // 내용
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(50, 680);
        
        // 텍스트 줄바꿈 처리
        String[] lines = content.split("\n");
        int yPosition = 680;
        
        for (String line : lines) {
            if (yPosition < 50) {
                // 새 페이지 추가
                contentStream.endText();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, 750);
                yPosition = 750;
            }
            
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, -20);
            yPosition -= 20;
        }
        
        contentStream.endText();
        contentStream.close();
        
        document.save(pdfPath);
        document.close();
        
        return pdfPath;
    }
    
    /**
     * 텍스트 내용을 PDF로 변환하여 바이트 배열로 반환
     */
    public byte[] createPdfBytesFromText(String content, String uploaderName) throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        
        // 한글 폰트 로드
        PDType0Font font = PDType0Font.load(document, new File("src/main/resources/fonts/NanumGothic.ttf"));
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        // 제목
        contentStream.beginText();
        contentStream.setFont(font, 18);
        contentStream.newLineAtOffset(250, 750);
        contentStream.showText("족보");
        contentStream.endText();
        
        // 업로더 정보
        contentStream.beginText();
        contentStream.setFont(font, 10);
        contentStream.newLineAtOffset(400, 720);
        contentStream.showText("업로더: " + uploaderName);
        contentStream.endText();
        
        // 구분선
        contentStream.setLineWidth(1);
        contentStream.moveTo(50, 710);
        contentStream.lineTo(550, 710);
        contentStream.stroke();
        
        // 내용
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(50, 680);
        
        // 텍스트 줄바꿈 처리
        String[] lines = content.split("\n");
        int yPosition = 680;
        
        for (String line : lines) {
            if (yPosition < 50) {
                // 새 페이지 추가
                contentStream.endText();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, 750);
                yPosition = 750;
            }
            
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, -20);
            yPosition -= 20;
        }
        
        contentStream.endText();
        contentStream.close();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        
        return baos.toByteArray();
    }
}
