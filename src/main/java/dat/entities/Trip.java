package dat.entities;

import dat.entities.enums.TripCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startPosition;
    private String name;
    private double price;

    @Enumerated(EnumType.STRING)
    private TripCategory category;

    @ManyToOne
    private Guide guide;

    public Trip(String name, String startPosition, LocalDateTime startTime, LocalDateTime endTime, double price, TripCategory category, Guide guide) {
        this.name = name;
        this.startPosition = startPosition;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.category = category;
        this.guide = guide;
    }
}