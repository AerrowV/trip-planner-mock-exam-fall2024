package dat.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuideTotalDTO {
    private int guideId;
    private String guideName;
    private double totalPrice;
}
