#import <Foundation/Foundation.h>

@class DJProduct, DJUser, DJCart;

NS_ASSUME_NONNULL_BEGIN

typedef void (^DJProductCompletionHandler)(DJProduct * _Nullable product, NSError * _Nullable error);
typedef void (^DJProductsCompletionHandler)(NSArray<DJProduct *> * _Nullable products, NSError * _Nullable error);

@interface DummyJSONClient : NSObject

@property (nonatomic, readonly, strong) NSURLSession *session;

- (instancetype)initWithSession:(NSURLSession * _Nullable)session;
- (instancetype)init;

- (void)getProduct:(NSInteger)productId completion:(DJProductCompletionHandler)completion;
- (void)getProductsWithLimit:(NSInteger)limit skip:(NSInteger)skip completion:(DJProductsCompletionHandler)completion;
- (void)getAllProductsWithCompletion:(DJProductsCompletionHandler)completion;
- (void)searchProducts:(NSString *)query completion:(DJProductsCompletionHandler)completion;

@end

NS_ASSUME_NONNULL_END