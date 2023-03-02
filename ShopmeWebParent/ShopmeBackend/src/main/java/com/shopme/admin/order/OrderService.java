package com.shopme.admin.order;

import com.shopme.admin.setting.SettingRepository;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.Setting;
import com.shopme.common.enums.OrderStatus;
import com.shopme.common.enums.SettingCategory;
import com.shopme.common.exception.OrderNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    public static final int NUMBER_OF_ORDER_PER_PAGE = 10;

    private final OrderRepository orderRepository;
    private final SettingRepository settingRepository;
    private final CountryRepository countryRepository;

    // support service-function
    // 1
    public List<Setting> getAllCurrencySetting() {
        return settingRepository.findByCategory(SettingCategory.CURRENCY);
    }

    // 2
    public List<Country> getAllCountries() {
        return countryRepository.findAllByOrderByName();
    }

    // main service-function
    // 1
    public Page<Order> getOrdersByKeywordAndPage(String sortField,
                                                 String sortDir,
                                                 String keyword,
                                                 int pageNum) {
        Sort sort = null;
        if(sortField.equals("destination")) {
            sort = Sort.by("city").and(Sort.by("state")).and(Sort.by("country"));
        } else {
            sort = Sort.by(sortField);
        }
        if(sortField.equals("orderTime")) {
            sort = sortDir.equals("desc") ? sort.ascending() : sort.descending();
        } else {
            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_ORDER_PER_PAGE, sort);

        if(keyword.equals("")) {
            return orderRepository.findAll(pageable);
        } else if(keyword.startsWith("for-shipper")){
            if(keyword.contains("#")) {
                return orderRepository.searchForShipperById(Integer.valueOf(keyword.substring(12)), pageable);
            }
            return orderRepository.searchForShipperByName(keyword.substring(11), pageable);
        }
        return orderRepository.findByKeywordAndPage(keyword, pageable);
    }

    // 2
    public Order getOrderById(int id) throws OrderNotFoundException {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if(orderOptional.isEmpty()) {
            throw new OrderNotFoundException("Could not find any orders with ID " + id);
        }
        return orderOptional.get();
    }

    // 3
    public void updateOrderStatus(int orderId, String status) {
        /*
        - Hóa đơn có nhiều trạng thái theo thời gian khác nhau
        - Vì vậy trạng thái hiện tại ở hóa đơn sẽ là trạng thái của OrderTrack gần nhất
        */
        Order orderInDB = orderRepository.findById(orderId).get();
        OrderStatus statusToUpdate = OrderStatus.valueOf(status);

        if(!orderInDB.hasStatus(statusToUpdate)) {
            OrderTrack newTrack = new OrderTrack();

            newTrack.setNotes(statusToUpdate.getDefaultDescription());
            newTrack.setUpdatedTime(new Date());
            newTrack.setStatus(statusToUpdate);
            newTrack.setOrder(orderInDB);

            orderInDB.getOrderTracks().add(newTrack);
            orderInDB.setStatus(statusToUpdate);
            orderRepository.save(orderInDB);
        }
    }

}
