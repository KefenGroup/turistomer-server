package etu.kefengroup.turistomer.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="name")
    @NonNull
    private String name;

    @Column(name="city")
    @NonNull
    private String city;

    @Column(name="link")
    @NonNull
    private String link;

    @Column(name="rating")
    private float rating = -1;        //default value for rating is -1

    @Column(name="price")
    private int price = -1;    //default value for price is -1

    @Column(name="longitude")
    private float longitude;

    @Column(name="latitude")
    private float latitude;

}
