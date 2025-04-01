package dat.services;

import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;

public class TripMapper {

    public static TripDTO toDTO(Trip trip) {
        return new TripDTO(
                trip.getId(),
                trip.getName(),
                trip.getPrice(),
                trip.getStartPosition(),
                trip.getStartTime(),
                trip.getEndTime(),
                trip.getCategory(),
                trip.getGuide() != null ? trip.getGuide().getId() : null
        );
    }

    public static Trip toEntity(TripDTO dto, Guide guide) {
        Trip trip = new Trip();
        trip.setId(dto.getId());
        trip.setName(dto.getName());
        trip.setPrice(dto.getPrice());
        trip.setStartPosition(dto.getStartposition());
        trip.setStartTime(dto.getStarttime());
        trip.setEndTime(dto.getEndtime());
        trip.setCategory(dto.getCategory());
        trip.setGuide(guide);
        return trip;
    }
}
