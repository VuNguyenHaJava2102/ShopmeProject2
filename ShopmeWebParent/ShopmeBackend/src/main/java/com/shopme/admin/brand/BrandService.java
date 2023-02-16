package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BrandService {

    public static final int NUMBER_OF_BRANDS_PER_PAGE = 10;

    private final BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAllBrands();
    }

    public Page<Brand> getBrandsByPage(int pageNum,
                                       String sortDir,
                                       String keyword) {
        Sort sort = Sort.by("name");
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_BRANDS_PER_PAGE, sort);

        if(!keyword.equals("")) {
            return brandRepository.searchBrand(keyword, pageable);
        }
        return brandRepository.findAll(pageable);
    }

    public boolean checkNameDuplicated(Integer id, String name) {
        if(id == null) return brandRepository.existsByName(name);

        Optional<Brand> optionalBrand = brandRepository.findByName(name);

        if(optionalBrand.isEmpty()) return false;

        if(optionalBrand.get().getId() == id) return false;

        return true;
    }

    public Brand getBrandById(int id) throws BrandNotFoundException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if(optionalBrand.isEmpty()) {
            throw new BrandNotFoundException("Could not find any brand ID " + id);
        }
        return optionalBrand.get();
    }

    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public void deleteBrand(int id) throws BrandNotFoundException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if(optionalBrand.isEmpty()) {
            throw new BrandNotFoundException("Could not find any brand ID " + id);
        }
        brandRepository.delete(optionalBrand.get());
    }

}
