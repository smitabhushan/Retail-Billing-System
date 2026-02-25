package in.ankitdaksh.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_id", unique = true)
    private String itemId;
    private String name;
    private BigDecimal price;
    private String description;
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
    private String imgUrl;
    @ManyToOne
    @JoinColumn(name="category_id",nullable = false)
    //Table me category_id naam ka foreign key banega aur product bina category ke save nahi ho sakta.

    @OnDelete(action = OnDeleteAction.RESTRICT)
    //Agar Category use ho rahi hai
    //To us Category ko delete nahi kar sakte
    private CategoryEntity category;//many items belong to one category

}
