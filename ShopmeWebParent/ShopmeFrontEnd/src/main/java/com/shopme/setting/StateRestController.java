package com.shopme.setting;

import com.shopme.common.dto.StateDto;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class StateRestController {

    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    @GetMapping("/settings/get-all-states/{countryId}")
    public List<StateDto> getAllStatesByCountryOrderByName(@PathVariable("countryId") int id) {
        List<StateDto> expectedList = new ArrayList<>();
        Country country = countryRepository.findById(id).get();
        List<State> stateList = stateRepository.findAllByCountryOrderByName(country);

        stateList.forEach(s -> {
            StateDto stateDto = new StateDto(s.getId(), s.getName());
            expectedList.add(stateDto);
        });
        return expectedList;
    }
}
