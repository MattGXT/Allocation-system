package com.example.demo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Setter
@Getter
@Entity
@Table(name = "project_table")
public class ProjectEntity {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectEntity", orphanRemoval = true)
    private List<GroupEntity> groupEntities = new ArrayList<>();

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(64)")
    private String name;

    @Column(name = "client", columnDefinition = "varchar(64)")
    private String client;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "email", columnDefinition = "varchar(64)")
    private String email;

    @Column(name = "company", columnDefinition = "varchar(64)")
    private String company;

    @Column(name = "describe", columnDefinition = "varchar(512)")
    private String describe;

    @Column(name = "skill_require", columnDefinition = "varchar(512)")
    private String skillRequire;
    // audit publish finish reject
    @Column(name = "state", columnDefinition = "varchar(32)")
    private String state;

    @Column(name = "is_need_annex", columnDefinition = "varchar(32)")
    private String isNeedAnnex;

    @Column(name = "apply_start_time")
    private Date applyStartTime;

    @Column(name = "apply_end_time")
    private Date applyEndTime;

    @Column(name = "group_number")
    private Integer groupNumber;

    @Column(name = "audit_count")
    private Integer auditCount;

    @Column(name = "permit_count")
    private Integer permitCount;

    @Column(name = "unique_id", columnDefinition = "varchar(64)")
    private String uniqueId;

}
