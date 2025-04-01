package dat.services.mappers;

import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;

public class TripMapper {

    public static TripDTO toDTO(Trip trip) {
        TripDTO dto = new TripDTO(
                trip.getId(),
                trip.getName(),
                trip.getPrice(),
                trip.getStartPosition(),
                trip.getStartTime(),
                trip.getEndTime(),
                trip.getCategory(),
                trip.getGuide() != null ? trip.getGuide().getId() : null
        );

        if (trip.getGuide() != null) {
            dto.setGuideName(trip.getGuide().getFirstname() + " " + trip.getGuide().getLastname());
        }

        return dto;
    }

    public static Trip toEntity(TripDTO dto, Guide guide) {
        Trip trip = new Trip();
        trip.setId(dto.getId());
        trip.setName(dto.getName());
        trip.setPrice(dto.getPrice());
        trip.setStartPosition(dto.getStartPosition());
        trip.setStartTime(dto.getStartTime());
        trip.setEndTime(dto.getEndTime());
        trip.setCategory(dto.getCategory());
        trip.setGuide(guide);
        return trip;
    }
}
