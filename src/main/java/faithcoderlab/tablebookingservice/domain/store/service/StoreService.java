package faithcoderlab.tablebookingservice.domain.store.service;

import faithcoderlab.tablebookingservice.domain.partner.entity.Partner;
import faithcoderlab.tablebookingservice.domain.partner.repository.PartnerRepository;
import faithcoderlab.tablebookingservice.domain.store.dto.StoreDto;
import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import faithcoderlab.tablebookingservice.domain.store.repository.StoreRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 매장 서비스 클래스
 * 매장 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final PartnerRepository partnerRepository;

    /**
     * 매장 등록 메서드
     *
     * @param partnerId 파트너 ID
     * @param request 매장 등록 요청 정보
     * @return 등록된 매장 정보
     */
    @Transactional
    public StoreDto.CreateResponse createStore(Long partnerId, StoreDto.CreateRequest request) {
        if (storeRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.STORE_NAME_ALREADY_EXISTS);
        }

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = Store.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .phoneNumber(request.getPhoneNumber())
                .businessHours(request.getBusinessHours())
                .partner(partner)
                .active(true)
                .build();

        Store savedStore = storeRepository.save(store);

        return StoreDto.CreateResponse.builder()
                .storeId(savedStore.getId())
                .name(savedStore.getName())
                .address(savedStore.getAddress())
                .description(savedStore.getDescription())
                .phoneNumber(savedStore.getPhoneNumber())
                .businessHours(savedStore.getBusinessHours())
                .partnerId(partner.getId())
                .createdAt(savedStore.getCreatedAt())
                .build();
    }

    /**
     * 파트너별 매장 목록 조회 메서드
     *
     * @param partnerId 파트너 ID
     * @return 매장 목록
     */
    @Transactional(readOnly = true)
    public List<StoreDto.StoreInfoResponse> getStoresByPartnerId(Long partnerId) {
        if (!partnerRepository.existsById(partnerId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<Store> stores = storeRepository.findByPartnerId(partnerId);

        return stores.stream()
                .map(this::convertToStoreInfoResponse)
                .collect(Collectors.toList());
    }

    /**
     * 매장 상세 정보 조회 메서드
     *
     * @param storeId 매장 ID
     * @return 매장 상세 정보
     */
    @Transactional(readOnly = true)
    public StoreDto.StoreInfoResponse getStoreInfo(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return convertToStoreInfoResponse(store);
    }

    /**
     * 매장 정보 수정 메서드
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     * @param request   매장 수정 요청 정보
     * @return 수정된 매장 정보
     */
    @Transactional
    public StoreDto.UpdateResponse updateStore(Long storeId, Long partnerId, StoreDto.UpdateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getPartner().getId().equals(partnerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (!store.getName().equals(request.getName()) && storeRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.STORE_NAME_ALREADY_EXISTS);
        }

        store.setName(request.getName());
        store.setAddress(request.getAddress());
        store.setDescription(request.getDescription());
        store.setPhoneNumber(request.getPhoneNumber());
        store.setBusinessHours(request.getBusinessHours());

        Store updatedStore = storeRepository.save(store);

        return StoreDto.UpdateResponse.builder()
                .storeId(updatedStore.getId())
                .name(updatedStore.getName())
                .address(updatedStore.getAddress())
                .description(updatedStore.getDescription())
                .phoneNumber(updatedStore.getPhoneNumber())
                .businessHours(updatedStore.getBusinessHours())
                .partnerId(updatedStore.getPartner().getId())
                .updatedAt(updatedStore.getUpdatedAt())
                .build();
    }

    /**
     * 매장 삭제 메서드 (soft delete)
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     */
    @Transactional
    public void deleteStore(Long storeId, Long partnerId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getPartner().getId().equals(partnerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        store.setActive(false);
        storeRepository.save(store);
    }

    /**
     * Store 엔티티를 StoreInfoResponse DTO로 변환
     *
     * @param store Store 엔티티
     * @return StoreInfoResponse DTO
     */
    private StoreDto.StoreInfoResponse convertToStoreInfoResponse(Store store) {
        return StoreDto.StoreInfoResponse.builder()
                .storeId(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .phoneNumber(store.getPhoneNumber())
                .businessHours(store.getBusinessHours())
                .partnerId(store.getPartner().getId())
                .partnerName(store.getPartner().getName())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .build();
    }
}
