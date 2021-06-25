package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Setter
@Getter
@Entity
@Table(name = "application_table")
public class ApplicationEntity {

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "group_id")
    private GroupEntity groupEntity;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "student_name", columnDefinition = "varchar(127)")
    private String studentName;

    // audit permit reject 进组
    @Column(name = "state", columnDefinition = "varchar(32)")
    private String state;

}
