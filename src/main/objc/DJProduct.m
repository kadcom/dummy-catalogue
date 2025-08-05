#import "DJProduct.h"

@implementation DJProduct

- (instancetype)initWithDictionary:(NSDictionary *)dictionary {
    self = [super init];
    if (self) {
        _productId = [dictionary[@"id"] integerValue];
        _title = [dictionary[@"title"] copy];
        _productDescription = [dictionary[@"description"] copy];
        _category = [dictionary[@"category"] copy];
        _price = [dictionary[@"price"] doubleValue];
        _discountPercentage = [dictionary[@"discountPercentage"] doubleValue];
        _rating = [dictionary[@"rating"] doubleValue];
        _stock = [dictionary[@"stock"] integerValue];
        _tags = [dictionary[@"tags"] copy];
        _brand = [dictionary[@"brand"] copy];
        _sku = [dictionary[@"sku"] copy];
        _weight = [dictionary[@"weight"] doubleValue];
        
        NSDictionary *dimensionsDict = dictionary[@"dimensions"];
        if (dimensionsDict && [dimensionsDict isKindOfClass:[NSDictionary class]]) {
            _dimensions = [[DJDimensions alloc] initWithDictionary:dimensionsDict];
        }
        
        _warrantyInformation = [dictionary[@"warrantyInformation"] copy];
        _shippingInformation = [dictionary[@"shippingInformation"] copy];
        _availabilityStatus = [dictionary[@"availabilityStatus"] copy];
        
        NSArray *reviewsArray = dictionary[@"reviews"];
        if (reviewsArray && [reviewsArray isKindOfClass:[NSArray class]]) {
            NSMutableArray *reviews = [NSMutableArray arrayWithCapacity:reviewsArray.count];
            for (NSDictionary *reviewDict in reviewsArray) {
                if ([reviewDict isKindOfClass:[NSDictionary class]]) {
                    DJReview *review = [[DJReview alloc] initWithDictionary:reviewDict];
                    [reviews addObject:review];
                }
            }
            _reviews = [reviews copy];
        }
        
        _returnPolicy = [dictionary[@"returnPolicy"] copy];
        _minimumOrderQuantity = [dictionary[@"minimumOrderQuantity"] integerValue];
        _images = [dictionary[@"images"] copy];
        _thumbnail = [dictionary[@"thumbnail"] copy];
    }
    return self;
}

- (double)discountedPrice {
    return self.price * (1.0 - self.discountPercentage / 100.0);
}

- (BOOL)isInStock {
    return self.stock > 0;
}

- (NSString *)displayPrice {
    return [NSString stringWithFormat:@"$%.2f", self.price];
}

@end

@implementation DJDimensions

- (instancetype)initWithDictionary:(NSDictionary *)dictionary {
    self = [super init];
    if (self) {
        _width = [dictionary[@"width"] doubleValue];
        _height = [dictionary[@"height"] doubleValue];
        _depth = [dictionary[@"depth"] doubleValue];
    }
    return self;
}

@end

@implementation DJReview

- (instancetype)initWithDictionary:(NSDictionary *)dictionary {
    self = [super init];
    if (self) {
        _rating = [dictionary[@"rating"] integerValue];
        _comment = [dictionary[@"comment"] copy];
        _reviewerName = [dictionary[@"reviewerName"] copy];
        _reviewerEmail = [dictionary[@"reviewerEmail"] copy];
        
        NSString *dateString = dictionary[@"date"];
        if (dateString) {
            NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
            formatter.dateFormat = @"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            formatter.timeZone = [NSTimeZone timeZoneWithAbbreviation:@"UTC"];
            _date = [formatter dateFromString:dateString];
        }
    }
    return self;
}

@end