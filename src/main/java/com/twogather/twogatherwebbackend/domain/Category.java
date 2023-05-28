package com.twogather.twogatherwebbackend.domain;

        import lombok.Getter;
        import lombok.NoArgsConstructor;

        import javax.persistence.*;
        import java.util.ArrayList;
        import java.util.List;
@Entity
@Getter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(unique = true)
    private String name;

    public Category(String name) {
        this.name = name;
    }

}
