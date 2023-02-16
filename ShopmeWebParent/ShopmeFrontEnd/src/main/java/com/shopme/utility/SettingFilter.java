package com.shopme.utility;

import com.shopme.common.entity.Setting;
import com.shopme.setting.SettingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class SettingFilter implements Filter {

    private final SettingService settingService;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String url = request.getRequestURL().toString();

        if(url.endsWith(".js") || url.endsWith(".css") || url.endsWith(".png") || url.endsWith(".jpeg")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        List<Setting> currencyAndGeneralSettingList = settingService.getCurrencyAndGeneralSetting();
        for(Setting setting : currencyAndGeneralSettingList) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
