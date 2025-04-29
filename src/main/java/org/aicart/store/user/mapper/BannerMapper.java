package org.aicart.store.user.mapper;

import org.aicart.media.entity.FileStorage;
import org.aicart.store.user.dto.BannerDto;
import org.aicart.store.user.entity.Banner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BannerMapper {

    public static BannerDto toDto(Banner banner) {
        if (banner == null) return null;

        FileStorage background = banner.background;
        FileStorage poster = banner.poster;

        BannerDto dto = new BannerDto();
        dto.id = banner.id;
        dto.shopId = banner.shop.id;
        dto.tag = banner.tag;
        dto.title = banner.title;
        dto.description = banner.description;
        dto.backgroundType = banner.backgroundType;
        dto.backgroundId = background != null ? background.id : null;
        dto.posterId = poster != null ? poster.id : null;
        dto.buttons = banner.buttons;
        dto.sortOrder = banner.sortOrder;
        dto.active = banner.active;
        dto.startDate = banner.startDate;
        dto.endDate = banner.endDate;
        dto.createdAt = banner.createdAt;
        dto.updatedAt = banner.updatedAt;

        return dto;
    }

    public static void updateEntity(Banner banner, BannerDto dto) {
        banner.tag = dto.tag;
        banner.title = dto.title;
        banner.description = dto.description;
        banner.backgroundType = dto.backgroundType;
        banner.sortOrder = dto.sortOrder != null ? dto.sortOrder : 0;
        banner.active = dto.active != null ? dto.active : true;
        banner.startDate = dto.startDate;
        banner.endDate = dto.endDate;
        banner.buttons = dto.buttons;
        banner.updatedAt = LocalDateTime.now();


        if (dto.backgroundId != null || dto.posterId != null) {
            List<Long> fileIds = Stream.of(dto.backgroundId, dto.posterId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            Map<Long, FileStorage> files = FileStorage.<FileStorage>find("id IN ?1", fileIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(file -> file.id, file -> file));

            banner.background = dto.backgroundId != null ? files.get(dto.backgroundId) : null;
            banner.poster = dto.posterId != null ? files.get(dto.posterId) : null;
        } else {
            banner.background = null;
            banner.poster = null;
        }
    }
}
