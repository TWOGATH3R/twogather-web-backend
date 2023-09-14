package com.twogather.twogatherwebbackend.domain;

import com.twogather.twogatherwebbackend.exception.StoreException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.ALREADY_APPROVED_STORE;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="store_id")
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private StoreOwner owner;

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Image> storeImageList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<BusinessHour> businessHourList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Menu> menuList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Review> reviewList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<StoreKeyword> storeKeywordList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Likes> likesList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Long likeCount;
    private Long reviewCount;
    @Column(precision = 3, scale = 1)
    private Double avgReviewRating;

    @Column(unique = true)
    private String name;
    private String address;
    private String phone;
    @Enumerated(EnumType.STRING)
    private StoreStatus status;
    private String reasonForRejection;
    @Builder.Default
    private LocalDateTime requestDate = LocalDateTime.now();
    private String businessNumber;
    private String businessName;
    private LocalDate businessStartDate;
    @Version
    private Long version;

    public Store(StoreOwner owner, String name, String address, String phone, String businessName, String businessNumber, LocalDate businessStartDate){
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.owner = owner;
        this.status = StoreStatus.PENDING;
        this.reasonForRejection = "";
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
        this.requestDate = LocalDateTime.now();
    }
    public void update(String storeName, String address, String phone, String businessName, String businessNumber, LocalDate businessStartDate) {
        if (storeName != null && !storeName.isEmpty()) {
            this.name = storeName;
        }
        if (address != null && !address.isEmpty()) {
            this.address = address;
        }
        if (phone != null && !phone.isEmpty()) {
            this.phone = phone;
        }
        if (businessName != null && !businessName.isEmpty()) {
            this.businessName = businessName;
        }
        if (businessNumber != null && !businessNumber.isEmpty()) {
            this.businessNumber = businessNumber;
        }
        if (businessStartDate != null) {
            this.businessStartDate = businessStartDate;
        }
    }
    public void setCategory(Category category){
        if(category!=null){
            this.category = category;
        }
    }
    public void addBusinessHour(BusinessHour businessHour){
        if(businessHourList==null){
            businessHourList = new ArrayList<>();
        }
        businessHourList.add(businessHour);
    }
    public void addImage(Image image){
        if(storeImageList==null){
            storeImageList = new ArrayList<>();
        }
        storeImageList.add(image);
    }
    public void addReview(Review review){
        if(reviewList==null){
            reviewList = new ArrayList<>();
        }
        reviewList.add(review);
    }
    public void addLikes(Likes likes){
        if(likesList==null){
            likesList = new ArrayList<>();
        }
        likesList.add(likes);
    }
    public void addMenu(Menu menu){
        if(menuList==null){
            menuList = new ArrayList<>();
        }
        menuList.add(menu);
    }
    public void addStoreKeyword(StoreKeyword storeKeyword){
        if(storeKeywordList==null){
            storeKeywordList = new ArrayList<>();
        }
        storeKeywordList.add(storeKeyword);
    }
    public void reject(String reasonForRejection) {
        this.status = StoreStatus.DENIED;
        this.reasonForRejection = reasonForRejection;
    }
    public void approve(){
        this.status = StoreStatus.APPROVED;
        this.reasonForRejection = "";
    }
    public void reapply(){
        if(this.status.equals(StoreStatus.APPROVED)) throw new StoreException(ALREADY_APPROVED_STORE);
        this.requestDate = LocalDateTime.now();
        this.status = StoreStatus.PENDING;
    }
    public void setDetail(Long likeCount,Long reviewCount ,Double avgReviewRating){
        this.likeCount = likeCount;
        this.reviewCount = reviewCount;
        this.avgReviewRating = Math.round(avgReviewRating * 10) / 10.0;
    }
    public void increaseVersion(){
        version++;
    }

}
