package com.example.springcloudconfigclient.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Organization {

    private Long id;

    private String name;

    private String contactName;

    private String contactEmail;

    private String contactPhone;

}
