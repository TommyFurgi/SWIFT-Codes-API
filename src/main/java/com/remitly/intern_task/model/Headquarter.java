package com.remitly.intern_task.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "Headquarters")
public class Headquarter {
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

    @OneToMany(mappedBy = "headquarter", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Branch> branches;

    public void addBranch(Branch branch) {
        if (branches != null) {
            branches.add(branch);
        }
    }

    public void remove(Branch branch) {
        if (branches != null) {
            branches.remove(branch);
        }
    }
}
