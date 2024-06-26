package etu.kefengroup.turistomer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter @Setter @ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="hotel")
public class Hotel implements RecommendationEntity{
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "Hotel_Amenity", joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities;
}
