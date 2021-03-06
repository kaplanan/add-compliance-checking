package com.example.demo.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@NoArgsConstructor
@Table(name = "pattern_instance")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PatternInstance implements Serializable {


    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "isActive", nullable = false)
    @JsonProperty(value="isActive")
    private Boolean isActive;

    @NotNull
    @Lob
    @Column(name = "constraintStatement", nullable = false)
    private String constraintStatement;

    @Column(name = "isViolated")
    @JsonProperty(value="isViolated")
    private Boolean isViolated;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatternVariable> variables = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getConstraintStatement() {
        return constraintStatement;
    }

    public void setConstraintStatement(String constraintStatement) {
        this.constraintStatement = constraintStatement;
    }

    public Boolean getIsViolated() {
        return isViolated;
    }

    public void setIsViolated(Boolean violated) {
        isViolated = violated;
    }

    public Set<PatternVariable> getVariables() {
        return variables;
    }

    public void setVariables(Set<PatternVariable> variables) {
        this.variables = variables;
    }
}
