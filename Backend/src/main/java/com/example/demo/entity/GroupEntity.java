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
@Table(name = "group_table")
public class GroupEntity {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupEntity", orphanRemoval = true)
    private List<ApplicationEntity> applicationEntities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupEntity", orphanRemoval = true)
    private List<AnnexEntity> annexEntities = new ArrayList<>();

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(64)")
    private String name;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "project_id")
    private ProjectEntity projectEntity;

    @Column(name = "pro_id")
    private Long proId;

    @Column(name = "max_person")
    private Integer maxPerson;

    @Column(name = "describe", columnDefinition = "varchar(512)")
    private String describe;

    // invalid pre audit permit
    @Column(name = "state", columnDefinition = "varchar(32)")
    private String state;

    @Column(name = "leader_id")
    private Long leaderId;

    @Column(name = "select_project_id", columnDefinition = "varchar(32)")
    private String selectProjectId;

}
