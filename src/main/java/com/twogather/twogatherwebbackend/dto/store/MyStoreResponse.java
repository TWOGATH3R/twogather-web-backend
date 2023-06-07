package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyStoreResponse extends StoreBaseResponse {
    private String phone;
    private Boolean isApproved;
    private String reasonForRejection;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
    private String storeImageUrl;

    public MyStoreResponse(Long storeId, String name, String address, String phone, Boolean isApproved, String reasonForRejection, LocalDate requestDate, String storeImageUrl){
        super(storeId,name,address);
        this.phone = phone;
        this.storeImageUrl = storeImageUrl;
        this.isApproved = isApproved;
        this.reasonForRejection = reasonForRejection;
        this.requestDate = requestDate;
    }
    public static class Builder {
        private Long storeId;
        private String name;
        private String address;
        private String phone;
        private Boolean isApproved;
        private String reasonForRejection;
        private LocalDate requestDate;
        private String storeImageUrl;

        public Builder storeId(Long storeId) {
            this.storeId = storeId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder isApproved(Boolean isApproved) {
            this.isApproved = isApproved;
            return this;
        }

        public Builder reasonForRejection(String reasonForRejection) {
            this.reasonForRejection = reasonForRejection;
            return this;
        }

        public Builder requestDate(LocalDate requestDate) {
            this.requestDate = requestDate;
            return this;
        }

        public Builder storeImageUrl(String storeImageUrl) {
            this.storeImageUrl = storeImageUrl;
            return this;
        }

        public MyStoreResponse build() {
            return new MyStoreResponse(storeId, name, address, phone, isApproved, reasonForRejection, requestDate, storeImageUrl);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
