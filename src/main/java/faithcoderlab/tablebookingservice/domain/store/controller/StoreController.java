package faithcoderlab.tablebookingservice.domain.store.controller;

import faithcoderlab.tablebookingservice.domain.store.dto.StoreDto;
import faithcoderlab.tablebookingservice.domain.store.service.StoreService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 매장 컨트롤러 클래스
 * 매장 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 매장 등록 API (파트너 전용)
     *
     * @param partnerId 파트너 ID
     * @param request   매장 등록 요청 정보
     * @return 등록된 매장 정보 응답
     */
    @PostMapping("partners/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<StoreDto.CreateResponse>> createStore(
            @PathVariable Long partnerId,
            @Valid @RequestBody StoreDto.CreateRequest request
    ) {
        authenticationUtil.validatePartnerOwnership(partnerId);

        StoreDto.CreateResponse response = storeService.createStore(partnerId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("매장이  성공적으로 등록되었습니다.", response));
    }

    /**
     * 파트너별 매장 목록 조회 API
     *
     * @param partnerId 파트너 ID
     * @return 매장 목록 응답
     */
    @GetMapping("/partners/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<List<StoreDto.StoreInfoResponse>>> getStoresByPartnerId(
            @PathVariable Long partnerId
    ) {
        authenticationUtil.validatePartnerOwnership(partnerId);

        List<StoreDto.StoreInfoResponse> response = storeService.getStoresByPartnerId(partnerId);

        return ResponseEntity.ok(ApiResponse.success("매장 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 매장 상세 정보 조회 API
     *
     * @param storeId 매장 ID
     * @return 매장 상세 정보 응답
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreDto.StoreInfoResponse>> getSoreInfo(
            @PathVariable Long storeId
    ) {
        StoreDto.StoreInfoResponse response = storeService.getStoreInfo(storeId);

        return ResponseEntity.ok(ApiResponse.success("매장 정보를 성공적으로 조회했습니다.", response));
    }

    /**
     * 매장 정보 수정 API (파트너 전용)
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     * @param request   매장 수정 요청 정보
     * @return 수정된 매장 정보 응답
     */
    @PutMapping("/{storeId}/partners/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<StoreDto.UpdateResponse>> updateStore(
            @PathVariable Long storeId,
            @PathVariable Long partnerId,
            @Valid @RequestBody StoreDto.UpdateRequest request
    ) {
        authenticationUtil.validatePartnerOwnership(partnerId);

        StoreDto.UpdateResponse response = storeService.updateStore(storeId, partnerId, request);

        return ResponseEntity.ok(ApiResponse.success("매장 정보가 성공적으로 수정되었습니다.", response));
    }

    /**
     * 매장 삭제 API (파트너 전용)
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     * @return 삭제 결과 응답
     */
    @DeleteMapping("/{storeId}/partners/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
            @PathVariable Long storeId,
            @PathVariable Long partnerId
    ) {
        authenticationUtil.validatePartnerOwnership(partnerId);

        storeService.deleteStore(storeId, partnerId);

        return ResponseEntity.ok(ApiResponse.success("매장이 성공적으로 삭제되었습니다.", null));
    }

    /**
     * 매장 목록 조회 API (정렬 기준 적용)
     *
     * @param sortBy 정렬 기준 (name, distance)
     * @param lat    사용자 위치 위도 (거리순 정렬 시 필요)
     * @param lng    사용자 위치 경도 (거리순 정렬 시 필요)
     * @return 정렬된 매장 목록 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreDto.StoreInfoResponse>>> getAllStores(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        List<StoreDto.StoreInfoResponse> response = storeService.getAllStores(sortBy, lat, lng);

        return ResponseEntity.ok(ApiResponse.success("매장 목록을 성공적으로 조회했습니다.", response));
    }
}
