package dat.services.mappers;

import dat.dtos.GuideDTO;
import dat.entities.Guide;

public class GuideMapper {

    public static GuideDTO toDTO(Guide guide) {
        return new GuideDTO(
                guide.getId(),
                guide.getFirstname(),
                guide.getLastname(),
                guide.getEmail(),
                guide.getPhone(),
                guide.getYearsOfExperience()
        );
    }

    public static Guide toEntity(GuideDTO dto) {
        Guide guide = new Guide();
        guide.setId(dto.getId());
        guide.setFirstname(dto.getFirstname());
        guide.setLastname(dto.getLastname());
        guide.setEmail(dto.getEmail());
        guide.setPhone(dto.getPhone());
        guide.setYearsOfExperience(dto.getYearsOfExperience());
        return guide;
    }
}
