package com.sejong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "book")
@Getter
@Setter
public class Book extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "author", length = 100)
    private String author;
    
    @Column(name = "publisher", length = 100)
    private String publisher;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Jokbo> jokbos;
} 