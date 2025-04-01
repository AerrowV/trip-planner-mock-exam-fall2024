package dat.dtos;

import lombok.Data;

@Data
public class GuideDTO {
    public int id;
    public String firstname;
    public String lastname;
    public String email;
    public String phone;
    public int yearsOfExperience;

    public GuideDTO() {
    }

    public GuideDTO(int id, String firstname, String lastname, String email, String phone, int yearsOfExperience) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }
}
