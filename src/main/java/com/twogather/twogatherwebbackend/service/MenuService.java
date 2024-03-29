package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Menu;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.menu.MenuResponse;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveInfo;
import com.twogather.twogatherwebbackend.dto.menu.MenuUpdateInfo;
import com.twogather.twogatherwebbackend.exception.MenuException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.MenuRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.MenuException.MenuErrorCode.*;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    @Value("${menu.max.size}")
    private Integer MenuMaxSize;

    public List<MenuResponse> saveList(Long storeId, List<MenuSaveInfo> requestList) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreException(NO_SUCH_STORE)
        );
        if(store.getMenuList().size() + requestList.size() > MenuMaxSize){
            throw new MenuException(MAX_MENU_SIZE);
        }
        for (MenuSaveInfo menu: requestList){
            boolean isExist = menuRepository.existsByStoreIdAndName(storeId, menu.getName());
            if(isExist) throw new MenuException(DUPLICATE_NAME);
        }
        List<Menu> menuList = toMenuEntity(requestList, store);
        List<Menu> savedMenuList = menuRepository.saveAll(menuList);
        return toResponseList(savedMenuList);
    }

    public List<MenuResponse> updateList(Long storeId, List<MenuUpdateInfo> menuList) {
        storeRepository.findActiveStoreById(storeId).orElseThrow(
                () -> new StoreException(NO_SUCH_STORE)
        );
        List<Menu> savedMenuList = new ArrayList<>();
        for(MenuUpdateInfo request: menuList){
            Menu menu = menuRepository.findByStoreIdAndMenuId(storeId,request.getMenuId()).orElseThrow(
                    () -> new MenuException(NO_SUCH_MENU)
            );
            if(!request.getName().equals(menu.getName())){
                boolean isExist = menuRepository.existsByStoreIdAndName(storeId, request.getName());
                if(isExist) throw new MenuException(DUPLICATE_NAME);
            }
            Menu updatedMenu = menu.update(request.getName(), request.getPrice());
            savedMenuList.add(updatedMenu);
        }

        return toResponseList(savedMenuList);

    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findMenusByStoreId(Long storeId) {
        List<Menu> menuList = menuRepository.findByStoreId(storeId);
        return toResponseList(menuList);
    }

    public void deleteMenuByStoreIdAndMenuId(Long storeId, List<Long> menuIdList) {
        for (Long menuId : menuIdList) {
            Menu menu = menuRepository.findByStoreIdAndMenuId(storeId, menuId)
                    .orElseThrow(() -> new MenuException(NO_SUCH_MENU));
            menuRepository.delete(menu);
        }
    }

    private List<Menu> toMenuEntity(List<MenuSaveInfo> requestList, Store store) {
        List<Menu> menuList = new ArrayList<>();
        for (MenuSaveInfo request: requestList){
            menuList.add(new Menu(store, request.getName(), request.getPrice()));
        }
        return menuList;
    }
    private List<MenuResponse> toResponseList(List<Menu> menuList){
        List<MenuResponse> responseList = new ArrayList<>();
        for (Menu menu: menuList){
            responseList.add(new MenuResponse(menu.getMenuId(), menu.getName(), menu.getPrice()));
        }
        return responseList;
    }

}
