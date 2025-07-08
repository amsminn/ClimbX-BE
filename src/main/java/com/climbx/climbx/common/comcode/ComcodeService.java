package com.climbx.climbx.common.comcode;

import com.climbx.climbx.common.comcode.dto.ComcodeDto;
import com.climbx.climbx.common.comcode.entity.ComcodeEntity;
import com.climbx.climbx.common.comcode.repository.ComcodeRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ComcodeService {

    private final ComcodeRepository comcodeRepository;
    Map<String, ComcodeDto> comcodes;

    public ComcodeService(ComcodeRepository comcodeRepository) {
        this.comcodeRepository = comcodeRepository;
        this.comcodes = getCodes();
    }

    public Map<String, ComcodeDto> getCodes() {
        return comcodeRepository.findAll()
            .stream()
            .collect(Collectors.toMap(
                ComcodeEntity::code,
                ComcodeDto::from
            ));
    }

    public List<ComcodeDto> getCodesByGroup(String groupCode) {
        return comcodes.values()
            .stream()
            .filter(code -> code.codeGroup().equals(groupCode))
            .collect(Collectors.toList());
    }

    public Optional<ComcodeDto> getCode(String code) {
        return Optional.ofNullable(comcodes.get(code));
    }
}
