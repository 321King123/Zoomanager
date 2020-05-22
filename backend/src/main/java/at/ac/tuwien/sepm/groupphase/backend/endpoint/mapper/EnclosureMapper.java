package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import org.mapstruct.Mapper;

import java.util.Arrays;

@Mapper
public class EnclosureMapper {

    public EnclosureDto enclosureToEnclosureDto(Enclosure enclosure) {
        if(enclosure == null) {
            return null;
        }

        if(enclosure.getPicture()==null){
            return EnclosureDto.builder()
                .id(enclosure.getId())
                .name(enclosure.getName())
                .description(enclosure.getDescription())
                .publicInfo(enclosure.getPublicInfo())
                .picture(null)
                .build();
        }

        return EnclosureDto.builder()
            .id(enclosure.getId())
            .name(enclosure.getName())
            .description(enclosure.getDescription())
            .publicInfo(enclosure.getPublicInfo())
            .picture(Arrays.toString(enclosure.getPicture()))
            .build();
    }

    public Enclosure enclosureDtoToEnclosure(EnclosureDto enclosureDto) {
        if(enclosureDto == null) {
            return null;
        }

        if(enclosureDto.getPicture()==null){
            return Enclosure.builder()
                .id(enclosureDto.getId())
                .name(enclosureDto.getName())
                .description(enclosureDto.getDescription())
                .publicInfo(enclosureDto.getPublicInfo())
                .picture(null)
                .build();
        }

        return Enclosure.builder()
            .id(enclosureDto.getId())
            .name(enclosureDto.getName())
            .description(enclosureDto.getDescription())
            .publicInfo(enclosureDto.getPublicInfo())
            .picture(enclosureDto.getPicture().getBytes())
            .build();
    }
}
