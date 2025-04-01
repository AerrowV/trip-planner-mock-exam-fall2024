package dat.dtos;

import dat.entities.enums.TripCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripDTO {
    public int id;
    public String name;
    public double price;
    public String startPosition;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public TripCategory category;
    public Integer guideId;
    private String guideName;

    public TripDTO() {
    }

    public TripDTO(int id, String name, double price, String startPosition, LocalDateTime startTime, LocalDateTime endTime, TripCategory category, Integer integer) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.startPosition = startPosition;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.guideId = integer;
    }
}
