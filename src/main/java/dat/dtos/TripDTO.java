package dat.dtos;

import dat.entities.enums.TripCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripDTO {
    public int id;
    public String name;
    public double price;
    public String startposition;
    public LocalDateTime starttime;
    public LocalDateTime endtime;
    public TripCategory category;
    public Integer guideId;

    public TripDTO(int id, String name, double price, String startposition, LocalDateTime starttime, LocalDateTime endtime, TripCategory category, Integer integer) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.startposition = startposition;
        this.starttime = starttime;
        this.endtime = endtime;
        this.category = category;
        this.guideId = integer;
    }
}
