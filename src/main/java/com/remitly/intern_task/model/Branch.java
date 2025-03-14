package com.remitly.intern_task.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "Branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "country-iso2-code")
    private String countryIso2Code;

    @Column(name = "swift-code")
    private String swiftCode;

    @Column(name = "code_type")
    private String codeType;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "town_name")
    private String townName;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "time_zone")
    private String timeZone;

    @ManyToOne
    @JoinColumn(name = "headquarter", nullable = true)
    private Headquarter headquarter;
}
