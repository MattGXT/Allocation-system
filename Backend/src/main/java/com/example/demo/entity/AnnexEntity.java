package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Data
@Setter
@Getter
@Entity
@Table(name = "annex_table")
public class AnnexEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "group_id")
    private GroupEntity groupEntity;


    /**
     * 附件名
     */
    @Column(name = "name", columnDefinition = "varchar(127)")
    private String name;

    /**
     * 附件存储地址
     */
    @Column(name = "address", columnDefinition = "varchar(127)")
    private String address;

    /**
     * 附件大小
     */
    @Column(name = "file_size", columnDefinition = "varchar(127)")
    private String fileSize;
}
