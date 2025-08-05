#import <Foundation/Foundation.h>

@class DJDimensions, DJReview;

NS_ASSUME_NONNULL_BEGIN

@interface DJProduct : NSObject

@property (nonatomic, readonly) NSInteger productId;
@property (nonatomic, readonly, copy) NSString *title;
@property (nonatomic, readonly, copy) NSString *productDescription;
@property (nonatomic, readonly, copy) NSString *category;
@property (nonatomic, readonly) double price;
@property (nonatomic, readonly) double discountPercentage;
@property (nonatomic, readonly) double rating;
@property (nonatomic, readonly) NSInteger stock;
@property (nonatomic, readonly, copy) NSArray<NSString *> *tags;
@property (nonatomic, readonly, copy) NSString *brand;
@property (nonatomic, readonly, copy) NSString *sku;
@property (nonatomic, readonly) double weight;
@property (nonatomic, readonly) DJDimensions *dimensions;
@property (nonatomic, readonly, copy) NSString *warrantyInformation;
@property (nonatomic, readonly, copy) NSString *shippingInformation;
@property (nonatomic, readonly, copy) NSString *availabilityStatus;
@property (nonatomic, readonly, copy) NSArray<DJReview *> *reviews;
@property (nonatomic, readonly, copy) NSString *returnPolicy;
@property (nonatomic, readonly) NSInteger minimumOrderQuantity;
@property (nonatomic, readonly, copy) NSArray<NSString *> *images;
@property (nonatomic, readonly, copy) NSString *thumbnail;

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
- (double)discountedPrice;
- (BOOL)isInStock;
- (NSString *)displayPrice;

@end

@interface DJDimensions : NSObject
@property (nonatomic, readonly) double width;
@property (nonatomic, readonly) double height;
@property (nonatomic, readonly) double depth;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end

@interface DJReview : NSObject
@property (nonatomic, readonly) NSInteger rating;
@property (nonatomic, readonly, copy) NSString *comment;
@property (nonatomic, readonly, copy) NSString *reviewerName;
@property (nonatomic, readonly, copy) NSString *reviewerEmail;
@property (nonatomic, readonly, copy) NSDate *date;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end

NS_ASSUME_NONNULL_END