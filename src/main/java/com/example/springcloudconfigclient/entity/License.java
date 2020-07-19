package com.example.springcloudconfigclient.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class License {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer max;

    @Column(nullable = false)
    private Integer allocated;

    private String comment;

    @Transient
    private Organization organization;



}
